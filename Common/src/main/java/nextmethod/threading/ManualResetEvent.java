package nextmethod.threading;

import nextmethod.base.IDisposable;
import nextmethod.base.NotImplementedException;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

// TODO
public final class ManualResetEvent implements IDisposable {

	public ManualResetEvent() {

	}

	public ManualResetEvent(final boolean initialState) {

	}

	public void reset() {
		throw new NotImplementedException();
	}

	public void set() {
		throw new NotImplementedException();
	}

	public void waitFor() {
		throw new NotImplementedException();
	}

	public void waitFor(@Nonnull final CancellationToken cancellationToken) {
		throw new NotImplementedException();
	}

	public void waitFor(final long timeout, @Nonnull final TimeUnit unit) {
		throw new NotImplementedException();
	}

	public void waitFor(final long timeout, @Nonnull final TimeUnit unit, @Nonnull final CancellationToken cancellationToken) {
		throw new NotImplementedException();
	}

	@Override
	public void close() {
		throw new NotImplementedException();
	}
}
