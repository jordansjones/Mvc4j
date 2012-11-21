package nextmethod.threading;

import com.google.common.util.concurrent.Atomics;
import nextmethod.base.IDisposable;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkArgument;

// TODO
public final class ManualResetEvent implements IDisposable {

	private final AtomicBoolean disposed;
	private final AtomicBoolean state;

	private AtomicReference<ManualResetEvent> handle = Atomics.newReference();

	public ManualResetEvent() {
		this(false);
	}

	public ManualResetEvent(final boolean initialState) {
		this.state = new AtomicBoolean(initialState);
		this.disposed = new AtomicBoolean(false);
	}

	public boolean isSet() {
		return state.get();
	}

	public void reset() {
		throwIfClosed();
		updateState(false);
	}

	public void set() {
		updateState(true);
	}

	public boolean waitFor() {
		return waitFor(CancellationToken.none());
	}

	public boolean waitFor(final long timeout, @Nonnull final TimeUnit unit) {
		return waitFor(timeout, unit, CancellationToken.none());
	}

	public boolean waitFor(@Nonnull final CancellationToken cancellationToken) {
		return waitFor(Long.MAX_VALUE, TimeUnit.DAYS, cancellationToken);
	}

	public boolean waitFor(final long timeout, @Nonnull final TimeUnit unit, @Nonnull final CancellationToken cancellationToken) {
		checkArgument(timeout > -1);

		throwIfClosed();
		if (!isSet()) {

			int spinCount = 0;
			int maxTime = 100;
			// Try sleeping for very small amounts of time
			while (!isSet()) {
				cancellationToken.throwIfCancellationRequested();
				spinCount += 1;
				if (spinCount < maxTime) {
					try {
						Thread.sleep(spinCount << 1);
					} catch (InterruptedException e) {}
					continue;
				}
				break;
			}

			if (isSet()) {
				return true;
			}

			final ManualResetEvent handle = getWaitHandle();

			if (cancellationToken.canBeCanceled()) {
				if (waitAny(timeout, unit, handle, cancellationToken.getWaitHandle())) {
					throw new OperationCanceledException(cancellationToken);
				}
				else {
					return false;
				}
			}
			else {
				if (!handle.waitAny(timeout, unit, handle)) {
					return false;
				}
			}
		}
		return true;
	}

	public ManualResetEvent getWaitHandle() {
		throwIfClosed();

		ManualResetEvent mre = handle.get();
		if (mre != null) {
			return mre;
		}

		final boolean isSet = isSet();
		mre = new ManualResetEvent(isSet);
		if (handle.compareAndSet(null, mre)) {
			if (isSet != isSet()) {
				if (isSet()) {
					mre.set();
				}
				else {
					mre.reset();
				}
			}
		}
		else {
			mre.close();
		}
		return handle.get();
	}

	private void updateState(final boolean newValue) {
		boolean oldValue;
		do {
			oldValue = state.get();
		} while (!state.compareAndSet(oldValue, newValue));
	}

	@Override
	public void close() {
		close(true);
	}

	protected void close(final boolean closing) {
		if (!disposed.compareAndSet(false, true)) {
			return;
		}

		if (handle.get() != null) {
			final ManualResetEvent mre = handle.getAndSet(null);
			mre.close();
		}
	}

	void throwIfClosed() {
		if (disposed.get()) {
			throw new IllegalStateException("Object is Disposed");
		}
	}

	private boolean waitAny(final long timeout, final TimeUnit timeUnit, final ManualResetEvent... handles) {
		boolean success = false;
		final long waitTime = timeUnit.toMillis(timeout);
		for (ManualResetEvent handle : handles) {
			try {
				synchronized (handle) {
					handle.wait(waitTime);
				}
			} catch (InterruptedException ignored) {}

			if (success) {
				break;
			}
		}
		return success;
	}


}
