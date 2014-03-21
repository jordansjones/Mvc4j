package nextmethod.threading;

import com.google.common.collect.ImmutableSet;
import nextmethod.annotations.Internal;
import nextmethod.base.IDisposable;
import nextmethod.base.NotImplementedException;
import nextmethod.base.ObjectDisposedException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

// TODO
abstract class WaitHandle implements IDisposable {

	static final WaitHandle DefaultHandle = new WaitHandle(false) {
	};

	@Internal
	static final int WaitTimeout = 258;

	@Internal
	protected volatile AtomicBoolean disposed = new AtomicBoolean(false);

	@Internal
	protected volatile AtomicBoolean state;

	private volatile Queue<Thread> waiters = new ConcurrentLinkedQueue<>();

	protected WaitHandle(final boolean initialState) {
		this.state = new AtomicBoolean(initialState);
	}

	protected boolean isSet() {
		return state.get();
	}

	public boolean reset() {
		synchronized (this) {
			checkDisposed();
			return state.compareAndSet(true, false);
		}
	}

	public boolean set() {
		final ImmutableSet<Thread> threads;
		synchronized (this) {
			checkDisposed();

			if (!state.compareAndSet(false, true)) {
				return false;
			}
			threads = ImmutableSet.copyOf(waiters);
			waiters.clear();
		}
		threads.forEach(LockSupport::unpark);
		return true;
	}

	@Override
	public void close() {
		close(true);
	}

	protected void close(boolean explicitClose) {
		if (!disposed.get()) {
			synchronized (this) {
				if (disposed.get()) {
					return;
				}

				disposed.set(true);
			}
		}
	}

	@Internal
	protected void checkDisposed() {
		if (disposed.get()) {
			throw new ObjectDisposedException(getClass().getName());
		}
	}

	private boolean await() {
		final Thread current = Thread.currentThread();
		waiters.add(current);
		LockSupport.park();
		return isSet();
	}

	private boolean await(final long amount, final TimeUnit timeUnit) {
		if (amount == 0) { return set(); }

		final Thread current = Thread.currentThread();
		waiters.add(current);
		LockSupport.parkUntil(timeUnit.toMillis(amount));
		return isSet();
	}

	public static boolean awaitOne() {
		return DefaultHandle.await();
	}

	public static boolean awaitOne(final long millisecondsToWait) {
		return awaitOne(millisecondsToWait, TimeUnit.MILLISECONDS);
	}

	public static boolean awaitOne(final long amount, final TimeUnit timeUnit) {
		return DefaultHandle.await(amount, timeUnit);
	}

	public static boolean awaitAll(final EventWaitHandle... waitHandles) {
		throw new NotImplementedException();
	}

	public static boolean awaitAll(final long millisecondsToWait, final WaitHandle... waitHandles) {
		throw new NotImplementedException();
	}

	public static boolean awaitAll(final long amount, final TimeUnit timeUnit, final WaitHandle... waithandles) {
		throw new NotImplementedException();
	}

	public static int awaitAny(final WaitHandle... waitHandles) {
		throw new NotImplementedException();
	}

	public static int awaitAny(final long millisecondsToWait, final WaitHandle... waitHandles) {
		return awaitAny(millisecondsToWait, TimeUnit.MILLISECONDS, waitHandles);
	}

	public static int awaitAny(final long millisecondsToWait, final TimeUnit timeUnit, final WaitHandle... waitHandles) {
//		try {
//			for (WaitHandle waitHandle : waitHandles) {
//				waitHandle.await(millisecondsToWait, timeUnit);
//			}
//		}
//		catch (InterruptedException e) {
//
//		}
		throw new NotImplementedException();
	}

//	private static int awaitAny(final long timeout, final TimeUnit timeUnit, final ManualResetEvent... handles) {
//		final long waitTime = timeUnit.toMillis(timeout);
//		for (int i = 0; i < handles.length; i++) {
//			if (handles[i].await(timeout, timeUnit)) {
//				return i;
//			}
//		}
//		return WaitHandle.WaitTimeout;
//	}


//	public boolean await() {
//		return await(CancellationToken.none());
//	}
//
//	public boolean await(final long timeout, @Nonnull final TimeUnit unit) {
//		return await(timeout, unit, CancellationToken.none());
//	}
//
//	public boolean await(@Nonnull final CancellationToken cancellationToken) {
//		return await(Long.MAX_VALUE, TimeUnit.DAYS, cancellationToken);
//	}
//
//	public boolean await(final long timeout, @Nonnull final TimeUnit unit, @Nonnull final CancellationToken cancellationToken) {
//		checkArgument(timeout > -1);
//
//		throwIfClosed();
//		if (!isSet()) {
//
//			final SpinWait spinWait = new SpinWait();
//
//			while (!isSet()) {
//				cancellationToken.throwIfCancellationRequested();
//
//				if (spinWait.count() < spinCount) {
//					spinWait.spinOnce();
//					continue;
//				}
//
//				break;
//			}
//
//			if (isSet()) {
//				return true;
//			}
//
//			final EventWaitHandle handle = getWaitHandle();
//
//			if (cancellationToken.canBeCanceled()) {
//				final int result = WaitHandle.awaitAny(timeout, unit, handle, cancellationToken.getWaitHandle());
//				if (result == 1) {
//					throw new OperationCanceledException(cancellationToken);
//				}
//				if (result == WaitHandle.WaitTimeout) {
//					return false;
//				}
//			}
//			else {
//				if (!handle.awaitOne(timeout, unit)) {
//					return false;
//				}
//			}
//		}
//		return true;
//	}
//
//	public boolean awaitOne() {
//		return awaitOne(Integer.MAX_VALUE, TimeUnit.DAYS);
//	}
//
//	public boolean awaitOne(final long amount, final TimeUnit timeUnit) {
//		throwIfClosed();
//		boolean interrupted = false;
//		try {
//			timeUnit.sleep(amount);
//		}
//		catch (InterruptedException e) {
//			interrupted = true;
//		}
//		finally {
//			if (interrupted) {
//				Thread.currentThread().interrupt();
//			}
//		}
//		return isSet();
//	}
}
