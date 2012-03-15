package nextmethod.reflection;

public class AmbiguousMatchException extends RuntimeException {

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
