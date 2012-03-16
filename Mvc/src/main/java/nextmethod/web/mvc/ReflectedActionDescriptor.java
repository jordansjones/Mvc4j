package nextmethod.web.mvc;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import nextmethod.collect.IDictionary;
import nextmethod.reflection.MethodInfo;
import nextmethod.reflection.ParameterInfo;

import javax.annotation.Nullable;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class ReflectedActionDescriptor extends ActionDescriptor {

	private final String actionName;
	private final ControllerDescriptor controllerDescriptor;
	private final MethodInfo actionMethod;

	public ReflectedActionDescriptor(final MethodInfo actionMethod, final String actionName, final ControllerDescriptor controllerDescriptor) {
		this(actionMethod, actionName, controllerDescriptor, true);
	}

	ReflectedActionDescriptor(final MethodInfo actionMethod, final String actionName, final ControllerDescriptor controllerDescriptor, final boolean validateMethod) {
		this.actionMethod = checkNotNull(actionMethod);

		this.actionName = checkNotNull(actionName);

		this.controllerDescriptor = checkNotNull(controllerDescriptor);
		if (validateMethod) {
			final String failedMessage = verifyActionMethodIsCallable(actionMethod);
			if (!Strings.isNullOrEmpty(failedMessage)) {
				throw new IllegalArgumentException(failedMessage);
			}
		}
	}

	@Override
	public Object execute(final ControllerContext controllerContext, final IDictionary<String, Object> parameters) {
		checkNotNull(controllerContext);
		checkNotNull(parameters);

		final ParameterInfo[] parameterInfos = actionMethod.getParameters();
		final Iterable<Object> objects = Iterables.transform(Arrays.asList(parameterInfos), new Function<ParameterInfo, Object>() {
			@Override
			public Object apply(@Nullable final ParameterInfo input) {
				if (input == null) return null;
				return extractParameterFromIDictionary(input, parameters, actionMethod);
			}
		});

		final ActionMethodDispatcher dispatcher = getDispatcherCache().getDispatcher(actionMethod);
		return dispatcher.execute(controllerContext.getController(), Iterables.toArray(objects, Object.class));
	}

	@Override
	public String getActionName() {
		return actionName;
	}

	@Override
	public ControllerDescriptor getControllerDescriptor() {
		return controllerDescriptor;
	}

	@Override
	public ParameterDescriptor[] getParameters() {
		return new ParameterDescriptor[0];
	}

	@Override
	public FilterInfo getFilters() {
		return getFilters(this.actionMethod);
	}
}
