package nextmethod.base;

import javax.annotation.Nonnull;

public class Tuple<A, B> {

	private final A item1;
	private final B item2;

	public Tuple(@Nonnull final A item1, @Nonnull final B item2) {
		this.item1 = item1;
		this.item2 = item2;
	}

	@SuppressWarnings("UnusedDeclaration")
	public A getItem1() {
		return item1;
	}

	@SuppressWarnings("UnusedDeclaration")
	public B getItem2() {
		return item2;
	}

	public static <A, B> Tuple<A, B> of(@Nonnull final A a, @Nonnull final B b) {
		return new Tuple<>(a, b);
	}

	public static <A, B> Tuple<A, B> create(@Nonnull final A a, @Nonnull final B b) {
		return of(a, b);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Tuple)) return false;

		Tuple tuple = (Tuple) o;

		if (!item1.equals(tuple.item1)) return false;
		if (!item2.equals(tuple.item2)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = item1.hashCode();
		result = 31 * result + item2.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{item1=" + item1 + ", item2=" + item2 + '}';
	}
}
