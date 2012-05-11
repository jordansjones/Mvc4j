package nextmethod.web.razor.parser;

import com.google.common.base.Function;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.Tokenizer;
import nextmethod.web.razor.tokenizer.TokenizerView;
import nextmethod.web.razor.tokenizer.symbols.SymbolBase;

import javax.annotation.Nonnull;

public abstract class TokenizerBackedParser<
	TTokenizer extends Tokenizer<TSymbol, TSymbolType>,
	TSymbol extends SymbolBase<TSymbolType>,
	TSymbolType
	> extends ParserBase {

	private TokenizerView<TTokenizer, TSymbol, TSymbolType> tokenizer;
	private SpanBuilder span;
	private Function<SpanBuilder, SpanBuilder> spanConfig;
	private TSymbol previousSymbol;

	protected TokenizerBackedParser() {
		this.span = new SpanBuilder();
	}


	protected SpanBuilder getSpan() {
		return span;
	}

	protected void setSpan(@Nonnull final SpanBuilder span) {
		this.span = span;
	}

	protected TokenizerView<TTokenizer, TSymbol, TSymbolType> getTokenizer() {
		return tokenizer != null ? tokenizer : initTokenizer();
	}

	private TokenizerView<TTokenizer, TSymbol, TSymbolType> initTokenizer() {
		return this.tokenizer = new TokenizerView<TTokenizer, TSymbol, TSymbolType>(getLanguage().createTokenizer(getContext().getSource()));
	}

	protected Function<SpanBuilder, SpanBuilder> getSpanConfig() {
		return spanConfig;
	}

	protected void setSpanConfig(@Nonnull final Function<SpanBuilder, SpanBuilder> spanConfig) {
		this.spanConfig = spanConfig;
	}

	protected TSymbol getPreviousSymbol() {
		return previousSymbol;
	}

	protected TSymbol getCurrentSymbol() {
		return tokenizer.getCurrent();
	}

	protected SourceLocation getCurrentLocation() {
		return (isEndOfFile() || getCurrentSymbol() == null) ? getContext().getSource().getLocation() : getCurrentSymbol().getStart();
	}

	protected boolean isEndOfFile() {
		return tokenizer.isEndOfFile();
	}

	protected abstract LanguageCharacteristics<TTokenizer, TSymbol, TSymbolType> getLanguage();

	protected void handleEmbeddedTransition() {

	}

	protected boolean isAtEmbeddedTransition(final boolean allowTemplatesAndComments, final boolean allowTransitions) {
		return false;
	}

	public void buildSpan(@Nonnull final SpanBuilder span, @Nonnull final SourceLocation start, @Nonnull final String content) {
		for (TSymbol tSymbol : getLanguage().tokenizeString(start, content)) {
			span.accept(tSymbol);
		}
	}

	protected void initialize(@Nonnull final SpanBuilder span) {
		if (spanConfig != null) {
			spanConfig.apply(span);
		}
	}

	protected boolean nextToken() {
		previousSymbol = getCurrentSymbol();
		return tokenizer.next();
	}
}
