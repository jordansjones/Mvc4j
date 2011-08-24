package nextmethod.web.routing;

import nextmethod.web.IHttpContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 */
public class RequestContext {

	private IHttpContext httpContext;
	private RouteData routeData;

	public RequestContext() {
		this.httpContext = null;
		this.routeData = null;
	}

	public RequestContext(@Nonnull final IHttpContext httpContext, @Nonnull final RouteData routeData) {
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
