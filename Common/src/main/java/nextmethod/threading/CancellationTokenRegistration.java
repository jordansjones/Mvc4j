package nextmethod.threading;

import nextmethod.annotations.Internal;
import nextmethod.base.IDisposable;

import javax.annotation.Nullable;

public final class CancellationTokenRegistration implements IDisposable {

	private final int id;
	private final CancellationTokenSource source;

	@Internal
	public CancellationTokenRegistration(final int id, @Nullable final CancellationTokenSource source) {
		this.id = id;
		this.source = source;
	}

	@Override
	public void close() {
		if (source != null) {
			source.removeCallback(this);
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final CancellationTokenRegistration that = (CancellationTokenRegistration) o;

		if (id != that.id) return false;
		if (source != null ? !source.equals(that.source) : that.source != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (source != null ? source.hashCode() : 0);
		return result;
	}
}
