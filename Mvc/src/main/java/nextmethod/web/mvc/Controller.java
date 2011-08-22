package nextmethod.web.mvc;

import nextmethod.web.routing.RequestContext;
import nextmethod.web.routing.RouteData;

import javax.inject.Inject;

/**
 *
 */
public abstract class Controller extends ControllerBase {

	@Inject
	private IActionInvoker actionInvoker;

	@Override
	protected void initialize(final RequestContext requestContext) {
		super.initialize(requestContext);
		// TODO: new UrlHelper(requestContext);
	}

	public IActionInvoker getActionInvoker() {
		return actionInvoker;
	}

	public RouteData getRouteData() {
		final ControllerContext context = getControllerContext();
		return context == null ? null : context.getRouteData();
	}

	private static final String ActionKey = "action";

	@Override
	protected void executeCore() {
		// If code in this method needs to be updated, please also check the beginExecuteCore() and
		// endExecuteCore() methods of AsyncController to see if that code also must be updated.
		possiblyLoadTempData();
		try {
			final String actionName = getRouteData().getRequiredString(ActionKey);
			if (!getActionInvoker().invokeAction(getControllerContext(), actionName)) {
				handleUnknownAction(actionName);
			}
		}
		finally {
			possiblySaveTempData();
		}
	}

	protected void handleUnknownAction(final String actionName) {
		// TODO: This should be an HttpException with a message.
		throw new RuntimeException();
	}

	protected ViewResult view() {
		return null;
	}

	void possiblyLoadTempData() {
		// TODO: This
	}

	void possiblySaveTempData() {
		// TODO: This
	}
}
