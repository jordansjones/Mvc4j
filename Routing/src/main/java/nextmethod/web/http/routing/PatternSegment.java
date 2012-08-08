package nextmethod.web.http.routing;

import java.util.List;

/**
 * 
 */
class PatternSegment {

	private final boolean allLiteral;
	private final List<PatternToken> tokens;

	PatternSegment(final boolean allLiteral, final List<PatternToken> tokens) {
		this.allLiteral = allLiteral;
		this.tokens = tokens;
	}

	public boolean isAllLiteral() {
		return allLiteral;
	}

	public List<PatternToken> getTokens() {
		return tokens;
	}
}
