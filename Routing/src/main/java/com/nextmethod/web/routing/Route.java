package com.nextmethod.web.routing;

import com.google.common.base.Strings;
import com.nextmethod.OutParam;
import com.nextmethod.web.IHttpContext;
import com.nextmethod.web.InvalidOperationException;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.nextmethod.TypeHelpers.typeAs;

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
		this(url, null, routeHandler);
	}

	public Route(final String url, @Nullable final RouteValueDictionary defaults, final IRouteHandler routeHandler) {
		this(url, defaults, null, routeHandler);
	}

	public Route(final String url, @Nullable final RouteValueDictionary defaults, @Nullable final RouteValueDictionary constraints, final IRouteHandler routeHandler) {
		this(url, defaults, constraints, null, routeHandler);
	}

	public Route(final String url, @Nullable final RouteValueDictionary defaults, @Nullable final RouteValueDictionary constraints, @Nullable final RouteValueDictionary dataTokens, final IRouteHandler routeHandler) {
		this.setUrl(url);
		this.defaults = defaults;
		this.constraints = constraints;
		this.dataTokens = dataTokens;
		this.routeHandler = routeHandler;
	}

	@Override
	public RouteData getRouteData(final IHttpContext httpContext) {
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
	protected boolean processConstraint(final IHttpContext httpContext, final Object constraint, final String parameterName, final RouteValueDictionary values, final RouteDirection routeDirection) {
		checkNotNull(parameterName);
		checkNotNull(values);

		final OutParam<Boolean> invalidConstraint = OutParam.of(false);
		final RequestContext requestContext = new RequestContext();
		requestContext.setHttpContext(httpContext);
		final boolean ret = processConstraintInternal(httpContext, this, constraint, parameterName, values, routeDirection, requestContext, invalidConstraint);

		if (invalidConstraint.get()) {
			throw new InvalidOperationException(String.format(
				"Constraint parameter '%s' on the route with Url '%s' must have a string value type or be a type which implements IRouteConstraint",
				parameterName,
				url
			));
		}

		return ret;
	}

	static boolean processConstraintInternal(final IHttpContext httpContext, final Route route, final Object constraint, final String parameterName, final RouteValueDictionary values, final RouteDirection routeDirection, final RequestContext requestContext, final OutParam<Boolean> invalidConstraint) {
		invalidConstraint.set(false);

		final IRouteConstraint irc = typeAs(constraint, IRouteConstraint.class);
		if (irc != null)
			return irc.match(httpContext, route, parameterName, values, routeDirection);

		try {
			final String s = typeAs(constraint, String.class);
			if (s != null) {
				OutParam<Object> out = OutParam.of(Object.class);
				String v;

				if (values != null && values.tryGetValue(parameterName, out)) {
					v = typeAs(out.get(), String.class);
				} else {
					v = null;
				}

				if (!Strings.isNullOrEmpty(v)) {
					return matchConstraintRegex(v, s);
				} else if (requestContext != null) {
					final RouteData routeData = requestContext.getRouteData();
					final RouteValueDictionary rdValues = routeData != null ? routeData.getValues() : null;

					if (rdValues == null || rdValues.isEmpty())
						return false;

					if (!rdValues.tryGetValue(parameterName, out))
						return false;

					v = typeAs(out.get(), String.class);

					return !Strings.isNullOrEmpty(v) && matchConstraintRegex(v, s);
				}
				return false;
			}
		}
		catch (PatternSyntaxException ignored) {
		}
		invalidConstraint.set(true);
		return false;
	}

	static boolean matchConstraintRegex(final String value, String constraint) {

//		int len = constraint.length();
//		if (len > 0) {
//			// Regexp constraints must be treated as absolute expressions
//			if (constraint.charAt(0) != '^') {
//				constraint = "^" + constraint;
//				len++;
//			}
//
//			if (constraint.charAt(len - 1) != '$')
//				constraint += "$";
//		}

		final Pattern compiledPattern = Pattern.compile(constraint);
		return compiledPattern.matcher(value).matches();
	}

	public String getUrl() {
		return url != null ? url.getUrl() : "";
	}

	public void setUrl(@Nullable final String url) {
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
