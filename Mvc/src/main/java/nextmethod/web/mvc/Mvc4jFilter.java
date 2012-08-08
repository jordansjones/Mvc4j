package nextmethod.web.mvc;


import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Injector;
import nextmethod.web.HttpException;
import nextmethod.web.IHttpApplication;
import nextmethod.web.IHttpContext;
import nextmethod.web.IHttpHandler;
import nextmethod.web.annotations.HttpApplicationStart;
import nextmethod.web.http.routing.IRouteHandler;
import nextmethod.web.http.routing.RequestContext;
import nextmethod.web.http.routing.RouteCollection;
import nextmethod.web.http.routing.RouteData;
import nextmethod.web.http.routing.RouteTable;
import nextmethod.web.http.routing.StopRoutingHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static nextmethod.TypeHelpers.typeIs;

@Singleton
class Mvc4jFilter implements Filter {

	@Inject
	private IHttpApplication httpApplication;
	@Inject
	private Injector injector;

	private RouteCollection routes;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.routes = RouteTable.getRoutes();
		initializeHttpApplication();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		final IHttpContext httpContext = injector.getInstance(IHttpContext.class);
		final IHttpHandler httpHandler = getHttpHandler(httpContext);

		if (httpHandler != null) {
			injector.injectMembers(httpHandler);
			httpHandler.processRequest(httpContext);
		} else {
			chain.doFilter(request, response);
		}
	}

	private IHttpHandler getHttpHandler(final IHttpContext httpContext) throws HttpException {
		final RouteData routeData = routes.getRouteData(httpContext);
		if (routeData == null)
			return null;

		final IRouteHandler routeHandler = getRouteHandler(routeData);
		if (routeHandler == null)
			throw new HttpException("No IRouteHandler is assigned to the selected route");

		if (typeIs(routeHandler, StopRoutingHandler.class))
			return null;

		return routeHandler.getHttpHandler(new RequestContext(httpContext, routeData));
	}

	private IRouteHandler getRouteHandler(final RouteData data) {
		final IRouteHandler routeHandler = data.getRouteHandler();
		if (routeHandler == null)
			return null;

		injector.injectMembers(routeHandler);
		return routeHandler;
	}

	@Override
	public void destroy() {
	}

	private void initializeHttpApplication() {
		final ImmutableMultimap<Class<? extends Annotation>, Method> map = loadEventMethods(httpApplication.getClass().getDeclaredMethods());

		// Application Start
		if (map.containsKey(HTTP_APPLICATION_START_CLASS))
			fireEvent(map.get(HTTP_APPLICATION_START_CLASS));

		// Other Application Events
	}

	private void fireEvent(final ImmutableCollection<Method> methods) {
		for (Method method : methods) {
			if (!method.isAccessible())
				method.setAccessible(true);

			try {
				method.invoke(httpApplication);
			}
			catch (IllegalAccessException e) {
				// TODO: Do something
			}
			catch (InvocationTargetException e) {
				// TODO: Do something
			}
		}
	}

	private static final Class<HttpApplicationStart> HTTP_APPLICATION_START_CLASS = HttpApplicationStart.class;

	private ImmutableMultimap<Class<? extends Annotation>, Method> loadEventMethods(final Method[] methods) {

		final ImmutableMultimap.Builder<Class<? extends Annotation>, Method> builder = ImmutableMultimap.builder();
		for (Method method : ImmutableList.copyOf(methods)) {
			final Annotation[] annotations = method.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				final Class<? extends Annotation> aClass = annotation.annotationType();
				if (HTTP_APPLICATION_START_CLASS.isAssignableFrom(aClass)) {
					builder.put(aClass, method);
				}
			}
		}

		return builder.build();
	}
}
