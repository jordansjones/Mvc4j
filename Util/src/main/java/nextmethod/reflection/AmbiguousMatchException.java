package nextmethod.reflection;

/**
 * An exception that is thrown when binding to a member results in more than one member matching the binding criteria.
 */
public final class AmbiguousMatchException extends RuntimeException {

	public AmbiguousMatchException() {
		super();
	}

	public AmbiguousMatchException(Throwable cause) {
		super(cause);
	}

	public AmbiguousMatchException(String message) {
		super(message);
	}

	public AmbiguousMatchException(String message, Throwable cause) {
		super(message, cause);
	}

}
