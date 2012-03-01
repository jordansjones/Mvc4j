package filters;

import nextmethod.web.mvc.ActionExecutedContext;
import nextmethod.web.mvc.ActionExecutingContext;
import nextmethod.web.mvc.IActionFilter;

/**
 *
 */
public final class ThreeActionFilter implements IActionFilter {

	@Override
	public void onActionExecuting(final ActionExecutingContext filterContext) {
		int x = 1;
	}

	@Override
	public void onActionExecuted(final ActionExecutedContext filterContext) {
		int y = 1;
	}
}
