package com.nextmethod.mvc;

import com.nextmethod.routing.RequestContext;
import com.nextmethod.routing.RouteData;
import com.nextmethod.web.HttpContext;

/**
 * User: Jordan
 * Date: 8/6/11
 * Time: 12:02 AM
 */
public class ControllerContext {

	private ControllerBase controller;
	private HttpContext httpContext;
	private RequestContext requestContext;
	private RouteData routeData;

	public ControllerContext() {
	}

	protected ControllerContext(final ControllerContext controllerContext) {
	}

	public ControllerContext(final RequestContext requestContext, final ControllerBase controllerBase) {
	}

	public ControllerContext(final HttpContext httpContext, final RouteData routeData, final ControllerBase controllerBase) {
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

	public HttpContext getHttpContext() {
		return httpContext;
	}

	public void setHttpContext(final HttpContext httpContext) {
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
