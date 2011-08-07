package com.nextmethod.routing;

import com.nextmethod.web.HttpContext;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:33 PM
 */
public interface IRouteConstraint {

	boolean match(HttpContext context, Route route, String parameterName, RouteValueDictionary values, RouteDirection routeDirection);

}
