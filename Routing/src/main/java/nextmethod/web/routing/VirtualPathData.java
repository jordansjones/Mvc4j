package nextmethod.web.routing;

/**
 * 
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
