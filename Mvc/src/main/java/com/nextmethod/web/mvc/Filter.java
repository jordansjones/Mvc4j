package com.nextmethod.web.mvc;

import java.lang.annotation.*;

/**
 * User: Jordan
 * Date: 8/6/11
 * Time: 12:27 AM
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Filter {

	boolean allowMultiple() default false;

	int order() default 0;
}


