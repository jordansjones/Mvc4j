package nextmethod.web.mvc;

/**
 *
 */
public interface IViewEngine {

	ViewEngineResult findPartialView(ControllerContext controllerContext, String partialViewName, boolean useCache);

	ViewEngineResult findView(ControllerContext controllerContext, String viewName, String masterName, boolean useCache);

	void releaseView(ControllerContext controllerContext, IView view);

}
