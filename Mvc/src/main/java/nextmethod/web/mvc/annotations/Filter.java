package nextmethod.web.mvc.annotations;

import nextmethod.web.mvc.IMvcFilter;
import nextmethod.web.mvc.VoidMvcFilter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: Jordan
 * Date: 8/6/11
 * Time: 12:27 AM
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Filter {

	Class<? extends IMvcFilter> impl() default VoidMvcFilter.class;

	boolean allowMultiple() default false;

	int order() default 0;
}


