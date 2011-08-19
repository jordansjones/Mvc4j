package com.nextmethod.web.mvc;

import java.lang.reflect.Method;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class ReflectedActionDescriptor extends ActionDescriptor {

	private final String actionName;
	private final ControllerDescriptor controllerDescriptor;
	private final Method actionMethod;

	public ReflectedActionDescriptor(final Method actionMethod, final String actionName, final ControllerDescriptor controllerDescriptor) {
		this(actionMethod, actionName, controllerDescriptor, true);
	}

	public ReflectedActionDescriptor(final Method actionMethod, final String actionName, final ControllerDescriptor controllerDescriptor, final boolean validateMethod) {
		this.actionMethod = actionMethod;
		this.actionName = actionName;
		this.controllerDescriptor = controllerDescriptor;
	}

	@Override
	public Object execute(final ControllerContext controllerContext, final Map<String, Object> parameters) {
		checkNotNull(controllerContext);
		checkNotNull(parameters);

		final Class<?>[] parameterTypes = actionMethod.getParameterTypes();
		// TODO: extractParameterFromMap(parameterInfo, parameters, actionMethod)

		final ActionMethodDispatcher dispatcher = getDispatcherCache().getDispatcher(actionMethod);
		final Object result = dispatcher.execute(controllerContext.getController(), new Object[0]);
		return result;
	}
}
