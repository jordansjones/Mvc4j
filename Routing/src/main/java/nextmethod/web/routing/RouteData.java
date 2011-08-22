package nextmethod.web.routing;

import com.google.common.base.Strings;
import nextmethod.OutParam;
import nextmethod.TypeHelpers;
import nextmethod.web.InvalidOperationException;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:38 PM
 */
public class RouteData {

	private final RouteValueDictionary dataTokens;
	private final RouteValueDictionary values;

	private RouteBase route;
	private IRouteHandler routeHandler;

	public RouteData(final RouteBase route, final IRouteHandler routeHandler) {
		this.route = route;
		this.routeHandler = routeHandler;

		this.dataTokens = new RouteValueDictionary();
		this.values = new RouteValueDictionary();
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

	public RouteValueDictionary getDataTokens() {
		return dataTokens;
	}

	public RouteValueDictionary getValues() {
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
