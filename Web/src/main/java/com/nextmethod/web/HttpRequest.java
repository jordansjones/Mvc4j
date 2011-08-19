package com.nextmethod.web;

import com.google.common.base.Strings;
import com.google.inject.servlet.RequestScoped;
import com.nextmethod.NotImplementedException;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

@RequestScoped
public final class HttpRequest implements IHttpRequest {

	public static final String ROUTE_DATA_KEY = "mvc4j.route.data";

	private final HttpServletRequest request;
	private final ServletContext servletContext;

	@Inject
	public HttpRequest(final HttpServletRequest request, final ServletContext servletContext) {
		this.request = request;
		this.servletContext = servletContext;
	}

	@Override
	public HttpServletRequest getHttpServletRequest() {
		return request;
	}

	@Override
	public String getAppRelativeCurrentExecutionFilePath() {
		final StringBuilder builder = new StringBuilder("~");

		String requestURI = request.getRequestURI();
		final String contextPath = servletContext.getContextPath();
		if (!Strings.isNullOrEmpty(contextPath) && requestURI.startsWith(contextPath)) {
			requestURI = requestURI.substring(0, contextPath.length());
		}
		if (requestURI.charAt(0) != '/') {
			builder.append('/');
		}
		builder.append(requestURI);

		return builder.toString();
	}

	@Override
	public String getApplicationPath() {
		final String path = servletContext.getContextPath();
		return Strings.isNullOrEmpty(path) ? "/" : path;
	}

	@Override
	public String getPath() {
		String path = request.getPathInfo();
		if (!Strings.isNullOrEmpty(path))
			return path;

		path = request.getPathTranslated();
		if (!Strings.isNullOrEmpty(path))
			return path;

		path = request.getServletPath();
		if (!Strings.isNullOrEmpty(path))
			return path;

		throw new NotImplementedException();
	}

	@Override
	public String getPathInfo() {
		return request.getPathInfo();
	}

	@Override
	public String getContentEncoding() {
		return request.getCharacterEncoding();
	}
}
