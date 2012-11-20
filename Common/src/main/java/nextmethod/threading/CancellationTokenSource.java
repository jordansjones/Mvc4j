package nextmethod.threading;

import nextmethod.annotations.Internal;
import nextmethod.base.IDisposable;
import nextmethod.base.NotImplementedException;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

// TODO
public class CancellationTokenSource implements IDisposable {

	@Internal
	public static final CancellationTokenSource NoneSource = new CancellationTokenSource();
	@Internal
	public static final CancellationTokenSource CanceledSource = new CancellationTokenSource();

	private boolean canceled;
	private boolean disposed;

	private int currentId = Integer.MIN_VALUE;

	private ManualResetEvent handle;

	static {
		CanceledSource.canceled = true;
	}

	public CancellationTokenSource() {
		this.handle = new ManualResetEvent(false);
	}

	public CancellationTokenSource(final long delay, @Nonnull final TimeUnit timeUnit) {
		this();

	}

	public boolean isCancellationRequested() {
		return canceled;
	}

	public CancellationToken getToken() {
		checkDisposed();
		return new CancellationToken(this);
	}

	public void cancel() {
		cancel(false);
	}

	public void cancel(final boolean throwOnFirstException) {
		checkDisposed();
		if (canceled) {
			return;
		}
		// TODO
	}

	public void cancelAfter(final long delay, @Nonnull final TimeUnit timeUnit) {
		throw new NotImplementedException();
	}


	@Override
	public void close() {
		throw new NotImplementedException();
	}

	public static CancellationTokenSource createLinkedTokenSource(@Nonnull final CancellationToken token1, @Nonnull final CancellationToken token2) {
		throw new NotImplementedException();
	}

	public static CancellationTokenSource createLinkedTokenSource(@Nonnull final CancellationToken... tokens) {
		throw new NotImplementedException();
	}

	void checkDisposed() {
		if (disposed) {
			throw new IllegalStateException("Object is Disposed");
		}
	}
}
