package nextmethod.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

import static nextmethod.reflection.TypeOfHelper.typeOf;

/**
 *
 */
public class ParameterInfo<T> extends WrappedInfo<ClassInfo<T>> {
	
	static ParameterInfo[] EmptyArray = new ParameterInfo[0];

	private final String name;
	private final AnnotationInfo<? extends Annotation>[] annotations;

	public ParameterInfo(final Class<T> parameterType, final String name, final Annotation[] annotations) {
		super(typeOf(parameterType));
		this.name = name;
		final AnnotationInfo<? extends Annotation>[] aInfos;
		if (annotations == null || annotations.length < 1) {
			aInfos = AnnotationInfo.emptyArray();
		}
		else {
			aInfos = (AnnotationInfo<?>[]) Array.newInstance(AnnotationInfo.class, annotations.length);
			for (int i = 0; i < annotations.length; i++) {
				aInfos[i] = AnnotationInfo.of(annotations[i]);
			}
		}
		this.annotations = aInfos;
	}

	public String getName() {
		return this.name;
	}

	public ClassInfo<T> getParameterType() {
		return this.wrappedType();
	}


	@SuppressWarnings({"unchecked"})
	static <PType extends Class<PType>> ParameterInfo<PType>[] emptyArray() {
		return (ParameterInfo<PType>[]) EmptyArray;
	}

}
