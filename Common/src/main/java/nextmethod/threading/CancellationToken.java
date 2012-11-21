package nextmethod.threading;

import nextmethod.annotations.Internal;
import nextmethod.base.Delegates;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

// TODO
public final class CancellationToken {

	private final CancellationTokenSource source;

	public CancellationToken(final boolean canceled) {
		this(canceled ? CancellationTokenSource.CanceledSource : null);
	}

	@Internal
	public CancellationToken(final CancellationTokenSource source) {
		this.source = source;
	}

	private CancellationTokenSource source() {
		return source == null ? CancellationTokenSource.NoneSource : source;
	}

	public static CancellationToken none() {
		return new CancellationToken(false);
	}

	public boolean canBeCanceled() {
		return source != null;
	}

	public boolean isCancellationRequested() {
		return source().isCancellationRequested();
	}

	public void throwIfCancellationRequested() {
		if (source().isCancellationRequested()) {
			throw new OperationCanceledException(this);
		}
	}

	public ManualResetEvent getWaitHandle() {
		return source().getWaitHandle();
	}

	public CancellationTokenRegistration register(@Nonnull final Delegates.IAction callback) {
		return register(callback, false);
	}

	public CancellationTokenRegistration register(@Nonnull final Delegates.IAction callback, final boolean useSynchronizationContext) {
		return source().register(checkNotNull(callback), useSynchronizationContext);
	}

}
