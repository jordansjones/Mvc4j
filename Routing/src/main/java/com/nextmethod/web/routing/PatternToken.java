package com.nextmethod.web.routing;

/**
 * User: Jordan
 * Date: 8/7/11
 * Time: 7:56 PM
 */
class PatternToken {

	private final PatternTokenType tokenType;
	private final String name;

	PatternToken(final PatternTokenType tokenType, final String name) {
		this.tokenType = tokenType;
		this.name = name;
	}

	@Override
	public String toString() {
		return new StringBuilder("PatternToken_")
			.append(tokenType == null ? "UKNOWN" : tokenType.name())
			.append(String.format(" [Name = '%s']", name))
			.toString();
	}

	public PatternTokenType getTokenType() {
		return tokenType;
	}

	public String getName() {
		return name;
	}
}
