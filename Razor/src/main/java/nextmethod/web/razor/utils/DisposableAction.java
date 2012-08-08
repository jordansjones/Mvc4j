package nextmethod.web.razor.utils;

import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;

import static com.google.common.base.Preconditions.checkNotNull;

public class DisposableAction implements IDisposable {

	private final Delegates.IAction action;

	public DisposableAction(final Delegates.IAction action) {
		this.action = checkNotNull(action);
	}

	@Override
	public void close() {
		dispose(true);
	}

	protected void dispose(final boolean disposing) {
		if (disposing) {
			action.invoke();
		}
	}

}
