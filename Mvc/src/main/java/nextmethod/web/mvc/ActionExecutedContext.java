package nextmethod.web.mvc;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 */
public class ActionExecutedContext extends ControllerContext {

	private ActionResult result = EmptyResult.instance();
	private ActionDescriptor actionDescriptor;
	private boolean canceled;
	private Exception exception;
	private boolean exceptionHandled;

	public ActionExecutedContext() {
	}

	public ActionExecutedContext(final ControllerContext controllerContext, final ActionDescriptor actionDescriptor, final boolean canceled, final Exception exception) {
		super(controllerContext);
		this.actionDescriptor = checkNotNull(actionDescriptor);
		this.canceled = canceled;
		this.exception = exception;
	}

	public ActionDescriptor getActionDescriptor() {
		return actionDescriptor;
	}

	public void setActionDescriptor(final ActionDescriptor actionDescriptor) {
		this.actionDescriptor = actionDescriptor;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(final boolean canceled) {
		this.canceled = canceled;
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

	@SuppressWarnings({"SimplifiableIfStatement"})
	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof ActionExecutedContext)) return false;

		final ActionExecutedContext that = (ActionExecutedContext) o;

		if (canceled != that.canceled) return false;
		if (exceptionHandled != that.exceptionHandled) return false;
		if (actionDescriptor != null ? !actionDescriptor.equals(that.actionDescriptor) : that.actionDescriptor != null)
			return false;
		if (exception != null ? !exception.equals(that.exception) : that.exception != null) return false;

		return result.equals(that.result);
	}

	@Override
	public int hashCode() {
		int result1 = result.hashCode();
		result1 = 31 * result1 + (actionDescriptor != null ? actionDescriptor.hashCode() : 0);
		result1 = 31 * result1 + (canceled ? 1 : 0);
		result1 = 31 * result1 + (exception != null ? exception.hashCode() : 0);
		result1 = 31 * result1 + (exceptionHandled ? 1 : 0);
		return result1;
	}
}
