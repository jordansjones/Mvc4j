package nextmethod.web.http.routing;

/**
 *
 */
class BoundRouteTemplate {

	private final String boundTemplate;
	private final HttpRouteValueDictionary values;

	BoundRouteTemplate(String boundTemplate, HttpRouteValueDictionary values) {
		this.boundTemplate = boundTemplate;
		this.values = values;
	}

	public String getBoundTemplate() {
		return boundTemplate;
	}

	public HttpRouteValueDictionary getValues() {
		return values;
	}
}
