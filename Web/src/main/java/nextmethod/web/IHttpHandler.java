package nextmethod.web;

/**
 * 
 */
public interface IHttpHandler {

	void processRequest(IHttpContext httpContext) throws HttpException;

//	boolean isReusable();

}
