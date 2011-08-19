package com.nextmethod.web.mvc;

import com.nextmethod.web.routing.RequestContext;

/**
 *
 */
public interface IControllerFactory {

	IController createController(RequestContext requestContext, String controllerName);

	void releaseController(IController controller);

}
