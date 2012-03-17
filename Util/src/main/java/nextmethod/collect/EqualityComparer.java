package nextmethod.collect;

import nextmethod.annotations.TODO;
import nextmethod.base.IEqualityComparer;

import java.util.Objects;

@TODO
public abstract class EqualityComparer <T> implements IEqualityComparer<T> {

	private static final CompositeEqualityComparer def = new CompositeEqualityComparer();

	@SuppressWarnings("unchecked")
	public static <T> EqualityComparer<T> getDefault() {
		return (EqualityComparer<T>) def;
	}
	
	final class InternalStringCompararer extends EqualityComparer<String> {

		@Override
		public boolean equals(String x, String y) {
			if (x == null)
				return y == null;

			return (Object) x == (Object) y || x.equals(y);
		}

		@Override
		public int getHashCode(String obj) {
			return 0;
		}
	}

	private static final class CompositeEqualityComparer extends EqualityComparer<Object> {

		private final InternalStringCompararer stringCompararer = new InternalStringCompararer();

		@Override
		public boolean equals(Object x, Object y) {
			if (x instanceof String && y instanceof String) {
				return stringCompararer.equals(String.class.cast(x), String.class.cast(y));
			}
			return Objects.equals(x, y);
		}

		@Override
		public int getHashCode(Object obj) {
			if (obj != null && obj instanceof String) {
				return stringCompararer.getHashCode(String.class.cast(obj));
			}
			return Objects.hashCode(obj);
		}
	}
}
