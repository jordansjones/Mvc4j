package com.nextmethod.web.mvc;

import com.google.inject.Injector;
import com.nextmethod.TypeHelpers;
import com.nextmethod.web.HttpException;
import com.nextmethod.web.HttpRequest;
import com.nextmethod.web.IHttpContext;
import com.nextmethod.web.IHttpHandler;
import com.nextmethod.web.routing.IRouteHandler;
import com.nextmethod.web.routing.RequestContext;
import com.nextmethod.web.routing.RouteData;

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
	private IRouteHandler routeHandler;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final IHttpContext httpContext = injector.getInstance(IHttpContext.class);
		final RouteData data = TypeHelpers.typeAs(req.getAttribute(HttpRequest.ROUTE_DATA_KEY), RouteData.class);

		if (data == null)
			throw new HttpException("No route found"); // This should be a 404?

		final IHttpHandler handler = routeHandler.getHttpHandler(new RequestContext(httpContext, data));
		handler.processRequest(httpContext);
	}
}
