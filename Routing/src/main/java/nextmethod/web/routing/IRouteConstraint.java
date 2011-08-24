package nextmethod.web.routing;

import nextmethod.web.IHttpContext;

/**
 * 
 */
public interface IRouteConstraint {

	boolean match(IHttpContext context, Route route, String parameterName, RouteValueDictionary values, RouteDirection routeDirection);

}
