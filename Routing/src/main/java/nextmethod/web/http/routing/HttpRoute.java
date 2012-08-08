package nextmethod.web.http.routing;

import com.google.common.base.Strings;
import nextmethod.annotations.TODO;
import nextmethod.collect.IDictionary;
import nextmethod.net.http.HttpMessageHandler;
import nextmethod.net.http.HttpRequestMessage;
import nextmethod.web.controllers.HttpControllerContext;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
@TODO
public class HttpRoute implements IHttpRoute {

	/**
	 * Key used to signify that a route URL generation request should include HTTP routes (e.g. Web API).
	 * If this key is not specified then no HTTP routes will match.
	 */
	static final String HttpRouteKey = "httproute";

	private static final String HttpMethodParameterName = "httpMethod";

	private final String routeTemplate;
	private final HttpRouteValueDictionary defaults;
	private final HttpRouteValueDictionary constraints;
	private final HttpRouteValueDictionary dataTokens;
	private final HttpMessageHandler handler;
	private HttpParsedRoute parsedRoute;

	public HttpRoute() {
		this(null, null, null, null, null);
	}

	public HttpRoute(@Nullable final String routeTemplate) {
		this(routeTemplate, null, null, null, null);
	}

	public HttpRoute(@Nullable final String routeTemplate, @Nullable final HttpRouteValueDictionary defaults) {
		this(routeTemplate, defaults, null, null, null);
	}

	public HttpRoute(@Nullable final String routeTemplate, @Nullable final HttpRouteValueDictionary defaults, @Nullable final HttpRouteValueDictionary constraints) {
		this(routeTemplate, defaults, constraints, null, null);
	}

	public HttpRoute(@Nullable final String routeTemplate, @Nullable final HttpRouteValueDictionary defaults, @Nullable final HttpRouteValueDictionary constraints, @Nullable final HttpRouteValueDictionary dataTokens) {
		this(routeTemplate, defaults, constraints, dataTokens, null);
	}

	public HttpRoute(@Nullable final String routeTemplate, @Nullable final HttpRouteValueDictionary defaults, @Nullable final HttpRouteValueDictionary constraints, @Nullable final HttpRouteValueDictionary dataTokens, @Nullable final HttpMessageHandler handler) {
		this.routeTemplate = Strings.isNullOrEmpty(routeTemplate) ? "" : routeTemplate;
		this.defaults = defaults == null ? new HttpRouteValueDictionary() : defaults;
		this.constraints = constraints == null ? new HttpRouteValueDictionary() : constraints;
		this.dataTokens = dataTokens == null ? new HttpRouteValueDictionary() : dataTokens;
		this.handler = handler;

		// The parser will throw for invalid routes.
		this.parsedRoute = HttpRouteParser.parse(getRouteTemplate());
	}


	@Override
	public String getRouteTemplate() {
		return null;
	}

	@Override
	public IDictionary<String, Object> getDefaults() {
		return this.defaults;
	}

	@Override
	public IDictionary<String, Object> getConstraints() {
		return this.constraints;
	}

	@Override
	public IDictionary<String, Object> getDataTokens() {
		return this.dataTokens;
	}

	@Override
	public HttpMessageHandler getHandler() {
		return this.handler;
	}

	@Override
	public IHttpRouteData getRouteData(final String virtualPathRoot, final HttpRequestMessage request) {
		checkNotNull(virtualPathRoot);
		checkNotNull(request);

		// Note: we don't validate host/port as this is expected to be done at the host level
		return null;
	}

	@Override
	public IHttpVirtualPathData getVirtualPath(HttpControllerContext controllerContext, IDictionary<String, Object> values) {
		return null;
	}
}
