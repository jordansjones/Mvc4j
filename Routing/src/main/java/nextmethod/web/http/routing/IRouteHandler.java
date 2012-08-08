package nextmethod.web.http.routing;

import nextmethod.web.IHttpHandler;

/**
 *
 */
public interface IRouteHandler {

	IHttpHandler getHttpHandler(RequestContext requestContext);

}
