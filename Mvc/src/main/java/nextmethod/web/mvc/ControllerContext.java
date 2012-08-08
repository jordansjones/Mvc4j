package nextmethod.web.mvc;

import nextmethod.web.IHttpContext;
import nextmethod.web.http.routing.RequestContext;
import nextmethod.web.http.routing.RouteData;

/**
 * 
 */
public class ControllerContext {

	private ControllerBase controller;
	private IHttpContext httpContext;
	private RequestContext requestContext;
	private RouteData routeData;

	public ControllerContext() {
	}

	protected ControllerContext(final ControllerContext controllerContext) {
		this(controllerContext.getRequestContext(), controllerContext.getController());
	}

	public ControllerContext(final RequestContext requestContext, final ControllerBase controllerBase) {
		this.requestContext = requestContext;
		this.routeData = requestContext.getRouteData();
		this.httpContext = requestContext.getHttpContext();
		this.controller = controllerBase;
	}

	public ControllerContext(final IHttpContext httpContext, final RouteData routeData, final ControllerBase controllerBase) {
		this(new RequestContext(httpContext, routeData), controllerBase);
	}

	public boolean isChildAction() {
		return false;
	}

	public ViewContext parentActionViewContext() {
		return null;
	}


	public ControllerBase getController() {
		return controller;
	}

	public void setController(final ControllerBase controller) {
		this.controller = controller;
	}

	public IHttpContext getHttpContext() {
		return httpContext;
	}

	public void setHttpContext(final IHttpContext httpContext) {
		this.httpContext = httpContext;
	}

	public RequestContext getRequestContext() {
		return requestContext;
	}

	public void setRequestContext(final RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	public RouteData getRouteData() {
		return routeData;
	}

	public void setRouteData(final RouteData routeData) {
		this.routeData = routeData;
	}
}
