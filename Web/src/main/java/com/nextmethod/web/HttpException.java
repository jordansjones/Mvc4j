package com.nextmethod.web;

import javax.servlet.ServletException;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 10:54 PM
 */
public class HttpException extends ServletException {

	public HttpException(final String message) {
		super(message);
	}
}
