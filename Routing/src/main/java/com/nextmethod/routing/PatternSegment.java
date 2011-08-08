package com.nextmethod.routing;

import java.util.List;

/**
 * User: Jordan
 * Date: 8/7/11
 * Time: 8:07 PM
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
