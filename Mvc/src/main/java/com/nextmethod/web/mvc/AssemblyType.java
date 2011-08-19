package com.nextmethod.web.mvc;

import java.lang.reflect.Modifier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
class AssemblyType<T> {

	private final String name;
	private final Class<T> cls;
	private final Package clsPackage;
	private final boolean isPublic;
	private final boolean isAbstract;
	private final boolean isEnum;
	private final boolean isInterface;
	private final boolean isArray;
	private final boolean isAnnotation;

	private AssemblyType(final String name, final Class<T> cls) {
		this.name = name;
		this.cls = cls;
		this.clsPackage = cls.getPackage();
		final int modifiers = cls.getModifiers();
		this.isPublic = Modifier.isPublic(modifiers);
		this.isAbstract = Modifier.isAbstract(modifiers);
		this.isEnum = cls.isEnum();
		this.isInterface = cls.isInterface();
		this.isArray = cls.isArray();
		this.isAnnotation = cls.isAnnotation();
	}

	public String getName() {
		return name;
	}

	public Package getPackage() {
		return this.clsPackage;
	}

	public Class<T> getTypeClass() {
		return cls;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public boolean isClass() {
		return !isAnnotation
			&& !isArray
			&& !isEnum
			&& !isInterface;
	}

	public boolean isA(final Class<?> cls) {
		return checkNotNull(cls).isAssignableFrom(this.cls);
	}

	public static <T> AssemblyType<T> of(final Class<T> cls) {
		final String name = checkNotNull(cls).getSimpleName();
		return new AssemblyType<T>(name, cls);
	}

	@SuppressWarnings({"SimplifiableIfStatement"})
	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof AssemblyType)) return false;

		final AssemblyType that = (AssemblyType) o;

		if (!cls.equals(that.cls)) return false;
		if (clsPackage != null ? !clsPackage.equals(that.clsPackage) : that.clsPackage != null) return false;

		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + cls.hashCode();
		result = 31 * result + (clsPackage != null ? clsPackage.hashCode() : 0);
		return result;
	}
}
