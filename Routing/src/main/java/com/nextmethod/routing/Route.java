package com.nextmethod.routing;

import com.nextmethod.web.HttpContext;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:33 PM
 */
public class Route extends RouteBase {

	private String url;
	private RouteValueDictionary constraints;
	private RouteValueDictionary dataTokens;
	private RouteValueDictionary defaults;
	private IRouteHandler routeHandler;

	public Route(final String url, final IRouteHandler routeHandler) {
		this(url, new RouteValueDictionary(), routeHandler);
	}

	public Route(final String url, final RouteValueDictionary defaults, final IRouteHandler routeHandler) {
		this(url, defaults, new RouteValueDictionary(), routeHandler);
	}

	public Route(final String url, final RouteValueDictionary defaults, final RouteValueDictionary constraints, final IRouteHandler routeHandler) {
		this(url, defaults, constraints, new RouteValueDictionary(), routeHandler);
	}

	public Route(final String url, final RouteValueDictionary defaults, final RouteValueDictionary constraints, final RouteValueDictionary dataTokens, final IRouteHandler routeHandler) {
		this.url = url;
		this.defaults = defaults;
		this.constraints = constraints;
		this.dataTokens = dataTokens;
		this.routeHandler = routeHandler;
	}

	@Override
	public RouteData getRouteData(final HttpContext httpContext) {
		return null;
	}

	@Override
	public VirtualPathData getVirtualPath(final RequestContext requestContext, final RouteValueDictionary values) {
		return null;
	}

	/**
	 * @param httpContext
	 * @param constraint
	 * @param parameterName
	 * @param values
	 * @param routeDirection
	 * @return TRUE if the parameter value matches the constraint
	 */
	protected boolean ProcessConstraint(final HttpContext httpContext, final Object constraint, final String parameterName, final RouteValueDictionary values, final RouteDirection routeDirection) {
		return false;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public RouteValueDictionary getConstraints() {
		return constraints;
	}

	public void setConstraints(final RouteValueDictionary constraints) {
		this.constraints = constraints;
	}

	public RouteValueDictionary getDataTokens() {
		return dataTokens;
	}

	public void setDataTokens(final RouteValueDictionary dataTokens) {
		this.dataTokens = dataTokens;
	}

	public RouteValueDictionary getDefaults() {
		return defaults;
	}

	public void setDefaults(final RouteValueDictionary defaults) {
		this.defaults = defaults;
	}

	public IRouteHandler getRouteHandler() {
		return routeHandler;
	}

	public void setRouteHandler(final IRouteHandler routeHandler) {
		this.routeHandler = routeHandler;
	}
}
