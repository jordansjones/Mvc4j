import nextmethod.web.IHttpApplication;
import nextmethod.web.annotations.HttpApplicationStart;
import nextmethod.web.mvc.AreaRegistration;
import nextmethod.web.mvc.UrlParameter;
import nextmethod.web.routing.RouteCollection;
import nextmethod.web.routing.RouteTable;
import nextmethod.web.routing.RouteValueDictionary;

import static nextmethod.web.mvc.RouteHelper.ignoreRoute;
import static nextmethod.web.mvc.RouteHelper.mapRoute;


public class MvcApplication implements IHttpApplication {

	public static void registerRoutes(final RouteCollection routes) {
		ignoreRoute(routes, "{resource}.axd/{*pathInfo}");
		ignoreRoute(routes, "{*favicon}", RouteValueDictionary.builder()
			.put("favicon", "(.*/)?favicon.ico(/.*)?").build()
		);

		// Default
		mapRoute(routes,
			"Default",
			"{controller}/{action}/{id}",
			RouteValueDictionary.builder()
				.put("controller", "Home").put("action", "Index").put("id", UrlParameter.Optional)
				.build()
		);
	}

	@HttpApplicationStart
	protected void AppStart() {
		AreaRegistration.registerAllAreas();

		registerRoutes(RouteTable.getRoutes());
	}
}
