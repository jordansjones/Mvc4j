package nextmethod.web.razor.tokenizer.symbols;

import nextmethod.base.Delegates;
import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.text.LocationTagged;

import javax.annotation.Nonnull;

public final class SymbolExtensions {

	private SymbolExtensions() {}

	public static LocationTagged<String> getContent(@Nonnull final SpanBuilder spanBuilder) {
		throw new NotImplementedException();
	}

	public static LocationTagged<String> getContent(@Nonnull final SpanBuilder spanBuilder, @Nonnull final Delegates.IFunc1<Iterable<ISymbol>, Iterable<ISymbol>> filter) {
		throw new NotImplementedException();
	}
}
