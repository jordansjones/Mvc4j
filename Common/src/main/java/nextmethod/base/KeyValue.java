package nextmethod.base;

import javax.annotation.Nonnull;

public class KeyValue<A, B> {

	private final A key;
	private final B value;

	public KeyValue(@Nonnull final A key, @Nonnull final B value) {
		this.key = key;
		this.value = value;
	}

	@SuppressWarnings("UnusedDeclaration")
	public A getKey() {
		return key;
	}

	@SuppressWarnings("UnusedDeclaration")
	public B getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KeyValue)) return false;

		KeyValue keyValue = (KeyValue) o;

		if (!key.equals(keyValue.key)) return false;
		if (!value.equals(keyValue.value)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = key.hashCode();
		result = 31 * result + value.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{key=" + key + ", value=" + value + '}';
	}

	public static <A, B> KeyValue<A, B> of(@Nonnull final A a, @Nonnull final B b) {
		return new KeyValue<>(a, b);
	}

}
