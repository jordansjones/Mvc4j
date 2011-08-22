package nextmethod.web.mvc;

/**
 *
 */
public interface IResultFilter extends IMvcFilter {

	void onResultExecuting(final ResultExecutingContext filterContext);

	void onResultExecuted(final ResultExecutedContext filterContext);
}
