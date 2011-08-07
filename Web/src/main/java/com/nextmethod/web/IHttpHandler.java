package com.nextmethod.web;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 10:33 PM
 */
public interface IHttpHandler {

	void processRequest(HttpContext httpContext) throws HttpException;

	boolean isReusable();

}
