package nextmethod.web.mvc.annotations;

import nextmethod.reflection.MethodInfo;
import nextmethod.web.mvc.ControllerContext;

/**
 *
 */
public interface IActionNameSelector {

	boolean isValidName(ControllerContext controllerContext, String actionName, MethodInfo method);

}
