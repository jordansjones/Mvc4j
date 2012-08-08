package nextmethod.web.http.routing;

import com.google.common.base.Strings;
import nextmethod.OutParam;
import nextmethod.TypeHelpers;
import nextmethod.web.InvalidOperationException;

/**
 * 
 */
public class RouteData {

	private final HttpRouteValueDictionary dataTokens;
	private final HttpRouteValueDictionary values;

	private RouteBase route;
	private IRouteHandler routeHandler;

	public RouteData(final RouteBase route, final IRouteHandler routeHandler) {
		this.route = route;
		this.routeHandler = routeHandler;

		this.dataTokens = new HttpRouteValueDictionary();
		this.values = new HttpRouteValueDictionary();
	}

	public String getRequiredString(final String valueName) {
		final OutParam<Object> param = OutParam.of(Object.class);
		if (!values.tryGetValue(valueName, param))
			throw new InvalidOperationException(String.format("value name '%s' does not match any of the values.", valueName));

		final String s = TypeHelpers.typeAs(param, String.class);
		if (Strings.isNullOrEmpty(s))
			throw new InvalidOperationException(String.format("The value for the name '%s' must be a non-empty string", valueName));

		return s;
	}

	public HttpRouteValueDictionary getDataTokens() {
		return dataTokens;
	}

	public HttpRouteValueDictionary getValues() {
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
