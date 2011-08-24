package nextmethod.web.mvc;


import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Injector;
import nextmethod.web.HttpException;
import nextmethod.web.HttpRequest;
import nextmethod.web.IHttpApplication;
import nextmethod.web.IHttpContext;
import nextmethod.web.annotations.HttpApplicationStart;
import nextmethod.web.routing.RouteCollection;
import nextmethod.web.routing.RouteData;
import nextmethod.web.routing.RouteTable;

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

		final RouteData routeData = routes.getRouteData(httpContext);
		if (routeData == null)
			throw new HttpException("The incoming request does not match any route");

		if (routeData.getRouteHandler() == null)
			throw new HttpException("No IRouteHandler is assigned to the selected route");

		request.setAttribute(HttpRequest.ROUTE_DATA_KEY, routeData);

		chain.doFilter(request, response);
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
