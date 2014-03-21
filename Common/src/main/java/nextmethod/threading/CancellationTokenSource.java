package nextmethod.threading;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import nextmethod.annotations.Internal;
import nextmethod.base.AggregateException;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.NotImplementedException;
import nextmethod.base.ObjectDisposedException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

// TODO
public class CancellationTokenSource implements IDisposable {

	@Internal
	public static final CancellationTokenSource NoneSource = new CancellationTokenSource();
	@Internal
	public static final CancellationTokenSource CanceledSource = new CancellationTokenSource();

	private boolean canceled;
	private boolean disposed;

	private AtomicInteger currentId = new AtomicInteger(Integer.MIN_VALUE);

	ConcurrentMap<CancellationTokenRegistration, Delegates.IAction> callbacks;
	CancellationTokenRegistration[] linkedTokens;

	private EventWaitHandle handle;

	private final Object lockObject = new Object();

	static {
		CanceledSource.canceled = true;
	}

	public CancellationTokenSource() {
		this.callbacks = new MapMaker().makeMap();
		this.handle = new EventWaitHandle(false);
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

	Delegates.IAction createSafeLinkedCancel() {
		return () -> {
			try {
				cancel();
			}
			catch (RuntimeException ignored) {}
		};
	}

	public void cancel() {
		cancel(false);
	}

	public void cancel(final boolean throwOnFirstException) {
		checkDisposed();
		if (canceled) {
			return;
		}
		synchronized (lockObject) {
			if (canceled) {
				return;
			}

			canceled = true;

			handle.set();
			if (linkedTokens != null) {
				unregisterLinkedTokens();
			}

			List<Exception> exceptions = null;

			try {
				Delegates.IAction cb;
				for (int id = Integer.MIN_VALUE + 1; id <= currentId.get(); id++) {
					CancellationTokenRegistration tok = new CancellationTokenRegistration(id, this);
					if (!callbacks.containsKey(tok)) {
						continue;
					}
					cb = callbacks.remove(tok);
					if (cb == null) {
						continue;
					}

					if (throwOnFirstException) {
						cb.invoke();
					}
					else {
						try {
							cb.invoke();
						}
						catch (Exception e) {
							if (exceptions == null) {
								exceptions = Lists.newArrayList();
							}
							exceptions.add(e);
						}
					}
				}
			}
			finally {
				callbacks.clear();
			}

			if (exceptions != null && !exceptions.isEmpty()) {
				throw new AggregateException(exceptions);
			}
		}
	}

	public void cancelAfter(final long delay, @Nonnull final TimeUnit timeUnit) {
		throw new NotImplementedException();
	}


	@Override
	public void close() {
		close(true);
	}

	protected void close(final boolean disposing) {
		if (disposing && !disposed) {
			synchronized (lockObject) {
				disposed = true;

				if (!canceled) {
					unregisterLinkedTokens();
					callbacks = null;
				}

				handle.close();
			}
		}
	}

	void unregisterLinkedTokens() {
		if (linkedTokens == null) {
			return;
		}
		for (CancellationTokenRegistration linkedToken : linkedTokens) {
			linkedToken.close();
		}
		linkedTokens = null;
	}

	CancellationTokenRegistration register(@Nonnull final Delegates.IAction callback, final boolean useSynchronizationContext) {
		checkDisposed();
		final CancellationTokenRegistration tokenReg = new CancellationTokenRegistration(currentId.incrementAndGet(), this);

		if (canceled) {
			callback.invoke();
		}
		else {
			callbacks.putIfAbsent(tokenReg, callback);
			if (canceled && callbacks.remove(tokenReg, callback)) {
				callback.invoke();
			}
		}

		return tokenReg;
	}

	@Internal
	void removeCallback(@Nonnull final CancellationTokenRegistration reg) {
		if (disposed) {
			return;
		}

		final ConcurrentMap<CancellationTokenRegistration, Delegates.IAction> cbs = callbacks;
		if (cbs != null) {
			cbs.remove(reg);
		}
	}

	public static CancellationTokenSource createLinkedTokenSource(@Nonnull final CancellationToken token1, @Nonnull final CancellationToken token2) {
		return createLinkedTokenSource(new CancellationToken[] { checkNotNull(token1), checkNotNull(token2) });
	}

	public static CancellationTokenSource createLinkedTokenSource(@Nonnull final CancellationToken... tokens) {
		checkNotNull(tokens);
		checkArgument(tokens.length > 0);

		final CancellationTokenSource src = new CancellationTokenSource();
		final Delegates.IAction action = src.createSafeLinkedCancel();

		final List<CancellationTokenRegistration> registrations = Lists.<CancellationTokenRegistration>newArrayList();

		for (CancellationToken token : tokens) {
			if (token.canBeCanceled()) {
				registrations.add(token.register(action));
			}
		}
		src.linkedTokens = registrations.toArray(new CancellationTokenRegistration[registrations.size()]);

		return src;
	}

	void checkDisposed() {
		if (disposed) {
			throw new ObjectDisposedException(getClass().getName());
		}
	}
}
