package nextmethod.web.mvc;

/**
 *
 */
public interface IExceptionFilter extends IMvcFilter {

	void onException(final ExceptionContext filterContext);
}
