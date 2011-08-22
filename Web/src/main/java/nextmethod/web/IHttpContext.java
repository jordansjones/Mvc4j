package nextmethod.web;

public interface IHttpContext {

	IHttpRequest getRequest();

	IHttpResponse getResponse();

	String applyApplicationPathModifier(final String virtualPath);

	String getApplicationPath();
}
