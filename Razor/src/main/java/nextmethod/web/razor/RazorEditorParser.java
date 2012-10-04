package nextmethod.web.razor;

import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import nextmethod.base.Debug;
import nextmethod.base.IDisposable;
import nextmethod.web.razor.editor.internal.BackgroundParser;
import nextmethod.web.razor.editor.internal.RazorEditorTrace;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.TextChange;

import javax.annotation.Nonnull;

import java.nio.file.FileSystems;
import java.nio.file.Path;

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
// TODO
public class RazorEditorParser implements IDisposable {

	private static final String DebugArg = "EDITOR_TRACING";

	// Lock for this document
	private Span lastChangeOwner;
	private Span lastAutoCompleteSpan;
	private BackgroundParser parser;
	private Block currentParseTree;

	private String fileName;

	public PartialParseResult checkForStructureChanges(@Nonnull final TextChange change) {
		// Validate the change
		Stopwatch sw = null;
		if (Debug.isDebugArgPresent(DebugArg)) {
			sw = new Stopwatch().start();
		}

		PartialParseResult result = PartialParseResult.Rejected;

		String changeString = "";
		// ...

		if (sw != null) {
			sw.stop();
		}

		RazorEditorTrace.traceLine(
			RazorResources().getString("trace.editorProcessedChange"),
			FileSystems.getDefault().getPath(fileName).getFileName().toString(),
			changeString,
			sw != null ? sw : "?",
			result.toString()
		);

		return result;
	}


	@Override
	public void close() {
		parser.close();
	}

}
