package nextmethod.web.mvc;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class ExceptionContext extends ControllerContext {

	private ActionResult result = EmptyResult.instance();
	private Exception exception;
	private boolean exceptionHandled;

	public ExceptionContext() {
	}

	public ExceptionContext(final ControllerContext controllerContext, final Exception exception) {
		super(controllerContext);
		this.exception = checkNotNull(exception);
	}

	public Exception getException() {
		return exception;
	}

	public void setException(final Exception exception) {
		this.exception = exception;
	}

	public boolean isExceptionHandled() {
		return exceptionHandled;
	}

	public void setExceptionHandled(final boolean exceptionHandled) {
		this.exceptionHandled = exceptionHandled;
	}

	public ActionResult getResult() {
		return result;
	}

	public void setResult(final ActionResult result) {
		this.result = result == null ? EmptyResult.instance() : result;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof ExceptionContext)) return false;

		final ExceptionContext that = (ExceptionContext) o;

		if (exceptionHandled != that.exceptionHandled) return false;
		if (!exception.equals(that.exception)) return false;
		if (!result.equals(that.result)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result1 = result.hashCode();
		result1 = 31 * result1 + exception.hashCode();
		result1 = 31 * result1 + (exceptionHandled ? 1 : 0);
		return result1;
	}
}
