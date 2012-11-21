package nextmethod.base;

import java.util.Collection;

public class AggregateException extends RuntimeException {

	public AggregateException() {
	}

	public AggregateException(final String message) {
		super(message);
	}

	public AggregateException(final Collection<Exception> exceptions) {

	}

	public AggregateException(final String message, final Collection<Exception> exceptions) {

	}
}
