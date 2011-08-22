package nextmethod.web;

import javax.servlet.http.HttpServletRequest;

public interface IHttpRequest {

	HttpServletRequest getHttpServletRequest();

	String getAppRelativeCurrentExecutionFilePath();

	String getApplicationPath();

	String getPath();

	String getPathInfo();

	String getContentEncoding();
}
