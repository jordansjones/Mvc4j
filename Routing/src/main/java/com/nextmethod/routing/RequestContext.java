package com.nextmethod.routing;

import com.nextmethod.web.HttpContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:31 PM
 */
public class RequestContext {

	private HttpContext httpContext;
	private RouteData routeData;

	public RequestContext() {
		this.httpContext = null;
		this.routeData = null;
	}

	public RequestContext(@NotNull final HttpContext httpContext, @NotNull final RouteData routeData) {
		this.httpContext = checkNotNull(httpContext);
		this.routeData = checkNotNull(routeData);
	}

	public HttpContext getHttpContext() {
		return httpContext;
	}

	public void setHttpContext(@Nullable final HttpContext httpContext) {
		this.httpContext = httpContext;
	}

	public RouteData getRouteData() {
		return routeData;
	}

	public void setRouteData(@Nullable final RouteData routeData) {
		this.routeData = routeData;
	}
}
