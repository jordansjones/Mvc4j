package nextmethod.web.razor.text;

import nextmethod.base.IAction;
import nextmethod.base.IDisposable;

import javax.annotation.Nonnull;

public class LookaheadToken implements IDisposable {

	private IAction cancelAction;
	private boolean accepted;

	public LookaheadToken(@Nonnull final IAction<?> cancelAction) {
		this.cancelAction = cancelAction;
	}

	public void accept() {
		accepted = true;
	}

	@Override
	public void close() throws Exception {
		dispose(true);
	}

	public void dispose() {
		dispose(true);
	}

	protected void dispose(final boolean disposing) {
		if (!accepted)
			cancelAction.invoke();
	}
}
