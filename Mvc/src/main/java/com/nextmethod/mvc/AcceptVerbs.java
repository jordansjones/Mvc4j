package com.nextmethod.mvc;

import com.nextmethod.web.HttpVerb;

import java.lang.annotation.*;

/**
 * User: Jordan
 * Date: 8/6/11
 * Time: 12:12 AM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AcceptVerbs {

	HttpVerb[] value();
	// TODO: IsValidForRequest (ControllerContext controllerContext, MethodInfo methodInfo)
}
