package nextmethod.web.razor.tokenizer.symbols;

import com.google.common.collect.Iterables;
import nextmethod.base.Delegates;
import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SymbolExtensions {

	private SymbolExtensions() {}

	public static LocationTagged<String> getContent(@Nonnull final SpanBuilder spanBuilder) {
		return getContent(spanBuilder, new Delegates.IFunc1<Iterable<ISymbol>, Iterable<ISymbol>>() {
			@Override
			public Iterable<ISymbol> invoke(@Nullable final Iterable<ISymbol> input1) {
				return input1;
			}
		});
	}

	public static LocationTagged<String> getContent(@Nonnull final SpanBuilder spanBuilder, @Nonnull final Delegates.IFunc1<Iterable<ISymbol>, Iterable<ISymbol>> filter) {
		return getContent(filter.invoke(spanBuilder.getSymbols()), spanBuilder.getStart());
	}

	public static LocationTagged<String> getContent(@Nullable final Iterable<? extends ISymbol> symbols, @Nonnull final SourceLocation spanStart) {
		if (symbols == null || Iterables.isEmpty(symbols)) {
			return new LocationTagged<>("", spanStart);
		}
		else {
			final ISymbol first = Iterables.getFirst(symbols, null);
			final StringBuilder sb = new StringBuilder();
			for (ISymbol symbol : symbols) {
				sb.append(symbol.getContent());
			}
			return new LocationTagged<>(sb.toString(), SourceLocation.add(spanStart, first.getStart()));
		}
	}

	public static LocationTagged<String> getContent(@Nonnull final ISymbol symbol) {
		return new LocationTagged<>(symbol.getContent(), symbol.getStart());
	}
}
