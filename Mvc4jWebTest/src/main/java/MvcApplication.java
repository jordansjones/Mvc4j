import com.nextmethod.web.IHttpApplication;
import com.nextmethod.web.annotations.HttpApplicationStart;
import com.nextmethod.web.routing.RouteCollection;
import com.nextmethod.web.routing.RouteTable;
import com.nextmethod.web.routing.RouteValueDictionary;

import static com.nextmethod.web.mvc.RouteHelper.ignoreRoute;
import static com.nextmethod.web.mvc.RouteHelper.mapRoute;


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
