package nextmethod.reflection;

import java.lang.annotation.Annotation;

/**
 *
 */
public final class AnnotationInfo<T extends Annotation> extends WrappedInfo<T> {

	static AnnotationInfo[] EmptyArray = new AnnotationInfo[0];

	public AnnotationInfo(final T annotation) {
		super(annotation);
	}

	public String getName() {
		return this.wrapped.annotationType().getSimpleName();
	}

	@SuppressWarnings({"unchecked"})
	static <AType extends Annotation> AnnotationInfo<AType>[] emptyArray() {
		return (AnnotationInfo<AType>[]) EmptyArray;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("AnnotationInfo");
		sb.append("{name='").append(getName()).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
