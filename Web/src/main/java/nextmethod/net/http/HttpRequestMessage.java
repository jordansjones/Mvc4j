package nextmethod.net.http;

import nextmethod.annotations.TODO;
import nextmethod.base.IDisposable;
import nextmethod.collect.DictionaryImpl;
import nextmethod.collect.IDictionary;

import javax.annotation.Nonnull;
import java.net.URI;

/**
 *
 */
@TODO
public class HttpRequestMessage implements IDisposable {

	private Object content;
	private final Object headers = new Object();
	private HttpMethod method;
	private final IDictionary<String, Object> properties = new DictionaryImpl<>();
	private URI requestUri;
	private Object version;

	public HttpRequestMessage() {

	}

	public HttpRequestMessage(@Nonnull final HttpMethod method, @Nonnull final String requestUri) {

	}

	public HttpRequestMessage(@Nonnull final HttpMethod method, @Nonnull final URI requestUri) {

	}

	@Override
	public void close() {
	}

	public Object getHeaders() {
		return headers;
	}

	public IDictionary<String, Object> getProperties() {
		return properties;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public URI getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(URI requestUri) {
		this.requestUri = requestUri;
	}

	public Object getVersion() {
		return version;
	}

	public void setVersion(Object version) {
		this.version = version;
	}
}
