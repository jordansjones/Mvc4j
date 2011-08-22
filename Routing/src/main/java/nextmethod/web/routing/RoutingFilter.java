package nextmethod.web.routing;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class RoutingFilter implements Filter {

	private FilterConfig filterConfig;
	private RouteCollection routes;


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.routes = RouteTable.getRoutes();
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
//		final ServletContext servletContext = filterConfig.getServletContext();
//		final HttpRequest request = new HttpRequest(HttpServletRequest.class.cast(servletRequest), servletContext);
//		final HttpServletResponse response = HttpServletResponse.class.cast(servletResponse);
//		final HttpContext httpContext = new HttpContext(servletContext, request, response);
//
//		final RouteData routeData = routes.getRouteData(httpContext);
//		if (routeData == null)
//			throw new HttpException("The incoming request does not match any route");
//		if (routeData.getRouteHandler() == null)
//			throw new InvalidOperationException("No IRouteHandler is assigned to the selected route");
//
//		final RequestContext requestContext = new RequestContext(httpContext, routeData);
//		final IHttpHandler httpHandler = routeData.getRouteHandler().getHttpHandler(requestContext);

	}

	@Override
	public void destroy() {
		filterConfig = null;
	}

}
