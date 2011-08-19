package com.nextmethod;

import com.google.inject.TypeLiteral;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: jordanjones
 * Date: 8/8/11
 * Time: 3:22 PM
 */
public final class OutParam<T> {

	private T value;

	private OutParam(T value) {
		this.value = value;
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	public boolean isNull() {
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

	@SuppressWarnings({"RedundantIfStatement"})
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OutParam that = (OutParam) o;

		if (value != null ? !value.equals(that.value) : that.value != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return value != null ? value.hashCode() : 0;
	}

	public static OutParam<Boolean> of(final boolean value) {
		return new OutParam<Boolean>(value);
	}

	public static OutParam<Integer> of(final int value) {
		return new OutParam<Integer>(value);
	}

	public static OutParam<Long> of(final long value) {
		return new OutParam<Long>(value);
	}

	public static OutParam<Float> of(final float value) {
		return new OutParam<Float>(value);
	}

	public static OutParam<Double> of(final double value) {
		return new OutParam<Double>(value);
	}

	public static OutParam<Byte> of(final byte value) {
		return new OutParam<Byte>(value);
	}

	public static OutParam<Character> of(final char value) {
		return new OutParam<Character>(value);
	}

	public static OutParam<Short> of(final short value) {
		return new OutParam<Short>(value);
	}

	public static <V> OutParam<V> of(@Nullable final V value, @Nonnull final Class<V> cls) {
		return new OutParam<V>(value);
	}

	public static <V> OutParam<V> of(@Nonnull final Class<V> cls) {
		return new OutParam<V>(null);
	}

	@SuppressWarnings({"unchecked"})
	public static <V> OutParam<V> of(@Nonnull final TypeLiteral<V> type) {
		return (OutParam<V>) of(type.getRawType());
	}

	public static <V> OutParam<V> of() {
		return new OutParam<V>(null);
	}
}
