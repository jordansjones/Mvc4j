package nextmethod.web.mvc;

import nextmethod.web.http.routing.*;
import nextmethod.web.http.routing.HttpRouteValueDictionary;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public final class RouteHelper {

	private RouteHelper() {
	}

	public static void ignoreRoute(final RouteCollection routes, final String url) {
		ignoreRoute(routes, url, null);
	}

	public static void ignoreRoute(final RouteCollection routes, final String url, @Nullable final HttpRouteValueDictionary constraints) {
		checkNotNull(routes);
		checkNotNull(url);

		final IgnoreRouteInternal route = new IgnoreRouteInternal(url);
		route.setConstraints(constraints);

		routes.add(route);
	}


	public static Route mapRoute(final RouteCollection routes, final String name, final String url) {
		return mapRoute(routes, name, url, null, null, null);
	}

	public static Route mapRoute(final RouteCollection routes, final String name, final String url, @Nullable final HttpRouteValueDictionary defaults) {
		return mapRoute(routes, name, url, defaults, null, null);
	}

	public static Route mapRoute(final RouteCollection routes, final String name, final String url, @Nullable final HttpRouteValueDictionary defaults, @Nullable final HttpRouteValueDictionary constraints) {
		return mapRoute(routes, name, url, defaults, constraints, null);
	}

	public static Route mapRoute(final RouteCollection routes, final String name, final String url, @Nullable final String[] packages) {
		return mapRoute(routes, name, url, null, null, packages);
	}

	public static Route mapRoute(final RouteCollection routes, final String name, final String url, @Nullable final HttpRouteValueDictionary defaults, @Nullable final String[] packages) {
		return mapRoute(routes, name, url, defaults, null, packages);
	}

	public static Route mapRoute(final RouteCollection routes, final String name, final String url, @Nullable final HttpRouteValueDictionary defaults, @Nullable final HttpRouteValueDictionary constraints, @Nullable final String[] packages) {
		checkNotNull(routes);
		checkNotNull(url);

		final Route route = new Route(url, new HttpRouteValueDictionary(defaults), new HttpRouteValueDictionary(constraints), new MvcRouteHandler());
		final HttpRouteValueDictionary dt = new HttpRouteValueDictionary();
		if (packages != null && packages.length > 0) {
			dt.put(MagicStrings.PackagesKey, packages);
		}
		route.setDataTokens(dt);

		routes.add(name, route);
		return route;
	}

	private static final class IgnoreRouteInternal extends Route {

		public IgnoreRouteInternal(final String url) {
			super(url, new StopRoutingHandler());
		}

		@Override
		public VirtualPathData getVirtualPath(final RequestContext requestContext, final HttpRouteValueDictionary values) {
			// Never match during route generation. This avoids the scenario where an IgnoreRoute with
			// fairly relaxed constraints ends up eagerly matching all generated Urls.
			return null;
		}
	}

}
