package nextmethod.web.routing;

import nextmethod.web.HttpException;
import nextmethod.web.IHttpContext;
import nextmethod.web.IHttpHandler;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:40 PM
 */
public abstract class UrlRoutingHandler implements IHttpHandler {

	protected RouteCollection routeCollection;

	@Override
	public void processRequest(final IHttpContext httpContext) throws HttpException {
		final RouteData routeData = getRouteCollection().getRouteData(checkNotNull(httpContext));

		if (routeData == null)
			throw new HttpException("The incoming request does not match any route");

		if (routeData.getRouteHandler() == null)
			throw new NullPointerException("No IRouteHandler is assigned to the selected route");

		final RequestContext requestContext = new RequestContext(httpContext, routeData);
		final IHttpHandler iHttpHandler = routeData.getRouteHandler().getHttpHandler(requestContext);
		verifyAndProcessRequest(iHttpHandler, httpContext);
	}

	//	@Override
	public boolean isReusable() {
		return false;
	}

	protected abstract void verifyAndProcessRequest(IHttpHandler httpHandler, IHttpContext httpContext);

	public RouteCollection getRouteCollection() {
		if (routeCollection == null)
			routeCollection = RouteTable.getRoutes();

		return routeCollection;
	}

	public void setRouteCollection(final RouteCollection routeCollection) {
		this.routeCollection = routeCollection;
	}
}
