package areas.forums;

import nextmethod.web.mvc.AreaRegistration;
import nextmethod.web.mvc.AreaRegistrationContext;
import nextmethod.web.mvc.UrlParameter;
import nextmethod.web.routing.RouteValueDictionary;

/**
 *
 */
public class ForumsAreaRegistration extends AreaRegistration {

	@Override
	public String getAreaName() {
		return "Forums";
	}

	@Override
	public void registerArea(final AreaRegistrationContext context) {
		context.mapRoute(
			"Forums_default",
			"Forums/{controller}/{action}/{id}",
			RouteValueDictionary.builder()
				.put("controller", "Home").put("action", "Index").put("id", UrlParameter.Optional)
				.build()
		);
	}
}
