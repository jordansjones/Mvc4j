package nextmethod.threading;

// TODO
public final class CancellationToken {

	public static CancellationToken none() {
		return new CancellationToken();
	}

	public boolean canBeCanceled() {
		return false;
	}

	public boolean isCancellationRequested() {
		return false;
	}
}
