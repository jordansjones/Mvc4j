package nextmethod.reflection;

/**
 *
 */
abstract class WrappedInfo<T> {

	protected final T wrapped;

	protected WrappedInfo(final T wrapped) {
		this.wrapped = wrapped;
	}

	public T wrappedType() {
		return wrapped;
	}

	public int wrappedHashCode() {
		return this.wrapped.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof WrappedInfo)) return false;

		final WrappedInfo that = (WrappedInfo) o;

		return wrapped.equals(that.wrapped);
	}

	@Override
	public int hashCode() {
		return wrapped.hashCode();
	}

}
