package nextmethod.web.razor.tokenizer;

import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import org.junit.Test;

public class HtmlTokenizerTest extends HtmlTokenizerTestBase {

	@SuppressWarnings("ConstantConditions")
	@Test(expected = NullPointerException.class)
	public void constructorThrowsNPEIfNullSourceProvided() {
		new HtmlTokenizer(null);
	}

	@Test
	public void nextReturnsNullWhenEOFReached() {
		testTokenizer("");
	}

	@Test
	public void textIsRecognized() {
		testTokenizer("foo-9309&smlkmb;::-3029022,.sdkq92384", new HtmlSymbol(0, 0, 0, "foo-9309&smlkmb;::-3029022,.sdkq92384", HtmlSymbolType.Text));
	}

}
