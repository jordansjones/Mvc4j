package nextmethod.web.razor.tokenizer;

import nextmethod.web.razor.text.LookaheadToken;
import nextmethod.web.razor.text.SeekableTextReader;
import nextmethod.web.razor.text.TextExtensions;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TokenizerLookaheadTest extends HtmlTokenizerTestBase {
	
	@Test
	public void afterCancellingLookaheadTokenizerReturnsSameTokensAsItDidBeforeLookahead() throws Exception {
		HtmlTokenizer tokenizer = new HtmlTokenizer(new SeekableTextReader("<foo>"));
		try (LookaheadToken token = TextExtensions.beginLookahead(tokenizer.getSource()))
		{
			assertEquals(new HtmlSymbol(0, 0, 0, "<", HtmlSymbolType.OpenAngle), tokenizer.nextSymbol());
			assertEquals(new HtmlSymbol(1, 0, 1, "foo", HtmlSymbolType.Text), tokenizer.nextSymbol());
			assertEquals(new HtmlSymbol(4, 0, 4, ">", HtmlSymbolType.CloseAngle), tokenizer.nextSymbol());
		}
		assertEquals(new HtmlSymbol(0, 0, 0, "<", HtmlSymbolType.OpenAngle), tokenizer.nextSymbol());
		assertEquals(new HtmlSymbol(1, 0, 1, "foo", HtmlSymbolType.Text), tokenizer.nextSymbol());
		assertEquals(new HtmlSymbol(4, 0, 4, ">", HtmlSymbolType.CloseAngle), tokenizer.nextSymbol());
	}

	@Test
	public void afterAcceptingLookaheadTokenizerReturnsNextToken() throws Exception {
		HtmlTokenizer tokenizer = new HtmlTokenizer(new SeekableTextReader("<foo>"));
		try (LookaheadToken lookahead = TextExtensions.beginLookahead(tokenizer.getSource()))
		{
			assertEquals(new HtmlSymbol(0, 0, 0, "<", HtmlSymbolType.OpenAngle), tokenizer.nextSymbol());
			assertEquals(new HtmlSymbol(1, 0, 1, "foo", HtmlSymbolType.Text), tokenizer.nextSymbol());
			lookahead.accept();
		}
		assertEquals(new HtmlSymbol(4, 0, 4, ">", HtmlSymbolType.CloseAngle), tokenizer.nextSymbol());
	}
}
