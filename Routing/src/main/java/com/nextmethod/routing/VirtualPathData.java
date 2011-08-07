package com.nextmethod.routing;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:41 PM
 */
public class VirtualPathData {

	private final RouteValueDictionary dataTokens;
	private RouteBase route;
	private String virtualPath;

	public VirtualPathData(final RouteBase route, final String virtualPath) {
		this.dataTokens = new RouteValueDictionary();
		this.route = route;
		this.virtualPath = virtualPath;
	}

	public RouteValueDictionary getDataTokens() {
		return dataTokens;
	}

	public RouteBase getRoute() {
		return route;
	}

	public void setRoute(final RouteBase route) {
		this.route = route;
	}

	public String getVirtualPath() {
		return virtualPath;
	}

	public void setVirtualPath(final String virtualPath) {
		this.virtualPath = virtualPath;
	}
}
