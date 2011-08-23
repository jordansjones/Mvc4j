package nextmethod.web.routing;

import nextmethod.web.IHttpHandler;

/**
 *
 */
public interface IRouteHandler {

	IHttpHandler getHttpHandler(RequestContext requestContext);

}
