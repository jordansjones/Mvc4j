package com.nextmethod.web.mvc.annotation;

import com.nextmethod.web.mvc.ControllerContext;

import java.lang.reflect.Method;

/**
 *
 */
public interface IActionMethodSelector {

	boolean isValidForRequest(ControllerContext controllerContext, Method method);
}
