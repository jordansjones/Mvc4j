package com.nextmethod.routing;

import com.google.common.base.Strings;
import com.nextmethod.web.HttpContext;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:33 PM
 */
public class Route extends RouteBase {

	private PatternParser url;
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
		this.setUrl(url);
		this.defaults = defaults;
		this.constraints = constraints;
		this.dataTokens = dataTokens;
		this.routeHandler = routeHandler;
	}

	@Override
	public RouteData getRouteData(final HttpContext httpContext) {
		String path = httpContext.getRequest().getAppRelativeCurrentExecutionFilePath();
		final String pathInfo = httpContext.getRequest().getPathInfo();

		if (!Strings.isNullOrEmpty(pathInfo))
			path += pathInfo;

		if (!url.getUrl().equalsIgnoreCase(path) && !"~/".equals(path.substring(0, 2)))
			return null;
		path = path.substring(2);

		final RouteValueDictionary values = url.match(path, defaults);
		if (values == null)
			return null;

		final RouteValueDictionary constraints = this.constraints;
		if (constraints != null) {
			for (Map.Entry<String, Object> entry : constraints.entrySet()) {
				if (!processConstraint(httpContext, entry.getValue(), entry.getKey(), values, RouteDirection.IncomingRequest))
					return null;
			}
		}

		final RouteData rd = new RouteData(this, routeHandler);
		final RouteValueDictionary rdValues = rd.getValues();

		for (Map.Entry<String, Object> entry : values.entrySet()) {
			rdValues.put(entry.getKey(), entry.getValue());
		}

		final RouteValueDictionary dataTokens = this.dataTokens;
		if (dataTokens != null) {
			final RouteValueDictionary rdDataTokens = rd.getDataTokens();
			for (Map.Entry<String, Object> entry : dataTokens.entrySet()) {
				rdDataTokens.put(entry.getKey(), entry.getValue());
			}
		}

		return rd;
	}

	@Override
	public VirtualPathData getVirtualPath(final RequestContext requestContext, final RouteValueDictionary values) {
		checkNotNull(requestContext);

		if (url == null)
			return new VirtualPathData(this, "");

		// null values is allowed.
//		if (values == null)
//			values = requestContext.getRouteData().getValues();

		final String s = url.buildUrl(this, requestContext, values);
		if (Strings.isNullOrEmpty(s))
			return null;

		return new VirtualPathData(this, s);
	}

	/**
	 * @param httpContext
	 * @param constraint
	 * @param parameterName
	 * @param values
	 * @param routeDirection
	 * @return TRUE if the parameter value matches the constraint
	 */
	protected boolean processConstraint(final HttpContext httpContext, final Object constraint, final String parameterName, final RouteValueDictionary values, final RouteDirection routeDirection) {
		checkNotNull(parameterName);
		checkNotNull(values);

		return false;
	}

	static boolean processConstraintInternal(final HttpContext httpContext, final Route route, final Object constraint, final String parameterName, final RouteValueDictionary values, final RouteDirection routeDirection, final RequestContext requestContext) {
		return false;
	}

	public String getUrl() {
		return url != null ? url.getUrl() : "";
	}

	public void setUrl(final String url) {
		this.url = !Strings.isNullOrEmpty(url) ? new PatternParser(url) : new PatternParser("");
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
