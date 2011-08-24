package nextmethod.web.routing;

import com.google.inject.Inject;

/**
 * 
 */
public class RouteTable {

	@Inject
	private static RouteCollection routes;

	public static RouteCollection getRoutes() {
		return routes;
	}
}
