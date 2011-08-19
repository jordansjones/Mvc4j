package com.nextmethod.web.mvc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 *
 */
final class ActionMethodDispatcher {

	private ActionExecutor<?> executor;

	ActionMethodDispatcher(final Method actionMethod) {
//		final Method actionMethod1 = actionMethod;
		this.executor = getExecutor(actionMethod);
	}

	public Object execute(final ControllerBase controllerBase, final Object[] parameters) {
		return executor.invoke(controllerBase, parameters);
	}

	private static ActionExecutor<?> getExecutor(final Method method) {

		final Type returnType = method.getGenericReturnType();
		if (Void.TYPE.equals(returnType)) {
			return createVoidActionExecutor(method);
		}
		return createReturningActionExecutor(method);
	}

	private static Object tryInvokeActionMethod(final Method method, final ControllerBase controllerBase, final Object[] parameters) {
		try {
			return method.invoke(controllerBase, parameters);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static ActionExecutor<Void> createVoidActionExecutor(final Method method) {
		return new ActionExecutor<Void>() {
			@Override
			public Void invoke(final ControllerBase controllerBase, final Object[] parameters) {
				tryInvokeActionMethod(method, controllerBase, parameters);
				return null;
			}
		};
	}

	private static ActionExecutor<Object> createReturningActionExecutor(final Method method) {
		return new ActionExecutor<Object>() {
			@Override
			public Object invoke(final ControllerBase controllerBase, final Object[] parameters) {
				return tryInvokeActionMethod(method, controllerBase, parameters);
			}
		};
	}
}
