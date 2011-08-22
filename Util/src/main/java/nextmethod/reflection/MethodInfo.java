package nextmethod.reflection;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import nextmethod.NotImplementedException;
import nextmethod.OutParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public final class MethodInfo extends MemberInfo<Method> {

	static MethodInfo[] EmptyArray = new MethodInfo[0];

	private final boolean isGeneric;
	private final boolean isVarArg;

	private final Object lockObject = new Object();

	private ImmutableList<ParameterInfo> parameters;
	private ImmutableCollection<Annotation> annotations;

	public MethodInfo(final Method method) {
		super(method);

		final TypeVariable<Method>[] typeParameters = method.getTypeParameters();

		this.isGeneric = typeParameters != null && typeParameters.length > 0;
		this.isVarArg = method.isVarArgs();

		populateParameters();
	}

	private void populateParameters() {
		if (parameters != null)
			return;

		final ImmutableList.Builder<ParameterInfo> builder = ImmutableList.builder();

		final Type[] genericParameterTypes = wrapped.getGenericParameterTypes();
		final Class<?>[] parameterTypes = wrapped.getParameterTypes();
		final Annotation[][] parameterAnnotations = wrapped.getParameterAnnotations();

		int x = 1;
	}

	private void populateAnnotations() {
		if (annotations == null) {
			synchronized (lockObject) {
				if (annotations == null) {
					annotations = ImmutableList.copyOf(wrapped.getAnnotations());
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

	public boolean isGeneric() {
		return isGeneric;
	}

	public boolean isVarArg() {
		return isVarArg;
	}

	public ClassInfo<?>[] getParameterTypes() {
		return null;
	}

	public boolean containsGenericParameters() {
		throw new NotImplementedException();
	}

	public ParameterInfo[] getParameters() {
		throw new NotImplementedException();
	}

	public ParameterInfo getReturnParameter() {
		throw new NotImplementedException();
	}

	public ClassInfo getReturnType() {
		throw new NotImplementedException();
	}

	public Object invoke(final Object obj, final Object... parameters) {
		final OutParam<Object> result = OutParam.of();
		if (tryInvoke(obj, parameters, result)) {
			return result.get();
		}
		return null;
	}

	public boolean tryInvoke(final Object obj, final Object[] parameters, final OutParam<Object> result) {
		throw new NotImplementedException();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("MethodInfo");
		sb.append("{name='").append(getName()).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
