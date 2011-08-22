package nextmethod.web.routing;

import nextmethod.NotImplementedException;
import nextmethod.web.IHttpHandler;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:38 PM
 */
public class StopRoutingHandler implements IRouteHandler {

	@Override
	public IHttpHandler getHttpHandler(final RequestContext requestContext) {
		throw new NotImplementedException();
	}

}
