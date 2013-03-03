package nextmethod.base;

import javax.annotation.Nullable;

/**
 *
 */
public final class OutParam<T> {

	private T value;

	private OutParam(T value) {
		this.value = value;
	}

	public T value() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	@SuppressWarnings("UnusedDeclaration")
	public boolean hasValue() {
		return this.value == null;
	}

	@Override
	public String toString() {
		final boolean isNull = value == null;
		return String.format("OutParam<%s>(%s)",
			isNull ? "NULL" : value.getClass().getSimpleName(),
			isNull ? "null" : value.toString()
		);
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OutParam reference = (OutParam) o;

		if (value != null ? !value.equals(reference.value) : reference.value != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return value != null ? value.hashCode() : 0;
	}

	public static OutParam<Boolean> of(final boolean value) {
		return new OutParam<>(value);
	}

	public static OutParam<Integer> of(final int value) {
		return new OutParam<>(value);
	}

	public static OutParam<Long> of(final long value) {
		return new OutParam<>(value);
	}

	public static OutParam<Float> of(final float value) {
		return new OutParam<>(value);
	}

	public static OutParam<Double> of(final double value) {
		return new OutParam<>(value);
	}

	public static OutParam<Byte> of(final byte value) {
		return new OutParam<>(value);
	}

	public static OutParam<Character> of(final char value) {
		return new OutParam<>(value);
	}

	public static OutParam<Short> of(final short value) {
		return new OutParam<>(value);
	}

	public static <V> OutParam<V> of(@Nullable final V value) {
		return new OutParam<>(value);
	}

	public static <V> OutParam<V> of() {
		return new OutParam<>(null);
	}
}
