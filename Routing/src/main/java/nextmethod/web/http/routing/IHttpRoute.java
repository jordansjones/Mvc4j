package nextmethod.web.http.routing;

import nextmethod.collect.IDictionary;
import nextmethod.net.http.HttpMessageHandler;
import nextmethod.net.http.HttpRequestMessage;
import nextmethod.web.controllers.HttpControllerContext;

/**
 * {@link IHttpRoute} defines the interface for a route expressing how to map an incoming {@link HttpRequestMessage} to a particular controller and action.
 */
public interface IHttpRoute {

	/**
	 * Gets the route template describing the URI pattern to match against.
	 */
	String getRouteTemplate();

	/**
	 * Gets the default values for route parameters if not provided by the incoming {@link HttpRequestMessage}.
	 */
	IDictionary<String, Object> getDefaults();

	/**
	 * Gets the constraints for the route parameters.
	 */
	IDictionary<String, Object> getConstraints();

	/**
	 * Gets any additional data tokens not used directly to determine whether a route matches an incoming {@link HttpRequestMessage}.
	 */
	IDictionary<String, Object> getDataTokens();

	/**
	 * Gets the message handler that will be the recipient of the request. If <code>null</code>, the default handler will
	 * be used (which dispatches messages to implementations of {@link nextmethod.web.controllers.IHttpController}.
	 */
	HttpMessageHandler getHandler();

	/**
	 * Determine whether this route is a match for the incoming request by looking up the {@link IHttpRouteData} for the route.
	 * @param virtualPathRoot The virtual path root.
	 * @param request The request.
	 * @return The {@link IHttpRouteData} for a route if it matches; otherwise <code>null</code>.
	 */
	IHttpRouteData getRouteData(String virtualPathRoot, HttpRequestMessage request);

	/**
	 * Compute a URI based on the route and the values provided.
	 * @param controllerContext The controller context.
	 * @param values The values.
	 */
	IHttpVirtualPathData getVirtualPath(HttpControllerContext controllerContext, IDictionary<String, Object> values);
}
