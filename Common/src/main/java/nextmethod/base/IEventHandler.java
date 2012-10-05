package nextmethod.base;

import javax.annotation.Nonnull;

public interface IEventHandler<TEventArgs extends EventArgs> {

	void handleEvent(@Nonnull final Object sender, @Nonnull final TEventArgs e);
}
