package nextmethod.web.mvc;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class AuthorizationContext extends ControllerContext {

	private ActionDescriptor actionDescriptor;

	private ActionResult result;

	public AuthorizationContext(final ControllerContext controllerContext, final ActionDescriptor actionDescriptor) {
		super(controllerContext);
		this.actionDescriptor = checkNotNull(actionDescriptor);
	}

	public ActionDescriptor getActionDescriptor() {
		return actionDescriptor;
	}

	public void setActionDescriptor(final ActionDescriptor actionDescriptor) {
		this.actionDescriptor = actionDescriptor;
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
		if (!(o instanceof AuthorizationContext)) return false;

		final AuthorizationContext that = (AuthorizationContext) o;

		if (actionDescriptor != null ? !actionDescriptor.equals(that.actionDescriptor) : that.actionDescriptor != null)
			return false;
		if (result != null ? !result.equals(that.result) : that.result != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result1 = actionDescriptor != null ? actionDescriptor.hashCode() : 0;
		result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
		return result1;
	}
}
