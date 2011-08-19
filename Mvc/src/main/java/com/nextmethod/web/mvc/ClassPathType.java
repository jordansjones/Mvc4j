package com.nextmethod.web.mvc;

/**
 *
 */
enum ClassPathType {
	Path(".class"),
	Jar(".jar");

	private final String suffix;

	ClassPathType(final String suffix) {
		this.suffix = suffix;
	}

	public String suffix() {
		return suffix;
	}
}
