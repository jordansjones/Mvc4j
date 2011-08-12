package com.nextmethod.web.routing;

import com.nextmethod.web.IHttpContext;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:33 PM
 */
public interface IRouteConstraint {

	boolean match(IHttpContext context, Route route, String parameterName, RouteValueDictionary values, RouteDirection routeDirection);

}
