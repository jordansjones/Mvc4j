package nextmethod.reflection;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.thoughtworks.paranamer.CachingParanamer;
import nextmethod.NotImplementedException;
import nextmethod.OutParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.reflection.TypeOfHelper.typeOf;

/**
 *
 */
public final class MethodInfo extends MemberInfo<Method> {

	static MethodInfo[] EmptyArray = new MethodInfo[0];

	private final boolean isGeneric;
	private final boolean isVarArg;

	private final Object lockObject = new Object();

	private ImmutableList<ParameterInfo<?>> parameters;
	private ImmutableCollection<Annotation> annotations;

	public MethodInfo(final Method method) {
		super(method);

		final TypeVariable<Method>[] typeParameters = method.getTypeParameters();

		this.isGeneric = typeParameters != null && typeParameters.length > 0;
		this.isVarArg = method.isVarArgs();

		populateParameters();
	}

	@SuppressWarnings("unchecked")
	private void populateParameters() {
		if (parameters == null) {
			synchronized (lockObject) {
				final ImmutableList.Builder<ParameterInfo<?>> builder = ImmutableList.builder();

//				final Type[] genericParameterTypes = wrapped.getGenericParameterTypes();
				final Class<?>[] parameterTypes = wrapped.getParameterTypes();
				final Annotation[][] parameterAnnotations = wrapped.getParameterAnnotations();

				final CachingParanamer paranamer = new CachingParanamer();
				final String[] paramNames = paranamer.lookupParameterNames(wrapped);

				if (parameterTypes.length == paramNames.length) {
					for (int i = 0; i < parameterTypes.length; i++) {
						final ParameterInfo pInfo = new ParameterInfo(parameterTypes[i], paramNames[i], parameterAnnotations[i]);
						builder.add(pInfo);
					}
				}
				this.parameters = builder.build();
			}
		}
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

	public ParameterInfo<?>[] getParameters() {
		return Iterables.toArray(this.parameters, ParameterInfo.class);
	}

	public ParameterInfo getReturnParameter() {
		throw new NotImplementedException();
	}

	public ClassInfo getReturnType() {
		return typeOf(this.wrapped.getReturnType());
	}

	public Object invoke(final Object obj, final Object... parameters) {
		final OutParam<Object> result = OutParam.of();
		if (tryInvoke(obj, parameters, result)) {
			return result.get();
		}
		return null;
	}

	public boolean tryInvoke(final Object obj, final Object[] parameters, final OutParam<Object> result) {
		try {
			final Object invokeResult = wrapped.invoke(obj, parameters);
			result.set(invokeResult);
			return true;
		}
		catch (IllegalAccessException e) {
			// TODO: Handle this somehow
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			// TODO: Handle this somehow
			e.printStackTrace();
		}
		return false;
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
