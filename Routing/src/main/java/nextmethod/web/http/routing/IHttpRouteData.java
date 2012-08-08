package nextmethod.web.http.routing;

import nextmethod.collect.IDictionary;

/**
 *
 */
public interface IHttpRouteData {

	IHttpRoute getRoute();

	IDictionary<String, Object> getValues();

}
