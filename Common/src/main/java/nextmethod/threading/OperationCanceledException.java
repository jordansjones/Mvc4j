package nextmethod.threading;

import com.google.common.base.Optional;

// TODO
public class OperationCanceledException extends RuntimeException {

	private Optional<CancellationToken> token;

	public OperationCanceledException() {
	}

	public OperationCanceledException(final CancellationToken token) {
		this();
		this.token = Optional.fromNullable(token);
	}
}
