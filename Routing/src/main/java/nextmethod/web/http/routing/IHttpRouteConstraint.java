package nextmethod.web.http.routing;

import nextmethod.collect.IDictionary;
import nextmethod.web.IHttpContext;

/**
 * 
 */
public interface IHttpRouteConstraint {

	boolean match(IHttpContext context, IHttpRoute route, String parameterName, IDictionary<String, Object> values, HttpRouteDirection httpRouteDirection);

}
