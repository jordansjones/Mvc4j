package nextmethod.web;

import com.google.inject.servlet.RequestScoped;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

@RequestScoped
public final class HttpResponse implements IHttpResponse {

	private final HttpServletResponse response;
	private final ServletContext servletContext;

	@Inject
	public HttpResponse(final HttpServletResponse response, final ServletContext servletContext) {
		this.response = response;
		this.servletContext = servletContext;
	}

	@Override
	public HttpServletResponse getServletResponse() {
		return this.response;
	}

	@Override
	public void appendHeader(final String name, final String value) {
		this.response.addHeader(checkNotNull(name), value);
	}

	@Override
	public void setContentEncoding(final String encoding) {
		this.response.setCharacterEncoding(encoding);
	}

	@Override
	public void setContentType(final String contentType) {
		this.response.setContentType(contentType);
	}

	@Override
	public void write(final String content) {
		try {
			this.response.getWriter().write(content);
		}
		catch (IOException e) {
			// TODO: Do something with this
			e.printStackTrace();
		}
	}
}
