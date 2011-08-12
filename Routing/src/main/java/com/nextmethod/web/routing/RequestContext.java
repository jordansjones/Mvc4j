package com.nextmethod.web.routing;

import com.nextmethod.web.IHttpContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:31 PM
 */
public class RequestContext {

	private IHttpContext httpContext;
	private RouteData routeData;

	public RequestContext() {
		this.httpContext = null;
		this.routeData = null;
	}

	public RequestContext(@NotNull final IHttpContext httpContext, @NotNull final RouteData routeData) {
		this.httpContext = checkNotNull(httpContext);
		this.routeData = checkNotNull(routeData);
	}

	public IHttpContext getHttpContext() {
		return httpContext;
	}

	public void setHttpContext(@Nullable final IHttpContext httpContext) {
		this.httpContext = httpContext;
	}

	public RouteData getRouteData() {
		return routeData;
	}

	public void setRouteData(@Nullable final RouteData routeData) {
		this.routeData = routeData;
	}

	public String getContentEncoding() {
		return httpContext.getRequest().getContentEncoding();
	}
}
