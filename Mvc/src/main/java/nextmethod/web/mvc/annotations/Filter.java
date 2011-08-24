package nextmethod.web.mvc.annotations;

import nextmethod.web.mvc.IMvcFilter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Filter {

	Class<? extends IMvcFilter> impl() default DefaultMvcFilter.class;

	boolean allowMultiple() default false;

	int order() default 0;

	static class DefaultMvcFilter implements IMvcFilter {}
}


