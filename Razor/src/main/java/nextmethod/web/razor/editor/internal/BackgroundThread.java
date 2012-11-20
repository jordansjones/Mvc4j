package nextmethod.web.razor.editor.internal;

import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import nextmethod.annotations.Internal;
import nextmethod.base.Debug;
import nextmethod.io.Filesystem;
import nextmethod.threading.CancellationToken;
import nextmethod.threading.CancellationTokenSource;
import nextmethod.threading.OperationCanceledException;
import nextmethod.web.razor.DocumentParseCompleteEventArgs;
import nextmethod.web.razor.GeneratorResults;
import nextmethod.web.razor.RazorEditorParser;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.RazorTemplateEngine;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.text.ITextBuffer;
import nextmethod.web.razor.text.TextChange;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

@Internal
final class BackgroundThread extends BaseThreadState {

	private static final String DebugArgCheckTree = "CHECK_TREE";

	private final MainThreadState main;
	private Thread backgroundThread;
	private CancellationToken shutdownToken;
	private final RazorEngineHost host;
	private final String fileName;
	private Block currentParseTree;
	private List<TextChange> previouslyDiscarded = Lists.newArrayList();

	BackgroundThread(@Nonnull final MainThreadState main, @Nonnull final RazorEngineHost host, @Nonnull final String fileName) {
		this.main = checkNotNull(main);
		this.host = checkNotNull(host);
		this.fileName = checkNotNull(fileName);

		this.shutdownToken = main.getCancelToken();
		this.backgroundThread = new Thread(new Runnable() {
			@Override
			public void run() {
				workerLoop();
			}
		});
		this.backgroundThread.setDaemon(true);
		this.backgroundThread.setName(BackgroundParser.class.getSimpleName() + " Thread");

		setThreadId(backgroundThread.getId());
	}

	public void start() {
		backgroundThread.start();
	}

	private void workerLoop() {
		final boolean isEditorTracing = Debug.isDebugArgPresent(RazorEditorParser.DebugArg);
		final String fileNameOnly = Filesystem.getFileName(fileName);

		Stopwatch sw = null;
		if (isEditorTracing) {
			sw = new Stopwatch();
		}

		try {
			RazorEditorTrace.traceLine(RazorResources().traceBackgroundThreadStart(fileNameOnly));
			ensureOnThread();
			while (!shutdownToken.isCancellationRequested()) {
				// Grab the parcel of work to do
				final WorkParcel parcel = main.getParcel();
				if (!parcel.getChanges().isEmpty()) {
					RazorEditorTrace.traceLine(RazorResources().traceChangesArrived(fileNameOnly, String.valueOf(parcel.getChanges().size())));
					try {
						DocumentParseCompleteEventArgs args = null;
						try (CancellationTokenSource linkedCancel = CancellationTokenSource.createLinkedTokenSource(shutdownToken, parcel.getCancelToken())) {
							if (parcel != null && !linkedCancel.isCancellationRequested()) {
								// Collect ALL changes
								if (isEditorTracing && previouslyDiscarded != null && !previouslyDiscarded.isEmpty()) {
									RazorEditorTrace.traceLine(RazorResources().traceCollectedDiscardedChanges(fileNameOnly, String.valueOf(parcel.getChanges().size())));
								}
								final Iterable<TextChange> allChanges = Iterables.<TextChange>concat(
									previouslyDiscarded != null ? previouslyDiscarded : Collections.<TextChange>emptyList(),
									parcel.getChanges()
								);

								final TextChange finalChange = Iterables.<TextChange>getLast(allChanges, null);
								if (finalChange != null) {
									if (isEditorTracing) {
										sw.reset().start();
									}

									final GeneratorResults results = parseChange(finalChange.getNewBuffer(), linkedCancel.getToken());

									if (isEditorTracing) {
										sw.stop();
									}

									RazorEditorTrace.traceLine(RazorResources().traceParseComplete(
										fileNameOnly,
										sw != null ? sw.toString() : "?"
									));

									if (results != null && !linkedCancel.isCancellationRequested()) {
										// Clear discarded changes list
										previouslyDiscarded = Lists.newArrayList();
										// Take the current tree and check for differences
										if (isEditorTracing) {
											sw.reset().start();
										}
										final boolean treeStructureChanged = currentParseTree == null || BackgroundParser.treesAreDifferent(currentParseTree, results.getDocument(), allChanges, parcel.getCancelToken());

										if (isEditorTracing) {
											sw.stop();
										}

										currentParseTree = results.getDocument();
										RazorEditorTrace.traceLine(RazorResources().traceTreesCompared(
											fileNameOnly,
											sw != null ? sw.toString() : "?",
											String.valueOf(treeStructureChanged)
										));

										// Build Arguments
										args = new DocumentParseCompleteEventArgs(treeStructureChanged, results, finalChange);
									}
									else {
										// Parse completed but we were cancelled in the mean time. Add these to the discarded changes set
										RazorEditorTrace.traceLine(RazorResources().traceChangesDiscarded(fileNameOnly, String.valueOf(Iterables.size(allChanges))));
										previouslyDiscarded = Lists.newArrayList(allChanges);
									}

									if (Debug.isDebugArgPresent(DebugArgCheckTree) && args != null) {
										// Rewind the buffer and sanity check the line mappings
										finalChange.getNewBuffer().setPosition(0);
										// TODO
									}
								}
							}
						}
						if (args != null) {
							main.returnParcel(args);
						}
					}
					catch (OperationCanceledException ignored) {

					}
				}
				else {
					RazorEditorTrace.traceLine(RazorResources().traceNoChangesArrived(fileName), parcel.getChanges().size());
					Thread.yield();
				}
			}
		}
		catch (OperationCanceledException ignored) {}
		finally {
			RazorEditorTrace.traceLine(RazorResources().traceBackgroundThreadShutdown(fileNameOnly));
			// Clean up main thread resources
			main.close();
		}
	}

	private GeneratorResults parseChange(@Nonnull final ITextBuffer buffer, @Nonnull final CancellationToken token) {
		ensureOnThread();

		// Create a template engine
		final RazorTemplateEngine engine = new RazorTemplateEngine(host);

		// Seek the buffer to the beginning
		buffer.setPosition(0);

		try {
			return engine.generateCode(
				buffer,
				null,
				null,
				fileName,
				Optional.fromNullable(token)
			);
		}
		catch (OperationCanceledException ignored) {
			return null;
		}
	}
}
