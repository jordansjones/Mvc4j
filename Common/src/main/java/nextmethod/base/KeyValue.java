package nextmethod.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KeyValue<A, B> {

	private final A key;
	private final B value;

	public KeyValue(@Nonnull final A key, @Nullable final B value) {
		this.key = key;
		this.value = value;
	}

	@Nonnull
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

		if (!key.equals(keyValue.key)) return false;
		if (value != null ? !value.equals(keyValue.value) : keyValue.value != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = key.hashCode();
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{key=" + key + ", value=" + value + '}';
	}

	public static <A, B> KeyValue<A, B> of(@Nonnull final A a, @Nullable final B b) {
		return new KeyValue<>(a, b);
	}

}
