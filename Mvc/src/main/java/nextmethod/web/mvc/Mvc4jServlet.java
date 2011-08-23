package nextmethod.web.mvc;

import com.google.inject.Injector;
import nextmethod.TypeHelpers;
import nextmethod.web.HttpException;
import nextmethod.web.HttpRequest;
import nextmethod.web.IHttpContext;
import nextmethod.web.IHttpHandler;
import nextmethod.web.routing.IRouteHandler;
import nextmethod.web.routing.RequestContext;
import nextmethod.web.routing.RouteData;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
class Mvc4jServlet extends HttpServlet {

	@Inject
	private Injector injector;

	@Inject
	private IRouteHandler defaultRouteHandler;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final IHttpContext httpContext = injector.getInstance(IHttpContext.class);
		final RouteData data = TypeHelpers.typeAs(req.getAttribute(HttpRequest.ROUTE_DATA_KEY), RouteData.class);

		if (data == null)
			throw new HttpException("No route found"); // This should be a 404?

		final IRouteHandler routeHandler = getRouteHandler(data);
		final IHttpHandler handler = routeHandler.getHttpHandler(new RequestContext(httpContext, data));
		handler.processRequest(httpContext);
	}

	private IRouteHandler getRouteHandler(final RouteData data) {
		final IRouteHandler routeHandler = data.getRouteHandler();
		if (routeHandler == null)
			return defaultRouteHandler;

		injector.injectMembers(routeHandler);
		return routeHandler;
	}
}
