package nextmethod.web.mvc.annotations;

import nextmethod.reflection.MethodInfo;
import nextmethod.web.mvc.ControllerContext;

/**
 *
 */
public interface IActionMethodSelector {

	boolean isValidForRequest(ControllerContext controllerContext, MethodInfo method);
}
