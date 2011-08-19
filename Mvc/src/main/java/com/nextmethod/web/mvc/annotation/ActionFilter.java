package com.nextmethod.web.mvc.annotation;

import java.lang.annotation.*;

/**
 * User: Jordan
 * Date: 8/6/11
 * Time: 12:21 AM
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ActionFilter {

}
