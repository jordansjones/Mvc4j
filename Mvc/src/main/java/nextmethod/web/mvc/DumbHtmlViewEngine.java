package nextmethod.web.mvc;

import com.google.common.base.Strings;
import com.google.common.io.Resources;

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
		final String string = controllerContext.getRouteData().getRequiredString(MagicStrings.ControllerKey);
		final String controller = Strings.nullToEmpty(string).toLowerCase();
		final URL resource = Resources.getResource(String.format("/views/%s/%s.html", controller, viewName));
		final DumbHtmlView view = new DumbHtmlView(resource);
		return new ViewEngineResult(view, this);
	}

	@Override
	public void releaseView(final ControllerContext controllerContext, final IView view) {
	}
}
