package com.nextmethod.web;

import com.google.common.base.Strings;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * User: Jordan
 * Date: 8/7/11
 * Time: 10:05 PM
 */
public class HttpRequest implements HttpServletRequest {

	private final HttpServletRequest request;
	private final ServletContext servletContext;

	public HttpRequest(final HttpServletRequest request, final ServletContext servletContext) {
		this.request = request;
		this.servletContext = servletContext;
	}

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

	public String getApplicationPath() {
		final String path = servletContext.getContextPath();
		return Strings.isNullOrEmpty(path) ? "/" : path;
	}

	@Override
	public String getAuthType() {
		return request.getAuthType();
	}

	@Override
	public Cookie[] getCookies() {
		return request.getCookies();
	}

	@Override
	public long getDateHeader(final String name) {
		return request.getDateHeader(name);
	}

	@Override
	public String getHeader(final String name) {
		return request.getHeader(name);
	}

	@Override
	public Enumeration getHeaders(final String name) {
		return request.getHeaders(name);
	}

	@Override
	public Enumeration getHeaderNames() {
		return request.getHeaderNames();
	}

	@Override
	public int getIntHeader(final String name) {
		return request.getIntHeader(name);
	}

	@Override
	public String getMethod() {
		return request.getMethod();
	}

	@Override
	public String getPathInfo() {
		return request.getPathInfo();
	}

	@Override
	public String getPathTranslated() {
		return request.getPathTranslated();
	}

	@Override
	public String getContextPath() {
		return request.getContextPath();
	}

	@Override
	public String getQueryString() {
		return request.getQueryString();
	}

	@Override
	public String getRemoteUser() {
		return request.getRemoteUser();
	}

	@Override
	public boolean isUserInRole(final String role) {
		return request.isUserInRole(role);
	}

	@Override
	public Principal getUserPrincipal() {
		return request.getUserPrincipal();
	}

	@Override
	public String getRequestedSessionId() {
		return request.getRequestedSessionId();
	}

	@Override
	public String getRequestURI() {
		return request.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		return request.getRequestURL();
	}

	@Override
	public String getServletPath() {
		return request.getServletPath();
	}

	@Override
	public HttpSession getSession(final boolean create) {
		return request.getSession(create);
	}

	@Override
	public HttpSession getSession() {
		return request.getSession();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return request.isRequestedSessionIdValid();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return request.isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return request.isRequestedSessionIdFromURL();
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return request.isRequestedSessionIdFromUrl();
	}

	@Override
	public Object getAttribute(final String name) {
		return request.getAttribute(name);
	}

	@Override
	public Enumeration getAttributeNames() {
		return request.getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		return request.getCharacterEncoding();
	}

	@Override
	public void setCharacterEncoding(final String env) throws UnsupportedEncodingException {
		request.setCharacterEncoding(env);
	}

	@Override
	public int getContentLength() {
		return request.getContentLength();
	}

	@Override
	public String getContentType() {
		return request.getContentType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return request.getInputStream();
	}

	@Override
	public String getParameter(final String name) {
		return request.getParameter(name);
	}

	@Override
	public Enumeration getParameterNames() {
		return request.getParameterNames();
	}

	@Override
	public String[] getParameterValues(final String name) {
		return request.getParameterValues(name);
	}

	@Override
	public Map getParameterMap() {
		return request.getParameterMap();
	}

	@Override
	public String getProtocol() {
		return request.getProtocol();
	}

	@Override
	public String getScheme() {
		return request.getScheme();
	}

	@Override
	public String getServerName() {
		return request.getServerName();
	}

	@Override
	public int getServerPort() {
		return request.getServerPort();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return request.getReader();
	}

	@Override
	public String getRemoteAddr() {
		return request.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return request.getRemoteHost();
	}

	@Override
	public void setAttribute(final String name, final Object o) {
		request.setAttribute(name, o);
	}

	@Override
	public void removeAttribute(final String name) {
		request.removeAttribute(name);
	}

	@Override
	public Locale getLocale() {
		return request.getLocale();
	}

	@Override
	public Enumeration getLocales() {
		return request.getLocales();
	}

	@Override
	public boolean isSecure() {
		return request.isSecure();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(final String path) {
		return request.getRequestDispatcher(path);
	}

	@Override
	public String getRealPath(final String path) {
		return request.getRealPath(path);
	}

	@Override
	public int getRemotePort() {
		return request.getRemotePort();
	}

	@Override
	public String getLocalName() {
		return request.getLocalName();
	}

	@Override
	public String getLocalAddr() {
		return request.getLocalAddr();
	}

	@Override
	public int getLocalPort() {
		return request.getLocalPort();
	}
}
