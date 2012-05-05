package nextmethod.web.razor.tokenizer;

import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;
import nextmethod.web.razor.tokenizer.symbols.SymbolBase;

import javax.annotation.Nonnull;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class TokenizerView<
	TTokenizer extends Tokenizer<TSymbol, TSymbolType>,
	TSymbol extends SymbolBase<TSymbolType> & ISymbol,
	TSymbolType
> {

	private final TTokenizer tokenizer;
	private boolean endOfFile;
	private TSymbol current;

	public TokenizerView(@Nonnull final TTokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	public boolean next() {
		this.current = this.tokenizer.nextSymbol();
		this.endOfFile = (this.current == null);
		return !this.endOfFile;
	}

	public void putBack(@Nonnull final TSymbol symbol) {
		final ITextDocument source = getSource();
		assert source.getPosition() == symbol.getStart().getAbsoluteIndex() + symbol.getContent().length();

		if (source.getPosition() != symbol.getStart().getAbsoluteIndex() + symbol.getContent().length()) {
			// Passed the symbol
			throw new UnsupportedOperationException(String.format(
				RazorResources().getString("tokenizerView.cannotPutBack"),
				symbol.getStart().getAbsoluteIndex() + symbol.getContent().length(),
				source.getPosition()
			));
		}

		source.setPosition(source.getPosition() - symbol.getContent().length());
		current = null;
		endOfFile = source.getPosition() >= source.getLength();
		tokenizer.reset();
	}

	public ITextDocument getSource() {
		return tokenizer.getSource();
	}

	public TTokenizer getTokenizer() {
		return tokenizer;
	}

	public boolean isEndOfFile() {
		return endOfFile;
	}

	public TSymbol getCurrent() {
		return current;
	}
}
