package nextmethod.base;

import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

public abstract class StringComparer implements Comparator<String>, IEqualityComparer<String> {

	private static final OrdinalComparer caseInsensitive = new OrdinalComparer(true);
	private static final OrdinalComparer caseSensitive = new OrdinalComparer(false);

	public static StringComparer getOrdinal () {
		return caseSensitive;
	}

	public static StringComparer getOrdinalIgnoreCase() {
		return caseInsensitive;
	}

	protected StringComparer() {
	}

	@Override
	public int compare(String o1, String o2) {
		if (o1 == null && o2 == null) return 0;
		if (o1 != null && o2 == null) return -1;
		if (o1 == null) return 1;

		return o1.compareTo(o2);
	}

	@Override
	public boolean equals(String x, String y) {
		if (x == null)
			return y == null;

		return (Object) x == (Object) y || x.equals(y);
	}


	@Override
	public int getHashCode(String obj) {
		return Objects.hashCode(obj);
	}

	private static final class OrdinalComparer extends StringComparer {

		private final boolean ignoreCase;

		private OrdinalComparer(boolean ignoreCase) {
			this.ignoreCase = ignoreCase;
		}

		@Override
		public int compare(String o1, String o2) {
			if (o1 == null && o2 == null) return 0;
			if (o1 != null && o2 == null) return -1;
			if (o1 == null) return 1;

			if (ignoreCase)
				return o1.compareToIgnoreCase(o2);

			return super.compare(o1, o2);
		}

		@Override
		public boolean equals(String x, String y) {
			if (ignoreCase && x != null)
				return x.equalsIgnoreCase(y);

			return super.equals(x, y);
		}

		@Override
		public int getHashCode(String obj) {
			if (ignoreCase && obj != null)
				return obj.toUpperCase(Locale.ENGLISH).hashCode();

			return super.getHashCode(obj);
		}
	}
}
