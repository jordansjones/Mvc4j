package nextmethod.web.routing;

import nextmethod.web.IHttpContext;

/**
 * 
 */
public abstract class RouteBase {

	public abstract RouteData getRouteData(final IHttpContext httpContext);

	public abstract VirtualPathData getVirtualPath(final RequestContext requestContext, final RouteValueDictionary values);

}
