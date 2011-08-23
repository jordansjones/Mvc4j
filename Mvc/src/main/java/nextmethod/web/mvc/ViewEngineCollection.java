package nextmethod.web.mvc;

import javax.inject.Inject;

/**
 *
 */
public class ViewEngineCollection {

	@Inject
	private IViewEngine viewEngine;

	public ViewEngineResult findView(final ControllerContext controllerContext, final String viewName, final String masterName) {
		return viewEngine.findView(controllerContext, viewName, masterName, false);
	}

}
