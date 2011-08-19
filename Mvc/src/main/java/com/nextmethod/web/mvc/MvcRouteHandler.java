package com.nextmethod.web.mvc;

import com.google.inject.Injector;
import com.nextmethod.web.IHttpHandler;
import com.nextmethod.web.routing.IRouteHandler;
import com.nextmethod.web.routing.RequestContext;

import javax.inject.Inject;

public class MvcRouteHandler implements IRouteHandler {

	@Inject
	private Injector injector;

	@Override
	public IHttpHandler getHttpHandler(RequestContext requestContext) {
		final MvcHandler mvcHandler = new MvcHandler(requestContext);
		injector.injectMembers(mvcHandler);
		return mvcHandler;
	}

}
