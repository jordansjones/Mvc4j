package nextmethod.web.razor.editor.internal;

import javafx.event.EventHandler;
import nextmethod.base.IDisposable;
import nextmethod.base.IEventHandler;
import nextmethod.base.NotImplementedException;
import nextmethod.threading.CancellationToken;
import nextmethod.web.razor.DocumentParseCompleteEventArgs;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.text.TextChange;

// TODO
public class BackgroundParser implements IDisposable {

	private IEventHandler<DocumentParseCompleteEventArgs> resultsReadyHandler;

	public BackgroundParser(final RazorEngineHost host, final String fileName) {

	}

	public boolean isIdle() {
		throw new NotImplementedException();
	}

	public void start() {
		throw new NotImplementedException();
	}

	public void cancel() {
		throw new NotImplementedException();
	}

	public void queueChange(final TextChange change) {
		throw new NotImplementedException();
	}

	@Override
	public void close() {
	}

	public IDisposable synchronizeMainThreadState() {
		throw new NotImplementedException();
	}

	protected void onResultsReady(final DocumentParseCompleteEventArgs args) {
		throw new NotImplementedException();
	}

	static boolean treesAreDifferent(final Block leftTree, final Block rightTree, final Iterable<TextChange> changes) {
		throw new NotImplementedException();
	}

	static boolean treesAreDifferent(final Block leftTree, final Block rightTree, final Iterable<TextChange> changes, final CancellationToken cancelToken) {
		throw new NotImplementedException();
	}

	public void setResultsReadyHandler(final IEventHandler<DocumentParseCompleteEventArgs> resultsReadyHandler) {
		this.resultsReadyHandler = resultsReadyHandler;
	}
}
