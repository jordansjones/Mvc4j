package nextmethod.web.mvc;

import com.google.inject.Injector;
import nextmethod.web.HttpException;
import nextmethod.web.routing.RequestContext;
import nextmethod.web.routing.RouteData;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 *
 */
public abstract class Controller extends ControllerBase implements IActionFilter, IAuthorizationFilter, IExceptionFilter, IResultFilter {

	@Inject
	private IActionInvoker actionInvoker;
	@Inject
	private Injector injector;

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

	@Override
	protected void executeCore() throws HttpException{
		// If code in this method needs to be updated, please also check the beginExecuteCore() and
		// endExecuteCore() methods of AsyncController to see if that code also must be updated.
		possiblyLoadTempData();
		try {
			final String actionName = getRouteData().getRequiredString(MagicStrings.ActionKey);
			if (!getActionInvoker().invokeAction(getControllerContext(), actionName)) {
				handleUnknownAction(actionName);
			}
		}
		finally {
			possiblySaveTempData();
		}
	}

	protected void handleUnknownAction(final String actionName) throws HttpException {
        throw new HttpException(404, actionName + "not found.");
	}

	void possiblyLoadTempData() {
		// TODO: This
	}

	void possiblySaveTempData() {
		// TODO: This
	}

	// ************************************************************************
	// ViewResult Methods
	// ************************************************************************

	protected ViewResult view() {
		return view(null, null, null);
	}

	protected ViewResult view(final String viewName) {
		return view(viewName, null, null);
	}

	protected ViewResult view(final String viewName, final String masterName) {
		return view(viewName, masterName, null);
	}

	protected ViewResult view(@Nullable final String viewName, @Nullable final String masterName, @Nullable final Object model) {
		final ViewResult result = new ViewResult();
		result.setViewName(viewName);
		result.setMasterName(masterName);
		// TODO: ViewData
		// TODO: TempData

		injector.injectMembers(result);
		return result;
	}


	// ************************************************************************
	// IActionFilter Members
	// ************************************************************************

	@Override
	public void onActionExecuted(final ActionExecutedContext filterContext) {
	}

	@Override
	public void onActionExecuting(final ActionExecutingContext filterContext) {
	}


	// ************************************************************************
	// IAuthorizationFilter Members
	// ************************************************************************

	@Override
	public void onAuthorization(final AuthorizationContext filterContext) {
	}


	// ************************************************************************
	// IExceptionFilter Members
	// ************************************************************************

	@Override
	public void onException(final ExceptionContext filterContext) {
	}


	// ************************************************************************
	// IResultFilter Members
	// ************************************************************************

	@Override
	public void onResultExecuted(final ResultExecutedContext filterContext) {
	}

	@Override
	public void onResultExecuting(final ResultExecutingContext filterContext) {
	}

}
