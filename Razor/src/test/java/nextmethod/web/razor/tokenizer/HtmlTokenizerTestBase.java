package nextmethod.web.razor.tokenizer;

import nextmethod.base.Strings;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;

public abstract class HtmlTokenizerTestBase extends TokenizerTestBase<HtmlSymbol, HtmlSymbolType> {

	private static HtmlSymbol ignoreRemaining = new HtmlSymbol(0, 0, 0, Strings.Empty, HtmlSymbolType.Unknown);

	@Override
	protected Tokenizer<HtmlSymbol, HtmlSymbolType> createTokenizer(final ITextDocument source) {
		return new HtmlTokenizer(source);
	}

	@Override
	protected HtmlSymbol getIgnoreRemaining() {
		return ignoreRemaining;
	}

	protected void testSingleToken(final String text, final HtmlSymbolType expectedType) {
		testTokenizer(text, new HtmlSymbol(0, 0, 0, text, expectedType));
	}
}
