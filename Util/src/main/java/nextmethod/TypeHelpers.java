package nextmethod;

import com.google.inject.TypeLiteral;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public final class TypeHelpers {

	private TypeHelpers() {
	}

	public static <T> boolean typeIs(final Object o, final Class<T> cls) {
		checkNotNull(cls);
		return o != null && cls.isInstance(o);
	}

	public static <T> T typeAs(final OutParam<?> o, final Class<T> cls) {
		return typeAs(o.get(), cls);
	}

	public static <T> T typeAs(final Object o, final Class<T> cls) {
		checkNotNull(cls);

		return o != null && cls.isInstance(o) ? cls.cast(o) : null;
	}

	@SuppressWarnings({"unchecked"})
	public static <T> T typeAs(final Object o, final TypeLiteral<T> typeLiteral) {
		checkNotNull(typeLiteral);

		return (T) typeAs(o, typeLiteral.getRawType());
	}

	@SuppressWarnings({"unchecked"})
	public static <T> Class<T> rawType() {
		final TypeLiteral<T> typeLiteral = new TypeLiteral<T>() {
		};
		return (Class<T>) typeLiteral.getRawType();
	}
}
