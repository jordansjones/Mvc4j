package nextmethod.web.mvc;

/**
 *
 */
enum ClassPathType {
	Class(".class"),
	Jar(".jar");

	private final String suffix;

	ClassPathType(final String suffix) {
		this.suffix = suffix;
	}

	public String suffix() {
		return suffix;
	}
}
