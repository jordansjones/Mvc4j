package com.nextmethod.web.routing;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:38 PM
 */
public class RouteData {

	private final RouteValueDictionary dataTokens;
	private final RouteValueDictionary values;

	private RouteBase route;
	private IRouteHandler routeHandler;

	public RouteData(final RouteBase route, final IRouteHandler routeHandler) {
		this.route = route;
		this.routeHandler = routeHandler;

		this.dataTokens = new RouteValueDictionary();
		this.values = new RouteValueDictionary();
	}

	public String getRequiredString(final String valueName) {
		return null;
	}

	public RouteValueDictionary getDataTokens() {
		return dataTokens;
	}

	public RouteValueDictionary getValues() {
		return values;
	}

	public RouteBase getRoute() {
		return route;
	}

	public void setRoute(final RouteBase route) {
		this.route = route;
	}

	public IRouteHandler getRouteHandler() {
		return routeHandler;
	}

	public void setRouteHandler(final IRouteHandler routeHandler) {
		this.routeHandler = routeHandler;
	}
}
