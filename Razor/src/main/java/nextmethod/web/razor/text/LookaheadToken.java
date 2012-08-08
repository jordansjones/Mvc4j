package nextmethod.web.razor.text;

import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;

import javax.annotation.Nonnull;

public class LookaheadToken implements IDisposable {

	private Delegates.IFunc cancelAction;
	private boolean accepted;

	public LookaheadToken(@Nonnull final Delegates.IFunc<?> cancelAction) {
		this.cancelAction = cancelAction;
	}

	public void accept() {
		accepted = true;
	}

	@Override
	public void close() {
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
