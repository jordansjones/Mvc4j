import nextmethod.web.IHttpApplication;
import nextmethod.web.annotations.HttpApplicationStart;
import nextmethod.web.routing.RouteCollection;
import nextmethod.web.routing.RouteTable;
import nextmethod.web.routing.RouteValueDictionary;

import static nextmethod.web.mvc.RouteHelper.ignoreRoute;
import static nextmethod.web.mvc.RouteHelper.mapRoute;


public class MvcApplication implements IHttpApplication {

	public static void registerRoutes(final RouteCollection routes) {
		ignoreRoute(routes, "{resource}.axd/{*pathInfo}");

		// Default
		mapRoute(routes,
			"Default",
			"{controller}/{action}/{id}",
			RouteValueDictionary.builder()
				.put("controller", "Home").put("action", "Index").put("id", "")
				.build()
		);
	}

	@HttpApplicationStart
	protected void AppStart() {
		registerRoutes(RouteTable.getRoutes());
	}
}
