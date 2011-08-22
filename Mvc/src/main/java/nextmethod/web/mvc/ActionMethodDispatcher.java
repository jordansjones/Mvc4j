package nextmethod.web.mvc;

import nextmethod.OutParam;
import nextmethod.reflection.MethodInfo;

/**
 *
 */
final class ActionMethodDispatcher {

	private ActionExecutor<?> executor;

	ActionMethodDispatcher(final MethodInfo actionMethod) {
//		final Method actionMethod1 = actionMethod;
		this.executor = getExecutor(actionMethod);
	}

	public Object execute(final ControllerBase controllerBase, final Object[] parameters) {
		return executor.invoke(controllerBase, parameters);
	}

	private static ActionExecutor<?> getExecutor(final MethodInfo method) {
//		TODO: Fix this
//		final Type returnType = method.getGenericReturnType();
//		if (Void.TYPE.equals(returnType)) {
		return createVoidActionExecutor(method);
//		}
//		return createReturningActionExecutor(method);
	}

	private static Object tryInvokeActionMethod(final MethodInfo method, final ControllerBase controllerBase, final Object[] parameters) {
		final OutParam<Object> result = OutParam.of();
		if (method.tryInvoke(controllerBase, parameters, result)) {
			return result.get();
		}
		return null;
	}

	private static ActionExecutor<Void> createVoidActionExecutor(final MethodInfo method) {
		return new ActionExecutor<Void>() {
			@Override
			public Void invoke(final ControllerBase controllerBase, final Object[] parameters) {
				tryInvokeActionMethod(method, controllerBase, parameters);
				return null;
			}
		};
	}

	private static ActionExecutor<Object> createReturningActionExecutor(final MethodInfo method) {
		return new ActionExecutor<Object>() {
			@Override
			public Object invoke(final ControllerBase controllerBase, final Object[] parameters) {
				return tryInvokeActionMethod(method, controllerBase, parameters);
			}
		};
	}
}
