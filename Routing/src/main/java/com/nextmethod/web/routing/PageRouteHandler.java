package com.nextmethod.web.routing;

import com.nextmethod.web.IHttpHandler;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:37 PM
 */
public class PageRouteHandler implements IRouteHandler {

	private final String virtualPath;
	private final boolean checkPhysicalUrlAccess;

	public PageRouteHandler(final String virtualPath) {
		this(virtualPath, true);
	}

	public PageRouteHandler(final String virtualPath, final boolean checkPhysicalUrlAccess) {
		this.virtualPath = virtualPath;
		this.checkPhysicalUrlAccess = checkPhysicalUrlAccess;
	}

	@Override
	public IHttpHandler getHttpHandler(final RequestContext requestContext) {
		return null;
	}

	public String GetSubstitutedVirtualPath(final RequestContext requestContext) {
		return null;
	}

	public String getVirtualPath() {
		return virtualPath;
	}

	public boolean isCheckPhysicalUrlAccess() {
		return checkPhysicalUrlAccess;
	}
}
