package nextmethod.web.mvc;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import nextmethod.web.routing.Route;
import nextmethod.web.routing.RouteCollection;
import nextmethod.web.routing.RouteValueDictionary;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class AreaRegistrationContext {

	private final Set<String> packages = Sets.newHashSet();

	private final String areaName;
	private final RouteCollection routes;
	private final Object state;

	public AreaRegistrationContext(final String areaName, final RouteCollection routes) {
		this(areaName, routes, null);
	}

	public AreaRegistrationContext(final String areaName, final RouteCollection routes, @Nullable final Object state) {
		this.areaName = checkNotNull(areaName);
		this.routes = checkNotNull(routes);
		this.state = state;
	}

	// ************************************************************************
	// MapRoute methods
	// ************************************************************************

	public Route mapRoute(final String name, final String url) {
		return mapRoute(name, url, (RouteValueDictionary) null);
	}

	public Route mapRoute(final String name, final String url, @Nullable RouteValueDictionary defaults) {
		return mapRoute(name, url, defaults, (RouteValueDictionary) null);
	}

	public Route mapRoute(final String name, final String url, @Nullable final RouteValueDictionary defaults, @Nullable final RouteValueDictionary constraints) {
		return mapRoute(name, url, defaults, constraints, null);
	}

	public Route mapRoute(final String name, final String url, @Nullable final String[] packages) {
		return mapRoute(name, url, null, packages);
	}

	public Route mapRoute(final String name, final String url, @Nullable final RouteValueDictionary defaults, @Nullable final String[] packages) {
		return mapRoute(name, url, defaults, null, packages);
	}

	public Route mapRoute(final String name, final String url, @Nullable final RouteValueDictionary defaults, @Nullable final RouteValueDictionary constraints, @Nullable String[] packages) {
		if (packages == null && this.packages != null) {
			packages = Iterables.toArray(this.packages, String.class);
		}

		final Route route = RouteHelper.mapRoute(this.routes, name, url, defaults, constraints, packages);
		route.getDataTokens().put(MagicStrings.AreaKey, this.areaName);

		// disabling the package lookup fallback mechanism keeps this areas from accidentally picking up
		// controllers belonging to other areas
		final boolean usePackageFallback = packages == null || packages.length == 0;
		route.getDataTokens().put(MagicStrings.UsePackageFallbackKey, usePackageFallback);
		return route;
	}

	// ************************************************************************
	// Property methods
	// ************************************************************************


	public String getAreaName() {
		return areaName;
	}

	public Collection<String> getPackages() {
		return packages;
	}

	public RouteCollection getRoutes() {
		return routes;
	}

	public Object getState() {
		return state;
	}
}
