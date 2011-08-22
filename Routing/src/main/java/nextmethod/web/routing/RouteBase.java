package nextmethod.web.routing;

import nextmethod.web.IHttpContext;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:32 PM
 */
public abstract class RouteBase {

	public abstract RouteData getRouteData(final IHttpContext httpContext);

	public abstract VirtualPathData getVirtualPath(final RequestContext requestContext, final RouteValueDictionary values);

}
