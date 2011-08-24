package nextmethod.web.routing;

import nextmethod.NotImplementedException;
import nextmethod.web.IHttpHandler;

/**
 * 
 */
public class StopRoutingHandler implements IRouteHandler {

	@Override
	public IHttpHandler getHttpHandler(final RequestContext requestContext) {
		throw new NotImplementedException();
	}

}
