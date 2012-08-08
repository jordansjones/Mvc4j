package nextmethod.web.mvc;

import nextmethod.web.http.routing.RequestContext;

/**
 *
 */
public interface IControllerFactory {

	IController createController(RequestContext requestContext, String controllerName);

	void releaseController(IController controller);

}
