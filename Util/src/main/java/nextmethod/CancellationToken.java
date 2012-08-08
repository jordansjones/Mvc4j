package nextmethod;

import nextmethod.annotations.TODO;

@TODO
public final class CancellationToken {

//	private final boolean canceled;

//	public CancellationToken(boolean canceled) {
//		this.canceled = canceled;
//	}

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
