package com.nextmethod.web.mvc.annotation;

import com.nextmethod.web.mvc.ControllerContext;

import java.lang.reflect.Method;

/**
 *
 */
public interface IActionNameSelector {

	boolean isValidName(ControllerContext controllerContext, String actionName, Method method);

}
