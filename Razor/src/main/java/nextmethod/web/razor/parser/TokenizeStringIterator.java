package nextmethod.web.razor.parser;

import nextmethod.base.Delegates;
import nextmethod.collections.IterableIterator;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SeekableTextReader;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.Tokenizer;
import nextmethod.web.razor.tokenizer.symbols.SymbolBase;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
class TokenizeStringIterator<
	TTokenizer extends Tokenizer<TSymbol, TSymbolType>,
	TSymbol extends SymbolBase<TSymbolType>,
	TSymbolType extends Enum<TSymbolType>
	> extends IterableIterator<TSymbol> {

	private final SourceLocation start;
	private final String input;
	private final Delegates.IFunc1<ITextDocument, TTokenizer> createTokenizerDelegate;

	TokenizeStringIterator(@Nonnull final SourceLocation start, @Nonnull final String input, @Nonnull final Delegates.IFunc1<ITextDocument, TTokenizer> createTokenizerDelegate) {
		this.start = checkNotNull(start);
		this.input = checkNotNull(input);
		this.createTokenizerDelegate = checkNotNull(createTokenizerDelegate);
	}

	private SeekableTextReader textReader;
	private TTokenizer tokenizer;

	@Override
	protected TSymbol computeNext() {
		final TTokenizer tok = ensureTokenizer();
		TSymbol sym;
		if ((sym = tok.nextSymbol()) != null) {
			sym.offsetStart(start);
			return sym;
		}

		textReader.close();

		return endOfData();
	}

	private TTokenizer ensureTokenizer() {
		if (textReader == null) {
			this.textReader = new SeekableTextReader(this.input);
			this.tokenizer = createTokenizerDelegate.invoke(this.textReader);
		}
		return this.tokenizer;
	}


}
