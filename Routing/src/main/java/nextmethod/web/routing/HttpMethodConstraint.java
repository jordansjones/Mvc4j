package nextmethod.web.routing;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import nextmethod.web.HttpVerb;
import nextmethod.web.IHttpContext;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 */
public class HttpMethodConstraint implements IRouteConstraint {

	private final ImmutableCollection<HttpVerb> allowedMethods;

	public HttpMethodConstraint(final HttpVerb... allowedMethods) {
		this.allowedMethods = ImmutableSet.copyOf(allowedMethods);
	}

	@Override
	public boolean match(final IHttpContext context, final Route route, final String parameterName, final RouteValueDictionary values, final RouteDirection routeDirection) {
		checkNotNull(context);
		checkNotNull(route);
		checkNotNull(parameterName);
		checkNotNull(values);

		return routeDirection == null || (
			routeDirection == RouteDirection.IncomingRequest
				? matchIncomingRequest(context, route, parameterName, values)
				: matchUrlGeneration(context, route, parameterName, values)
		);
	}

	private boolean matchIncomingRequest(final Object context, final Route route, final String parameterName, final RouteValueDictionary values) {
		return true;
	}

	private boolean matchUrlGeneration(final Object context, final Route route, final String parameterName, final RouteValueDictionary values) {
		return true;
	}

	public ImmutableCollection<HttpVerb> getAllowedMethods() {
		return allowedMethods;
	}
}
