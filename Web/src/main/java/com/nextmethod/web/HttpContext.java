package com.nextmethod.web;

import com.google.inject.servlet.RequestScoped;

import javax.inject.Inject;
import javax.servlet.ServletContext;

@RequestScoped
class HttpContext implements IHttpContext {

	private final ServletContext servletContext;
	private final IHttpRequest request;
	private final IHttpResponse response;

	@Inject
	public HttpContext(final ServletContext servletContext, final IHttpRequest request, final IHttpResponse response) {
		this.servletContext = servletContext;
		this.request = request;
		this.response = response;
	}

	ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public IHttpRequest getRequest() {
		return request;
	}

	@Override
	public IHttpResponse getResponse() {
		return response;
	}

	@Override
	public String applyApplicationPathModifier(final String virtualPath) {
		// TODO: Implement this
		// NOTE: Take SessionID into account.
		return virtualPath;
	}

	@Override
	public String getApplicationPath() {
		return request.getApplicationPath();
	}

}
