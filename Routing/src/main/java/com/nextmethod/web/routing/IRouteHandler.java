package com.nextmethod.web.routing;

import com.nextmethod.web.IHttpHandler;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:36 PM
 */
public interface IRouteHandler {

	IHttpHandler getHttpHandler(RequestContext requestContext);

}
