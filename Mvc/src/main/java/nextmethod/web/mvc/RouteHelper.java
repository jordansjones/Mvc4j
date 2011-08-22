package nextmethod.web.mvc;

import nextmethod.web.routing.RequestContext;
import nextmethod.web.routing.Route;
import nextmethod.web.routing.RouteCollection;
import nextmethod.web.routing.RouteValueDictionary;
import nextmethod.web.routing.StopRoutingHandler;
import nextmethod.web.routing.VirtualPathData;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public final class RouteHelper {

	private RouteHelper() {
	}

	public static void ignoreRoute(final RouteCollection routes, final String url) {
		ignoreRoute(routes, url, null);
	}

	public static void ignoreRoute(final RouteCollection routes, final String url, @Nullable final RouteValueDictionary constraints) {
		checkNotNull(routes);
		checkNotNull(url);

		final IgnoreRouteInternal route = new IgnoreRouteInternal(url);
		route.setConstraints(constraints);

		routes.add(route);
	}


	public static Route mapRoute(final RouteCollection routes, final String name, final String url) {
		return mapRoute(routes, name, url, null, null, null);
	}

	public static Route mapRoute(final RouteCollection routes, final String name, final String url, @Nullable final RouteValueDictionary defaults) {
		return mapRoute(routes, name, url, defaults, null, null);
	}

	public static Route mapRoute(final RouteCollection routes, final String name, final String url, @Nullable final RouteValueDictionary defaults, @Nullable final RouteValueDictionary constraints) {
		return mapRoute(routes, name, url, defaults, constraints, null);
	}

	public static Route mapRoute(final RouteCollection routes, final String name, final String url, @Nullable final String[] namespaces) {
		return mapRoute(routes, name, url, null, null, namespaces);
	}

	public static Route mapRoute(final RouteCollection routes, final String name, final String url, @Nullable final RouteValueDictionary defaults, @Nullable final String[] namespaces) {
		return mapRoute(routes, name, url, defaults, null, namespaces);
	}

	public static Route mapRoute(final RouteCollection routes, final String name, final String url, @Nullable final RouteValueDictionary defaults, @Nullable final RouteValueDictionary constraints, @Nullable final String[] namespaces) {
		checkNotNull(routes);
		checkNotNull(url);

		final Route route = new Route(url, new RouteValueDictionary(defaults), new RouteValueDictionary(constraints), new MvcRouteHandler());
		if (namespaces != null && namespaces.length > 0) {
			final RouteValueDictionary dt = new RouteValueDictionary();
			dt.put("Namespaces", namespaces);
			route.setDataTokens(dt);
		}

		routes.add(name, route);
		return route;
	}

	private static final class IgnoreRouteInternal extends Route {

		public IgnoreRouteInternal(final String url) {
			super(url, new StopRoutingHandler());
		}

		@Override
		public VirtualPathData getVirtualPath(final RequestContext requestContext, final RouteValueDictionary values) {
			// Never match during route generation. This avoids the scenario where an IgnoreRoute with
			// fairly relaxed constraints ends up eagerly matching all generated Urls.
			return null;
		}
	}

}
