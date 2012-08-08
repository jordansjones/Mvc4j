package nextmethod.net.http;

/**
 *
 */
public enum HttpMethod {
	Delete,
	Get,
	Head,
	Options,
	Post,
	Put,
	Trace,
	;

	private final String method;

	private HttpMethod() {
		this.method = this.name().toUpperCase();
	}

	public String method() {
		return this.method;
	}
}
