package nextmethod.base;

import static com.google.common.base.Preconditions.checkNotNull;

public final class TypeHelpers {

	private TypeHelpers() {
	}

	public static <T> boolean typeIs(final Object o, final Class<T> cls) {
		checkNotNull(cls);
		return o != null && cls.isInstance(o);
	}

	public static <T> T typeAs(final OutParam<?> o, final Class<T> cls) {
		return typeAs(o.value(), cls);
	}

	public static <T> T typeAs(final Object o, final Class<T> cls) {
		checkNotNull(cls);

		return o != null && cls.isInstance(o) ? cls.cast(o) : null;
	}

}
