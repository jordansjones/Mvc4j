/*
 * Copyright 2013 Jordan S. Jones <jordansjones@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextmethod.web.razor;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import nextmethod.base.Debug;
import nextmethod.base.IDisposable;
import nextmethod.base.IEventHandler;
import nextmethod.base.Strings;
import nextmethod.web.razor.editor.AutoCompleteEditHandler;
import nextmethod.web.razor.editor.EditResult;
import nextmethod.web.razor.editor.internal.BackgroundParser;
import nextmethod.web.razor.editor.internal.RazorEditorTrace;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.TextChange;

import javax.annotation.Nonnull;
import java.nio.file.FileSystems;
import java.util.EnumSet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.common.Mvc4jCommonResources.CommonResources;
import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 * Parser used by editors to avoid resparsing the entire document on each text change
 *
 * <p>
 *     This parser is designed to allow editors to avoid having to worry about incremental parsing.
 *     The {@link #checkForStructureChanges} method can be called with every change made by a user in an editor
 *     and the parser will provide a result indicating if it was able to incrementally reparse the document.
 * </p>
 * <p>
 *     The general workflow for editors with this parser is:
 *     <ol>
 *         <li>User edits document</li>
 *         <li>Editor builds TextChange structure describing the edit and providing a reference to the _updated_ text buffer</li>
 *         <li>Editor calls {@link #checkForStructureChanges} passing in that change.</li>
 *         <li>
 *             Parser determins if the change can be simply applied to an existing parse tree node
 *             <ol>
 *                 <li>If it can, the Parser updates its parse tree and returns PartialParseResult.Accepted</li>
 *                 <li>If it can not, the Parser starts a background parse task and return PartialParseResult.Rejected</li>
 *             </ol>
 *         </li>
 *         NOTE: Additional flags can be applied to the PartialParseResult, see the enum for more details. However,
 *         the Accepted or Rejected flags will ALWAYS be present
 *     </ol>
 * </p>
 * <p>
 *     A change can only be incrementally parsed if a single, unique, Span (see System.Web.Razor.Parser.SyntaxTree) in the syntax tree can
 *     be identified as owning the entire change.  For example, if a change overlaps with multiple spans, the change cannot be
 *     parsed incrementally and a full reparse is necessary.  A Span "owns" a change if the change occurs either a) entirely
 *     within it's boundaries or b) it is a pure insertion (see TextChange) at the end of a Span whose CanGrow flag (see Span) is
 *     true.
 * </p>
 * <p>
 *     Even if a single unique Span owner can be identified, it's possible the edit will cause the Span to split or merge with other
 *     Spans, in which case, a full reparse is necessary to identify the extent of the changes to the tree.
 * </p>
 * <p>
 *     When the RazorEditorParser returns Accepted, it updates CurrentParseTree immediately.  However, the editor is expected to
 *     update it's own data structures independently.  It can use CurrentParseTree to do this, as soon as the editor returns from
 *     CheckForStructureChanges, but it should (ideally) have logic for doing so without needing the new tree.
 * </p>
 * <p>
 *     When Rejected is returned by CheckForStructureChanges, a background parse task has _already_ been started.  When that task
 *     finishes, the DocumentStructureChanged event will be fired containing the new generated code, parse tree and a reference to
 *     the original TextChange that caused the reparse, to allow the editor to resolve the new tree against any changes made since
 *     calling CheckForStructureChanges.
 * </p>
 * <p>
 *     If a call to CheckForStructureChanges occurs while a reparse is already in-progress, the reparse is cancelled IMMEDIATELY
 *     and Rejected is returned without attempting to reparse.  This means that if a conusmer calls CheckForStructureChanges, which
 *     returns Rejected, then calls it again before DocumentParseComplete is fired, it will only recieve one DocumentParseComplete
 *     event, for the second change.
 * </p>
 */
public class RazorEditorParser implements IDisposable {

	// Lock for this document
	private Span lastChangeOwner;
	private Span lastAutoCompleteSpan;
	private BackgroundParser parser;
	private volatile Block currentParseTree;

	private final RazorEngineHost host;
	private final String fileName;

	private boolean lastResultProvisional;

	private IEventHandler<DocumentParseCompleteEventArgs> documentParseCompleteHandler;

	public RazorEditorParser(@Nonnull final RazorEngineHost host, @Nonnull final String sourceFileName) {
		this.host = checkNotNull(host, "host");

		checkArgument(Strings.isNullOrEmpty(sourceFileName) == false, CommonResources().argumentCannotBeNullOrEmpty(), "sourceFileName");
		this.fileName = sourceFileName;

		this.parser = new BackgroundParser(this.host, this.fileName);
		this.parser.setResultsReadyHandler((sender, e) -> onDocumentParseComplete(e));
		this.parser.start();
	}

	public RazorEngineHost getHost() {
		return host;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean isLastResultProvisional() {
		return lastResultProvisional;
	}

	public Block getCurrentParseTree() {
		return currentParseTree;
	}

	public String getAutoCompleteString() {
		if (lastAutoCompleteSpan != null) {
			final AutoCompleteEditHandler editHandler = typeAs(lastAutoCompleteSpan.getEditHandler(), AutoCompleteEditHandler.class);
			if (editHandler != null) {
				return editHandler.getAutoCompleteString();
			}
		}
		return null;
	}

	/**
	 * Determines if a change will cause a structural change to the document and if not, applies it to the existing tree.
	 * If a structural change would occur, automatically starts a reparse
	 * <p>
	 *     NOTE: The initial incremental parsing check and actual incremental parsing (if possible) occurs
	 *     on the callers thread. However, if a full reparse is needed, this occurs on a background thread.
	 * </p>
	 *
	 * @param change    The change to apply to the parse tree
	 * @return a PartialParseResult value indicating the result of the incremental parse
	 */
	public EnumSet<PartialParseResult> checkForStructureChanges(@Nonnull final TextChange change) {
		// Validate the change
		Stopwatch sw = null;
		if (Debug.isDebugArgPresent(DebugArgs.EditorTracing)) {
			sw = Stopwatch.createStarted();
		}
		RazorEditorTrace.traceLine(
			RazorResources().traceEditorReceivedChange(
				getFileName(fileName),
				change.toString()
			)
		);
		if (change.getNewBuffer() == null) {
			throw new IllegalArgumentException(
				RazorResources().structureMemberCannotBeNull(
					"Buffer",
					"TextChange"
				)
			);
		}

		EnumSet<PartialParseResult> result = PartialParseResult.setOfRejected();

		// If there isn't already a parse underway, try partial-parsing
		String changeString = Strings.Empty;
		try(IDisposable ignored = parser.synchronizeMainThreadState()) {
			// Capture the string value of the change while we're synchronized
			changeString = change.toString();

			// Check if we can partial-parse
			if (getCurrentParseTree() != null && parser.isIdle()) {
				result = tryPartialParse(change);
			}
		}

		// If partial parsing failed or there were outstanding parser tasks, start a full reparse
		if (result.contains(PartialParseResult.Rejected)) {
			parser.queueChange(change);
		}

		// Otherwise, remember if this was provisionally accepted for next partial parse
		lastResultProvisional = result.contains(PartialParseResult.Provisional);
		verifyFlagsAreValid(result);

		if (sw != null) {
			sw.stop();
		}

		RazorEditorTrace.traceLine(
			RazorResources().traceEditorProcessedChange(
				getFileName(fileName),
				changeString,
				sw != null ? sw.toString() : "?",
				enumSetToString(result)
			)
		);

		return result;
	}


	@Override
	public void close() {
		parser.close();
	}

	private EnumSet<PartialParseResult> tryPartialParse(final TextChange change) {
		EnumSet<PartialParseResult> result = PartialParseResult.setOfRejected();

		// Try the last change owner
		if (lastChangeOwner != null && lastChangeOwner.getEditHandler().ownsChange(lastChangeOwner, change)) {
			final EditResult editResult = lastChangeOwner.getEditHandler().applyChange(lastChangeOwner, change);
			result = editResult.getResults();
			if (!editResult.getResults().contains(PartialParseResult.Rejected)) {
				lastChangeOwner.replaceWith(editResult.getEditedSpan());
			}

			return result;
		}

		// Locate the span responsible for this change
		lastChangeOwner = getCurrentParseTree().locateOwner(change);

		if (lastResultProvisional) {
			// Last change owner couldn't accept this, so we must do a full reparse
			result = PartialParseResult.setOfRejected();
		}
		else if (lastChangeOwner != null) {
			final EditResult editResult = lastChangeOwner.getEditHandler().applyChange(lastChangeOwner, change);
			result = editResult.getResults();
			if (!editResult.getResults().contains(PartialParseResult.Rejected)) {
				lastChangeOwner.replaceWith(editResult.getEditedSpan());
			}
			if (result.contains(PartialParseResult.AutoCompleteBlock)) {
				lastAutoCompleteSpan = lastChangeOwner;
			}
			else {
				lastAutoCompleteSpan = null;
			}
		}
		return result;
	}

	private void onDocumentParseComplete(final DocumentParseCompleteEventArgs args) {
		try(IDisposable ignored = parser.synchronizeMainThreadState()) {
			currentParseTree = args.getGeneratorResults().getDocument();
			lastChangeOwner = null;
		}

		if (Debug.isAssertEnabled()) assert args != null;

		if (documentParseCompleteHandler != null) {
			try {
				documentParseCompleteHandler.handleEvent(this, args);
			}
			catch (Throwable e) {
				// TODO
//				Debug.WriteLine("[RzEd] Document Parse Complete Handler Threw: " + ex.ToString());
			}
		}
	}

	public void setDocumentParseCompleteHandler(final IEventHandler<DocumentParseCompleteEventArgs> documentParseCompleteHandler) {
		this.documentParseCompleteHandler = documentParseCompleteHandler;
	}

	private static void verifyFlagsAreValid(final EnumSet<PartialParseResult> result) {
		if (Debug.isAssertEnabled()) {
			assert result.contains(PartialParseResult.Accepted) || result.contains(PartialParseResult.Rejected) : "Partial Parse result does not have either of Accepted or Rejected";
			assert result.contains(PartialParseResult.Rejected) || !result.contains(PartialParseResult.SpanContextChanged) : "Partial Parse result was Accepted AND had SpanContextChanged";
			assert result.contains(PartialParseResult.Rejected) || !result.contains(PartialParseResult.AutoCompleteBlock) : "Partial Parse result was Accepted AND had AutoCompleteBlock";
			assert result.contains(PartialParseResult.Accepted) || !result.contains(PartialParseResult.Provisional) : "Partial Parse result was Rejected AND had Provisional";
		}
	}

	private static String getFileName(final String fileName) {
		return FileSystems.getDefault().getPath(fileName).getFileName().toString();
	}

	private static String enumSetToString(final EnumSet<PartialParseResult> results) {
		return Joiner.on("; ").skipNulls().join(results);
	}
}
