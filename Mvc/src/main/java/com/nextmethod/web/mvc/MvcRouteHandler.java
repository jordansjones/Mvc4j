package com.nextmethod.web.mvc;

import com.nextmethod.web.IHttpHandler;
import com.nextmethod.web.routing.IRouteHandler;
import com.nextmethod.web.routing.RequestContext;

public class MvcRouteHandler implements IRouteHandler {

	@Override
	public IHttpHandler getHttpHandler(RequestContext requestContext) {
		return new MvcHandler(requestContext);
	}

}
