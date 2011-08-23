package nextmethod.web;

import javax.servlet.http.HttpServletResponse;

public interface IHttpResponse {

	HttpServletResponse getServletResponse();

	void appendHeader(String name, String value);

	void setContentEncoding(String encoding);

	void setContentType(String contentType);

	void write(String content);
}
