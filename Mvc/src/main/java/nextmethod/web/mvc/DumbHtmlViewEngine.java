package nextmethod.web.mvc;

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import nextmethod.OutParam;
import nextmethod.web.routing.RouteData;

import java.net.URL;

/**
 *
 */
class DumbHtmlViewEngine implements IViewEngine {

	@Override
	public ViewEngineResult findPartialView(final ControllerContext controllerContext, final String partialViewName, final boolean useCache) {
		return null;
	}

	@Override
	public ViewEngineResult findView(final ControllerContext controllerContext, final String viewName, final String masterName, final boolean useCache) {
		final RouteData routeData = controllerContext.getRouteData();
		final String string = routeData.getRequiredString(MagicStrings.ControllerKey);
		String area = "";
		final OutParam<Object> out = OutParam.of();
		if (routeData.getDataTokens().tryGetValue(MagicStrings.AreaKey, out)) {
			area = out.get().toString();
			if (!Strings.isNullOrEmpty(area))
				area = String.format("/areas/%s", area.toLowerCase());
		}

		final String controller = Strings.nullToEmpty(string).toLowerCase();

		final URL resource = Resources.getResource(String.format("%s/views/%s/%s.html", area, controller, viewName));
		final DumbHtmlView view = new DumbHtmlView(resource);
		return new ViewEngineResult(view, this);
	}

	@Override
	public void releaseView(final ControllerContext controllerContext, final IView view) {
	}
}
