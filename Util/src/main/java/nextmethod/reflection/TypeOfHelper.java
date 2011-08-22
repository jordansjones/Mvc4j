package nextmethod.reflection;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public final class TypeOfHelper {

	// This is a "static" class
	private TypeOfHelper() {
	}

	// ************************************************************************
	// Common Helpers
	// ************************************************************************

	@SuppressWarnings({"unchecked"})
	private static <T> Class<T> rawType() {
		final TypeLiteral<T> typeLiteral = new TypeLiteral<T>() {
		};
		return (Class<T>) typeLiteral.getRawType();
	}


	// ************************************************************************
	// Class Helpers
	// ************************************************************************

	@SuppressWarnings({"unchecked"})
	public static <T> ClassInfo<T> getType(@Nonnull final T obj) {
		return typeOf((Class<T>) checkNotNull(obj).getClass());
	}

	public static <T> ClassInfo<T> typeOf(@Nonnull final Class<T> cls) {
		return new ClassInfo<T>(checkNotNull(cls));
	}

	@SuppressWarnings({"unchecked"})
	public static <T extends Class> ImmutableCollection<ClassInfo<T>> asClassInfo(@Nonnull final Iterable<T> classes) {
		final ImmutableSet.Builder<ClassInfo<T>> builder = ImmutableSet.builder();
		for (T aClass : classes) {
			builder.add(typeOf(aClass));
		}
		return builder.build();
	}


	// ************************************************************************
	// Method Helpers
	// ************************************************************************

	public static MethodInfo typeOf(@Nonnull final Method method) {
		return new MethodInfo(checkNotNull(method));
	}

	public static ImmutableCollection<MethodInfo> asMethodInfo(@Nonnull final Iterable<Method> methods) {
		final ImmutableSet.Builder<MethodInfo> builder = ImmutableSet.builder();
		for (Method method : methods) {
			builder.add(typeOf(method));
		}
		return builder.build();
	}


	// ************************************************************************
	// Annotation Helpers
	// ************************************************************************

	public static <T extends Annotation> AnnotationInfo<T> typeOf(@Nonnull final T annotation) {
		return new AnnotationInfo<T>(annotation);
	}

	@SuppressWarnings({"unchecked"})
	public static <T extends Annotation> ImmutableCollection<AnnotationInfo<T>> asAnnotationInfo(@Nonnull final Iterable<T> annotations) {
		final ImmutableSet.Builder<AnnotationInfo<T>> builder = ImmutableSet.builder();
		for (T annotation : annotations) {
			builder.add(typeOf(annotation));
		}
		return builder.build();
	}
}
