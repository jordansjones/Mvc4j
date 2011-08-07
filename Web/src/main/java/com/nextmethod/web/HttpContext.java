package com.nextmethod.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 9:57 PM
 */
public final class HttpContext {

	private final ServletContext servletContext;
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public HttpContext(final ServletContext servletContext, final HttpServletRequest request, final HttpServletResponse response) {
		this.servletContext = servletContext;
		this.request = request;
		this.response = response;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public String applyApplicationPathModifier(final String virtualPath) {
		// TODO: Implement this
		// NOTE: Take SessionID into account.
		return virtualPath;
	}

	public String getApplicationPath() {
		return servletContext.getContextPath();
	}

}
