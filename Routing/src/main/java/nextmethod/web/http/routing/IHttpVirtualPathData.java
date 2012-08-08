package nextmethod.web.http.routing;

/**
 *
 */
public interface IHttpVirtualPathData {

	IHttpRoute getRoute();

	String getVirtualPath();

	void setVirtualPath(String value);

}
