package nextmethod.web.http.routing;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import nextmethod.OutParam;
import nextmethod.annotations.TODO;
import nextmethod.collect.IDictionary;
import nextmethod.net.http.HttpMethod;
import nextmethod.web.HttpVerb;
import nextmethod.web.IHttpContext;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 */
@TODO
public class HttpMethodConstraint implements IHttpRouteConstraint {

	private final ImmutableCollection<HttpVerb> allowedMethods;

	public HttpMethodConstraint(final HttpVerb... allowedMethods) {
		this.allowedMethods = ImmutableSet.copyOf(checkNotNull(allowedMethods));
	}

	@Override
	public boolean match(final IHttpContext context, final IHttpRoute route, final String parameterName, final IDictionary<String, Object> values, final HttpRouteDirection httpRouteDirection) {
		checkNotNull(context);
		checkNotNull(route);
		checkNotNull(parameterName);
		checkNotNull(values);

		switch (httpRouteDirection) {
			case UriResolution:
				return allowedMethods.contains(null); // TODO
			case UriGeneration:
				// We need to see if the user specified the HTTP method explicitly.  Consider these two routes:
				//
				// a) Route: template = "/{foo}", Constraints = { httpMethod = new HttpMethodConstraint("GET") }
				// b) Route: template = "/{foo}", Constraints = { httpMethod = new HttpMethodConstraint("POST") }
				//
				// A user might know ahead of time that a URI he/she is generating might be used with a particular HTTP
				// method.  If a URI will be used for an HTTP POST but we match on (a) while generating the URI, then
				// the HTTP GET-specific route will be used for URI generation, which might have undesired behavior.
				// To prevent this, a user might call RouteCollection.GetVirtualPath(..., { httpMethod = "POST" }) to
				// signal that he is generating a URI that will be used for an HTTP POST, so he wants the URI
				// generation to be performed by the (b) route instead of the (a) route, consistent with what would
				// happen on incoming requests.
				OutParam<HttpMethod> constraint = OutParam.of();
				if (!values.tryGetValue(parameterName, constraint)) {
					return true;
				}

				return allowedMethods.contains(constraint.get()); // TODO

			default:
				throw new RuntimeException(); // TODO
		}
	}

	public ImmutableCollection<HttpVerb> getAllowedMethods() {
		return allowedMethods;
	}
}
