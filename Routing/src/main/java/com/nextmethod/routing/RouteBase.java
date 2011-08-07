package com.nextmethod.routing;

import com.nextmethod.web.HttpContext;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:32 PM
 */
public abstract class RouteBase {

	public abstract RouteData getRouteData(final HttpContext httpContext);

	public abstract VirtualPathData getVirtualPath(final RequestContext requestContext, final RouteValueDictionary values);

}
