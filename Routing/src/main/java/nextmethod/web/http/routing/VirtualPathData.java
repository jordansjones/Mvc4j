package nextmethod.web.http.routing;

/**
 * 
 */
public class VirtualPathData {

	private final HttpRouteValueDictionary dataTokens;
	private RouteBase route;
	private String virtualPath;

	public VirtualPathData(final RouteBase route, final String virtualPath) {
		this.dataTokens = new HttpRouteValueDictionary();
		this.route = route;
		this.virtualPath = virtualPath;
	}

	public HttpRouteValueDictionary getDataTokens() {
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
