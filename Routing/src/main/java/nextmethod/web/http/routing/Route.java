package nextmethod.web.http.routing;

import com.google.common.base.Strings;
import nextmethod.OutParam;
import nextmethod.annotations.TODO;
import nextmethod.collect.KeyValuePair;
import nextmethod.web.IHttpContext;
import nextmethod.web.InvalidOperationException;

import javax.annotation.Nullable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.TypeHelpers.typeAs;

/**
 * 
 */
@TODO
public class Route extends RouteBase {

	private PatternParser url;
	private HttpRouteValueDictionary constraints;
	private HttpRouteValueDictionary dataTokens;
	private HttpRouteValueDictionary defaults;
	private IRouteHandler routeHandler;

	public Route(final String url, final IRouteHandler routeHandler) {
		this(url, null, routeHandler);
	}

	public Route(final String url, @Nullable final HttpRouteValueDictionary defaults, final IRouteHandler routeHandler) {
		this(url, defaults, null, routeHandler);
	}

	public Route(final String url, @Nullable final HttpRouteValueDictionary defaults, @Nullable final HttpRouteValueDictionary constraints, final IRouteHandler routeHandler) {
		this(url, defaults, constraints, null, routeHandler);
	}

	public Route(final String url, @Nullable final HttpRouteValueDictionary defaults, @Nullable final HttpRouteValueDictionary constraints, @Nullable final HttpRouteValueDictionary dataTokens, final IRouteHandler routeHandler) {
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

		final HttpRouteValueDictionary values = url.match(path, defaults);
		if (values == null)
			return null;

		final HttpRouteValueDictionary constraints = this.constraints;
		if (constraints != null) {
			for (KeyValuePair<String, Object> entry : constraints) {
				if (!processConstraint(httpContext, entry.getValue(), entry.getKey(), values, HttpRouteDirection.UriResolution))
					return null;
			}
		}

		final RouteData rd = new RouteData(this, routeHandler);
		final HttpRouteValueDictionary rdValues = rd.getValues();

		for (KeyValuePair<String, Object> entry : values) {
			rdValues.put(entry.getKey(), entry.getValue());
		}

		final HttpRouteValueDictionary dataTokens = this.dataTokens;
		if (dataTokens != null) {
			final HttpRouteValueDictionary rdDataTokens = rd.getDataTokens();
			for (KeyValuePair<String, Object> entry : dataTokens) {
				rdDataTokens.put(entry.getKey(), entry.getValue());
			}
		}

		return rd;
	}

	@Override
	public VirtualPathData getVirtualPath(final RequestContext requestContext, final HttpRouteValueDictionary values) {
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

	protected boolean processConstraint(final IHttpContext httpContext, final Object constraint, final String parameterName, final HttpRouteValueDictionary values, final HttpRouteDirection httpRouteDirection) {
		checkNotNull(parameterName);
		checkNotNull(values);

		final OutParam<Boolean> invalidConstraint = OutParam.of(false);
		final RequestContext requestContext = new RequestContext();
		requestContext.setHttpContext(httpContext);
		final boolean ret = processConstraintInternal(httpContext, this, constraint, parameterName, values, httpRouteDirection, requestContext, invalidConstraint);

		if (invalidConstraint.get()) {
			throw new InvalidOperationException(String.format(
				"Constraint parameter '%s' on the route with Url '%s' must have a string value type or be a type which implements IHttpRouteConstraint",
				parameterName,
				url
			));
		}

		return ret;
	}

	static boolean processConstraintInternal(final IHttpContext httpContext, final Route route, final Object constraint, final String parameterName, final HttpRouteValueDictionary values, final HttpRouteDirection httpRouteDirection, final RequestContext requestContext, final OutParam<Boolean> invalidConstraint) {
		invalidConstraint.set(false);

		final IHttpRouteConstraint irc = typeAs(constraint, IHttpRouteConstraint.class);
		if (irc != null)
			return false;
//			return irc.match(httpContext, route, parameterName, values, httpRouteDirection);

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
					final HttpRouteValueDictionary rdValues = routeData != null ? routeData.getValues() : null;

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

	public HttpRouteValueDictionary getConstraints() {
		return constraints;
	}

	public void setConstraints(final HttpRouteValueDictionary constraints) {
		this.constraints = constraints;
	}

	public HttpRouteValueDictionary getDataTokens() {
		return dataTokens;
	}

	public void setDataTokens(final HttpRouteValueDictionary dataTokens) {
		this.dataTokens = dataTokens;
	}

	public HttpRouteValueDictionary getDefaults() {
		return defaults;
	}

	public void setDefaults(final HttpRouteValueDictionary defaults) {
		this.defaults = defaults;
	}

	public IRouteHandler getRouteHandler() {
		return routeHandler;
	}

	public void setRouteHandler(final IRouteHandler routeHandler) {
		this.routeHandler = routeHandler;
	}
}
