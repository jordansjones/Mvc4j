package nextmethod.web.razor.editor.internal;

import nextmethod.annotations.Internal;
import nextmethod.base.IDisposable;
import nextmethod.base.IEventHandler;
import nextmethod.threading.CancellationToken;
import nextmethod.web.razor.DocumentParseCompleteEventArgs;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.editor.EditResult;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.TextChange;

import javax.annotation.Nonnull;

@Internal
public class BackgroundParser implements IDisposable {

	private IEventHandler<DocumentParseCompleteEventArgs> resultsReadyHandler;
	private MainThreadState main;
	private BackgroundThread bg;

	public BackgroundParser(final RazorEngineHost host, final String fileName) {
		this.main = new MainThreadState(fileName);
		this.bg = new BackgroundThread(main, host, fileName);

		this.main.setResultsReadyHandler((sender, e) -> onResultsReady(e));
	}

	public boolean isIdle() {
		return main.isIdle();
	}

	public void start() {
		bg.start();
	}

	public void cancel() {
		main.cancel();
	}

	public void queueChange(final TextChange change) {
		main.queueChange(change);
	}

	@Override
	public void close() {
		main.close();
	}

	public IDisposable synchronizeMainThreadState() {
		return main.lock();
	}

	protected void onResultsReady(final DocumentParseCompleteEventArgs args) {
		if (resultsReadyHandler != null) {
			resultsReadyHandler.handleEvent(this, args);
		}
	}

	static boolean treesAreDifferent(final Block leftTree, final Block rightTree, final Iterable<TextChange> changes) {
		return treesAreDifferent(leftTree, rightTree, changes, CancellationToken.none());
	}

	static boolean treesAreDifferent(final Block leftTree, final Block rightTree, final Iterable<TextChange> changes, final CancellationToken cancelToken) {
		// Apply all pending changes to the original tree
		for (TextChange change : changes) {
			cancelToken.throwIfCancellationRequested();
			final Span changeOwner = leftTree.locateOwner(change);

			// Apply the change to the tree
			if (changeOwner == null) {
				return true;
			}
			final EditResult editResult = changeOwner.getEditHandler().applyChange(changeOwner, change, true);
			changeOwner.replaceWith(editResult.getEditedSpan());
		}

		// Now compare the trees
		final boolean treesAreDifferent = !leftTree.equivalentTo(rightTree);
		return treesAreDifferent;
	}

	public void setResultsReadyHandler(final IEventHandler<DocumentParseCompleteEventArgs> resultsReadyHandler) {
		this.resultsReadyHandler = resultsReadyHandler;
	}
}
