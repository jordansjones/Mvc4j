package com.nextmethod.routing;

/**
 * User: jordanjones
 * Date: 8/8/11
 * Time: 10:33 AM
 */
class TestUrl {

	private final String url;
	private final String expected;
	private final String label;
	private final Class<? extends Exception> expectedException;

	TestUrl(String url, String expected, String label, Class<? extends Exception> expectedException) {
		this.url = url;
		this.expected = expected;
		this.label = label;
		this.expectedException = expectedException;
	}

	TestUrl(String url, String label, Class<? extends Exception> expectedException) {
		this(url, null, label, expectedException);
	}

	TestUrl(String url, String expected, String label) {
		this(url, expected, label, null);
	}

	public String getUrl() {
		return url;
	}

	public String getExpected() {
		return expected;
	}

	public String getLabel() {
		return label;
	}

	public Class<? extends Exception> getExpectedException() {
		return expectedException;
	}
}
