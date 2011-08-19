package com.nextmethod.web;

import javax.servlet.http.HttpServletResponse;

public interface IHttpResponse {

	HttpServletResponse getServletResponse();

	void appendHeader(String name, String value);
}
