package com.nextmethod;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: jordanjones
 * Date: 8/8/11
 * Time: 3:37 PM
 */
public final class TypeHelpers {

	private TypeHelpers() {
	}

	public static <T> T typeAs(final Object o, final Class<T> cls) {
		checkNotNull(cls);

		return o != null && cls.isAssignableFrom(o.getClass()) ? cls.cast(o) : null;
	}
}
