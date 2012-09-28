package nextmethod.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KeyValue<A, B> {

	private final A key;
	private final B value;

	public KeyValue(@Nullable final A key, @Nullable final B value) {
		this.key = key;
		this.value = value;
	}

	@Nullable
	public A getKey() {
		return key;
	}

	@Nullable
	public B getValue() {
		return value;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final KeyValue keyValue = (KeyValue) o;

		if (key != null ? !key.equals(keyValue.key) : keyValue.key != null) return false;
		if (value != null ? !value.equals(keyValue.value) : keyValue.value != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = key != null ? key.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return String.format("%s{key=%s, value=%s}", this.getClass().getSimpleName(), key, value);
	}

	public static <A, B> KeyValue<A, B> of(@Nullable final A a, @Nullable final B b) {
		return new KeyValue<>(a, b);
	}

}
