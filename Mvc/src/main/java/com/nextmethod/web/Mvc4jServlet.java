package com.nextmethod.web;

import com.google.inject.Injector;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
class Mvc4jServlet extends HttpServlet {

	@Inject
	private Injector injector;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final IHttpContext instance = injector.getInstance(IHttpContext.class);
		final Object attribute = req.getAttribute(HttpRequest.ROUTE_DATA_KEY);
		int x = 1;
	}
}
