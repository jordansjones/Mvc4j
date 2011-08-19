package com.nextmethod.web.mvc;

/**
 *
 */
public interface IActionInvoker {

	boolean invokeAction(ControllerContext controllerContext, String actionName);
}
