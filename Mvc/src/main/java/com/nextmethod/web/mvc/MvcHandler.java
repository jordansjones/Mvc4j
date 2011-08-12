package com.nextmethod.web.mvc;

import com.nextmethod.web.HttpException;
import com.nextmethod.web.IHttpContext;
import com.nextmethod.web.IHttpHandler;
import com.nextmethod.web.routing.RequestContext;

public class MvcHandler implements IHttpHandler {

	private final RequestContext requestContext;

	public MvcHandler(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	@Override
	public void processRequest(IHttpContext httpContext) throws HttpException {
	}

	@Override
	public boolean isReusable() {
		return false;
	}

}
