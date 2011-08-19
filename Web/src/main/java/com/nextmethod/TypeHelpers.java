package com.nextmethod;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.TypeLiteral;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: jordanjones
 * Date: 8/8/11
 * Time: 3:37 PM
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

	public static <T extends Annotation> List<T> getMethodAnnotations(final Method method, final Class<T> annotation) {
		final List<T> annotations = Lists.newArrayList();
		if (method == null || annotation == null)
			return annotations;

		for (Annotation a : method.getAnnotations()) {
			if (annotation.isAssignableFrom(a.getClass())) {
				annotations.add(annotation.cast(a));
			}
		}

		return annotations;
	}

	@SuppressWarnings({"SimplifiableIfStatement"})
	public static boolean methodAnnotatedWith(final Method method, final Class<? extends Annotation> annotation) {
		if (method == null || annotation == null)
			return false;

		return Iterables.any(Sets.<Annotation>newHashSet(method.getAnnotations()), new Predicate<Annotation>() {
			@Override
			public boolean apply(@Nullable final Annotation input) {
				return input != null && annotation.isAssignableFrom(input.getClass());
			}
		});
	}
}
