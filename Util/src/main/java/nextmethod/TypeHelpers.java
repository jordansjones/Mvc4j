package nextmethod;

import com.google.inject.TypeLiteral;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public final class TypeHelpers {

	private TypeHelpers() {
	}

	public static <T> T typeAs(final OutParam<?> o, final Class<T> cls) {
		return typeAs(o.get(), cls);
	}

	public static <T> T typeAs(final Object o, final Class<T> cls) {
		checkNotNull(cls);

		return o != null && cls.isAssignableFrom(o.getClass()) ? cls.cast(o) : null;
	}

	@SuppressWarnings({"unchecked"})
	public static <T> T typeAs(final Object o, final TypeLiteral<T> typeLiteral) {
		checkNotNull(typeLiteral);

		return (T) typeAs(o, typeLiteral.getRawType());
	}

}
