package nextmethod.web.mvc;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
final class ActionMethodFilterProcessor {

	private final List<IActionFilter> actionFilters;
	private final IActionMethodFunction methodFunction;

	ActionMethodFilterProcessor(final List<IActionFilter> actionFilters, final IActionMethodFunction methodFunction) {
		this.actionFilters = checkNotNull(actionFilters);
		this.methodFunction = checkNotNull(methodFunction);
	}

	public ActionExecutedContext process(@Nonnull final ActionExecutingContext preContext,
	                                     @Nonnull final ControllerContext controllerContext,
	                                     @Nonnull final ActionDescriptor actionDescriptor,
	                                     final Map<String, Object> parameters) {
		checkNotNull(preContext);
		checkNotNull(controllerContext);
		checkNotNull(actionDescriptor);

		if (actionFilters.isEmpty()) {
			return invokeActionMethod(controllerContext, actionDescriptor, parameters);
		}

		ActionExecutedContext postContext = null;

		final Stack<IActionFilter> preInvoked = new Stack<IActionFilter>();
		invokeFilterExecuting(preContext, preInvoked);
		if (preContext.getResult() != null) {
			postContext = new ActionExecutedContext(controllerContext, actionDescriptor, true, null);
		}
		// Only invoke the action method if it wasn't handled by a filter's onActionExecuting
		if (postContext == null) {
			postContext = invokeActionMethod(controllerContext, actionDescriptor, parameters);
		}

		invokeFilterExecuted(postContext, preInvoked);
		return postContext;
	}

	private void invokeFilterExecuting(final ActionExecutingContext preContext, final Stack<IActionFilter> invoked) {
		for (IActionFilter filter : actionFilters) {
			filter.onActionExecuting(preContext);
			if (preContext.getResult() != null) {
				break;
			}
			invoked.push(filter);
		}
	}

	private static void invokeFilterExecuted(final ActionExecutedContext postContext, final Stack<IActionFilter> filters) {
		// Technically the following shouldn't happen, but we want to ensure
		checkNotNull(postContext);
		while (!filters.isEmpty()) {
			filters.pop().onActionExecuted(postContext);
		}
	}

	private ActionExecutedContext invokeActionMethod(final ControllerContext controllerContext, final ActionDescriptor actionDescriptor, final Map<String, Object> parameters) {
		final ActionExecutedContext context = new ActionExecutedContext(controllerContext, actionDescriptor, false, null);
		context.setResult(methodFunction.invoke(controllerContext, actionDescriptor, parameters));
		return context;
	}

	static interface IActionMethodFunction {

		ActionResult invoke(ControllerContext context, ActionDescriptor actionDescriptor, Map<String, Object> parameters);
	}
}
