package com.nextmethod.web;

import com.google.inject.servlet.RequestScoped;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

@RequestScoped
class HttpResponse implements IHttpResponse {

	private final HttpServletResponse response;
	private final ServletContext servletContext;

	@Inject
	public HttpResponse(final HttpServletResponse response, final ServletContext servletContext) {
		this.response = response;
		this.servletContext = servletContext;
	}

	@Override
	public HttpServletResponse getServletResponse() {
		return null;
	}
}
