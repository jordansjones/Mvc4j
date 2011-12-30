package nextmethod;

/**
 *
 */
public class Tuple<A, B> {

	private final A item1;
	private final B item2;

	public Tuple(final A item1, final B item2) {
		this.item1 = item1;
		this.item2 = item2;
	}

	public A getItem1() {
		return item1;
	}

	public B getItem2() {
		return item2;
	}

	public static <A, B> Tuple<A, B> of(final A a, final B b) {
		return new Tuple<A, B>(a, b);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof Tuple)) return false;

		final Tuple tuple = (Tuple) o;

		if (item1 != null ? !item1.equals(tuple.item1) : tuple.item1 != null) return false;
		if (item2 != null ? !item2.equals(tuple.item2) : tuple.item2 != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = item1 != null ? item1.hashCode() : 0;
		result = 31 * result + (item2 != null ? item2.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append("{item1=").append(item1);
		sb.append(", item2=").append(item2);
		sb.append('}');
		return sb.toString();
	}
}
