package nextmethod.net.http;

import nextmethod.CancellationToken;
import nextmethod.base.IDisposable;
import nextmethod.threading.Task;

import javax.annotation.Nonnull;

/**
 *
 */
public abstract class HttpMessageHandler implements IDisposable {

	protected HttpMessageHandler() {

	}

	protected abstract Task<HttpResponseMessage> sendAsync(@Nonnull final HttpRequestMessage request, @Nonnull final CancellationToken cancellationToken);

}
