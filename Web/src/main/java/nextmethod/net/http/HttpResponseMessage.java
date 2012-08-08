package nextmethod.net.http;

import nextmethod.annotations.TODO;
import nextmethod.base.IDisposable;
import nextmethod.net.HttpStatusCode;

import javax.annotation.Nonnull;

/**
 *
 */
@TODO
public class HttpResponseMessage implements IDisposable {

	private Object content;
	private final Object headers;
	private String reasonPhrase;
	private HttpRequestMessage requestMessage;
	private HttpStatusCode statusCode;
	private Object version;

	public HttpResponseMessage() {

	}

	public HttpResponseMessage(@Nonnull final HttpStatusCode statusCode) {

	}

	/**
	 * Throws an exception if {@link #isSuccessStatusCode} for the HTTP response is <strong>false</strong>.
	 */
	@TODO
	public HttpResponseMessage ensureSuccessStatusCode() {
		return null;
	}

	@TODO
	public boolean isSuccessStatusCode() {
		return true;
	}

	@Override
	public void close() {
	}

	public Object getHeaders() {
		return headers;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

	public void setReasonPhrase(String reasonPhrase) {
		this.reasonPhrase = reasonPhrase;
	}

	public HttpRequestMessage getRequestMessage() {
		return requestMessage;
	}

	public void setRequestMessage(HttpRequestMessage requestMessage) {
		this.requestMessage = requestMessage;
	}

	public HttpStatusCode getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(HttpStatusCode statusCode) {
		this.statusCode = statusCode;
	}

	public Object getVersion() {
		return version;
	}

	public void setVersion(Object version) {
		this.version = version;
	}
}
