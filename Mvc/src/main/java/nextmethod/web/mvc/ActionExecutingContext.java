package nextmethod.web.mvc;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class ActionExecutingContext extends ControllerContext {

	private ActionDescriptor actionDescriptor;
	private Map<String, Object> actionParameters;
	private ActionResult result;

	public ActionExecutingContext() {
	}

	public ActionExecutingContext(final ControllerContext controllerContext, final ActionDescriptor actionDescriptor, final Map<String, Object> actionParameters) {
		super(controllerContext);
		this.actionDescriptor = checkNotNull(actionDescriptor);
		this.actionParameters = checkNotNull(actionParameters);
	}

	public ActionDescriptor getActionDescriptor() {
		return actionDescriptor;
	}

	public void setActionDescriptor(final ActionDescriptor actionDescriptor) {
		this.actionDescriptor = actionDescriptor;
	}

	public Map<String, Object> getActionParameters() {
		return actionParameters;
	}

	public void setActionParameters(final Map<String, Object> actionParameters) {
		this.actionParameters = actionParameters;
	}

	public ActionResult getResult() {
		return result;
	}

	public void setResult(final ActionResult result) {
		this.result = result;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof ActionExecutingContext)) return false;

		final ActionExecutingContext that = (ActionExecutingContext) o;

		if (actionDescriptor != null ? !actionDescriptor.equals(that.actionDescriptor) : that.actionDescriptor != null)
			return false;
		if (actionParameters != null ? !actionParameters.equals(that.actionParameters) : that.actionParameters != null)
			return false;
		if (result != null ? !result.equals(that.result) : that.result != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result1 = actionDescriptor != null ? actionDescriptor.hashCode() : 0;
		result1 = 31 * result1 + (actionParameters != null ? actionParameters.hashCode() : 0);
		result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
		return result1;
	}
}
