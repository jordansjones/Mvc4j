package nextmethod.web.razor;

import com.google.common.base.Optional;
import nextmethod.base.NotImplementedException;
import nextmethod.threading.CancellationToken;
import nextmethod.web.razor.text.ITextBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO
public class RazorTemplateEngine {

	public RazorTemplateEngine(@Nonnull final RazorEngineHost host) {
	}

	public GeneratorResults generateCode(@Nonnull final ITextBuffer input, @Nullable final String className, @Nullable final String rootNamespace, @Nonnull final String sourceFileName, @Nonnull final Optional<CancellationToken> cancelToken) {
		throw new NotImplementedException();
	}
}
