package nextmethod.web.mvc;

/**
 *
 */
public interface IActionFilter extends IMvcFilter {

	void onActionExecuting(final ActionExecutingContext filterContext);

	void onActionExecuted(final ActionExecutedContext filterContext);
}
