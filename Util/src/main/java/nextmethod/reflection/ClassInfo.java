package nextmethod.reflection;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public final class ClassInfo<T> extends WrappedInfo<Class<T>> {

	static ClassInfo[] EmptyArray = new ClassInfo[0];

	private final String name;
	private final String fullName;
	private final boolean isAbstract;
	private final boolean isAnnotation;
	private final boolean isArray;
	private final boolean isEnum;
	private final boolean isFinal;
	private final boolean isGeneric;
	private final boolean isInterface;
	private final boolean isNested;
	private final boolean isPrimitive;
	private final boolean isPublic;
	private final boolean isStatic;

	private final Object lockObject = new Object();

	private ImmutableCollection<AnnotationInfo<Annotation>> annotations;
	private ImmutableCollection<MethodInfo> methods;

	public ClassInfo(final Class<T> cls) {
		super(cls);
		this.name = cls.getSimpleName();
		this.fullName = cls.getName();
		final int modifiers = cls.getModifiers();
		this.isAbstract = Modifier.isAbstract(modifiers);
		this.isAnnotation = cls.isAnnotation();
		this.isArray = cls.isArray();
		this.isEnum = cls.isEnum();
		this.isFinal = Modifier.isFinal(modifiers);
		this.isGeneric = cls.getGenericSuperclass() != null;
		this.isInterface = cls.isInterface();
		this.isNested = cls.isMemberClass();
		this.isPrimitive = cls.isPrimitive();
		this.isPublic = Modifier.isPublic(modifiers);
		this.isStatic = Modifier.isStatic(modifiers);
	}

	private void populateMethods() {
		if (methods == null) {
			synchronized (lockObject) {
				if (methods == null) {
					methods = TypeOfHelper.asMethodInfo(ImmutableSet.copyOf(wrapped.getMethods()));
				}
			}
		}
	}

	private void populateAnnotations() {
		if (annotations == null) {
			synchronized (lockObject) {
				if (annotations == null) {
					annotations = TypeOfHelper.asAnnotationInfo(ImmutableList.copyOf(wrapped.getAnnotations()));
				}
			}
		}
	}

	public <AType extends Annotation> ImmutableCollection<AnnotationInfo<AType>> getAnnotations(final Class<AType> annotationType) {
		checkNotNull(annotationType);
		populateAnnotations();

		final Iterable<AType> filtered = Iterables.filter(annotations, checkNotNull(annotationType));
		return TypeOfHelper.asAnnotationInfo(filtered);
	}

	public ImmutableCollection<MethodInfo> getMethods() {
		return ImmutableSet.copyOf(
			Iterables.filter(this.methods, Predicates.PublicMethods)
		);
	}

	public boolean isA(@Nullable final ClassInfo<?> classInfo) {
		return classInfo != null && classInfo.wrapped.isAssignableFrom(this.wrapped);
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return fullName;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public boolean isAnnotation() {
		return isAnnotation;
	}

	public boolean isArray() {
		return isArray;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public boolean isGeneric() {
		return isGeneric;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public boolean isNested() {
		return isNested;
	}

	public boolean isPrimitive() {
		return isPrimitive;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isStatic() {
		return isStatic;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("ClassInfo");
		sb.append("{name='").append(name).append('\'');
		sb.append('}');
		return sb.toString();
	}


	@SuppressWarnings({"unchecked"})
	static <CType extends Class<CType>> ClassInfo<CType>[] emptyArray() {
		return (ClassInfo<CType>[]) EmptyArray;
	}
}
