package nextmethod.web.razor.parser;

import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.Tokenizer;
import nextmethod.web.razor.tokenizer.symbols.SymbolBase;

import javax.annotation.Nonnull;

// TODO
public abstract class LanguageCharacteristics<
	TTokenizer extends Tokenizer<TSymbol, TSymbolType>,
	TSymbol extends SymbolBase<TSymbolType>,
	TSymbolType
	> {

	public abstract String getSample(@Nonnull final TSymbolType type);

	public abstract TTokenizer createTokenizer(@Nonnull final ITextDocument source);

	public Iterable<TSymbol> tokenizeString(@Nonnull final SourceLocation start, @Nonnull final String input) {
		// TODO
		return null;
	}
}
