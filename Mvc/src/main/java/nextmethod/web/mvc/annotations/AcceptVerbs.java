package nextmethod.web.mvc.annotations;

import nextmethod.web.HttpVerb;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AcceptVerbs {

	HttpVerb[] value();
	// TODO: IsValidForRequest (ControllerContext controllerContext, MethodInfo methodInfo)
}
