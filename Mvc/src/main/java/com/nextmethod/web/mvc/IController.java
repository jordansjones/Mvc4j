package com.nextmethod.web.mvc;

import com.nextmethod.web.routing.RequestContext;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 11:58 PM
 */
public interface IController {

	void execute(RequestContext requestContext);

}
