package nextmethod.web.razor.editor.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Monitor;
import nextmethod.annotations.Internal;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.IEventHandler;
import nextmethod.io.Filesystem;
import nextmethod.threading.CancellationToken;
import nextmethod.threading.CancellationTokenSource;
import nextmethod.threading.ManualResetEvent;
import nextmethod.web.razor.DocumentParseCompleteEventArgs;
import nextmethod.web.razor.text.TextChange;
import nextmethod.web.razor.utils.DisposableAction;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

@Internal
final class MainThreadState extends BaseThreadState implements IDisposable {


	private final CancellationTokenSource cancelSource = new CancellationTokenSource();
	private final ManualResetEvent hasParcel = new ManualResetEvent(false);
	private CancellationTokenSource currentParcelCancelSource;

	private String fileName;
	private final Monitor stateLock = new Monitor();
	private List<TextChange> changes = Lists.newArrayList();
	private IEventHandler<DocumentParseCompleteEventArgs> resultsReadyHandler;

	MainThreadState(@Nonnull final String fileName) {
		this.fileName = checkNotNull(fileName);
		setThreadId(Thread.currentThread().getId());
	}

	public CancellationToken getCancelToken() {
		return cancelSource.getToken();
	}

	public boolean isIdle() {
		stateLock.enter();
		try {
			return currentParcelCancelSource == null;
		}
		finally {
			stateLock.leave();
		}
	}

	public void cancel() {
		ensureOnThread();
		cancelSource.cancel();
	}

	public IDisposable lock() {
		stateLock.enter();
		return new DisposableAction(new Delegates.IAction() {
			@Override
			public void invoke() {
				stateLock.leave();
			}
		});
	}

	public void queueChange(@Nonnull final TextChange change) {
		RazorEditorTrace.traceLine(RazorResources().traceQueuingParse(), Filesystem.getFileName(fileName), checkNotNull(change));

		ensureOnThread();
		stateLock.enter();
		try {
			// CurrentParcel token source is not null ==> There's a parse underway
			if (currentParcelCancelSource != null) {
				currentParcelCancelSource.cancel();
			}
			changes.add(change);
			hasParcel.set();
		}
		finally {
			stateLock.leave();
		}
	}

	public WorkParcel getParcel() {
		ensureNotOnThread(); // Only the background thread can get a parcel
		hasParcel.waitFor(cancelSource.getToken());
		hasParcel.reset();

		stateLock.enter();
		try {
			// Create a cancellation source for this parcel
			currentParcelCancelSource = new CancellationTokenSource();

			final ImmutableList<TextChange> textChanges = ImmutableList.copyOf(changes);
			this.changes = Lists.newArrayList();
			return new WorkParcel(textChanges, currentParcelCancelSource.getToken());
		}
		finally {
			stateLock.leave();
		}
	}

	public void returnParcel(@Nonnull final DocumentParseCompleteEventArgs args) {
		stateLock.enter();
		try {
			// Clear the current parcel cancellation source
			if (currentParcelCancelSource != null) {
				currentParcelCancelSource.close();
				currentParcelCancelSource = null;
			}

			// If there are things waiting to be parsed, just don't fire the event because we're already out of date
			if (!changes.isEmpty()) {
				return;
			}

			if (resultsReadyHandler != null) {
				resultsReadyHandler.handleEvent(this, args);
			}
		}
		finally {
			stateLock.leave();
		}
	}

	@Override
	public void close() {
		close(true);
	}

	protected void close(boolean closing) {
		if (closing) {
			if (currentParcelCancelSource != null) {
				currentParcelCancelSource.close();
				currentParcelCancelSource = null;
			}
			cancelSource.close();
			hasParcel.close();
		}
	}

	public void setResultsReadyHandler(final IEventHandler<DocumentParseCompleteEventArgs> resultsReadyHandler) {
		this.resultsReadyHandler = resultsReadyHandler;
	}
}
