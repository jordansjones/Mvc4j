package com.nextmethod.routing;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.nextmethod.web.HttpContext;
import com.nextmethod.web.HttpVerb;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:36 PM
 */
public class HttpMethodConstraint implements IRouteConstraint {

	private final ImmutableCollection<HttpVerb> allowedMethods;

	public HttpMethodConstraint(final HttpVerb... allowedMethods) {
		this.allowedMethods = ImmutableSet.copyOf(allowedMethods);
	}

	@Override
	public boolean match(final HttpContext context, final Route route, final String parameterName, final RouteValueDictionary values, final RouteDirection routeDirection) {
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

	/**
	 * @param context
	 * @param route
	 * @param parameterName
	 * @param values
	 * @return TRUE if the incoming request was made by using an allowed HTTP verb
	 */
	private boolean matchIncomingRequest(final Object context, final Route route, final String parameterName, final RouteValueDictionary values) {
		return true;
	}

	/**
	 * @param context
	 * @param route
	 * @param parameterName
	 * @param values
	 * @return TRUE if the supplied values contain an HTTP verb that matches one of the allowed HTTP verbs.
	 */
	private boolean matchUrlGeneration(final Object context, final Route route, final String parameterName, final RouteValueDictionary values) {
		return true;
	}

	public ImmutableCollection<HttpVerb> getAllowedMethods() {
		return allowedMethods;
	}
}
