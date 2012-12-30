package nextmethod.web.razor.tokenizer;

import nextmethod.base.Strings;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;

public class JavaTokenizerTestBase extends TokenizerTestBase<JavaSymbol, JavaSymbolType> {

	private static final JavaSymbol ignoreRemaining = new JavaSymbol(0, 0, 0, Strings.Empty, JavaSymbolType.Unknown);


	@Override
	protected JavaSymbol getIgnoreRemaining() {
		return ignoreRemaining;
	}

	@Override
	protected Tokenizer<JavaSymbol, JavaSymbolType> createTokenizer(final ITextDocument source) {
		return new JavaTokenizer(source);
	}

	protected void testSingleToken(final String text, final JavaSymbolType expectedSymbolType) {
		testTokenizer(text, new JavaSymbol(0, 0, 0, text, expectedSymbolType));
	}
}
