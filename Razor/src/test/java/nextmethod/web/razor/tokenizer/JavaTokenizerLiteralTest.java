package nextmethod.web.razor.tokenizer;

import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import org.junit.Test;

public class JavaTokenizerLiteralTest extends JavaTokenizerTestBase {

	@Test
	public void simpleIntegerLiteralIsRecognized() {
		testSingleToken("01189998819991197253", JavaSymbolType.IntegerLiteral);
	}

	@Test
	public void integerTypeSuffixIsRecognized() {
		testSingleToken("42L", JavaSymbolType.IntegerLiteral);
		testSingleToken("42l", JavaSymbolType.IntegerLiteral);
	}

	@Test
	public void trailingLetterIsNotPartOfIntegerLiteralIfNotTypeSufix() {
		testTokenizer("42a", new JavaSymbol(0, 0, 0, "42", JavaSymbolType.IntegerLiteral), getIgnoreRemaining());
	}

	@Test
	public void simpleHexLiteralIsRecognized() {
		testSingleToken("0x0123456789ABCDEF", JavaSymbolType.IntegerLiteral);
	}

	@Test
	public void integerTypeSuffixIsRecognizedInHexLiteral() {
		testSingleToken("0xDEADBEEFL", JavaSymbolType.IntegerLiteral);
		testSingleToken("0xDEADBEEFl", JavaSymbolType.IntegerLiteral);
	}

	@Test
	public void trailingLetterIsNotPartOfHexLiteralIfNotTypeSufix() {
		testTokenizer("0xDEADBEEFz", new JavaSymbol(0, 0, 0, "0xDEADBEEF", JavaSymbolType.IntegerLiteral), getIgnoreRemaining());
	}

	@Test
	public void dotFollowedByNonDigitIsNotPartOfRealLiteral() {
		testTokenizer("3.a", new JavaSymbol(0, 0, 0, "3", JavaSymbolType.IntegerLiteral), getIgnoreRemaining());
	}

	@Test
	public void simpleRealLiteralIsRecognized() {
		testTokenizer("3.14159", new JavaSymbol(0, 0, 0, "3.14159", JavaSymbolType.RealLiteral));
	}

	@Test
	public void realLiteralBetweenZeroAndOneIsRecognized() {
		testTokenizer(".14159", new JavaSymbol(0, 0, 0, ".14159", JavaSymbolType.RealLiteral));
	}

	@Test
	public void integerWithRealTypeSuffixIsRecognized() {
		testSingleToken("42F", JavaSymbolType.RealLiteral);
		testSingleToken("42f", JavaSymbolType.RealLiteral);
		testSingleToken("42D", JavaSymbolType.RealLiteral);
		testSingleToken("42d", JavaSymbolType.RealLiteral);
	}

	@Test
	public void integerWithExponentIsRecognized() {
		testSingleToken("1e10", JavaSymbolType.RealLiteral);
		testSingleToken("1E10", JavaSymbolType.RealLiteral);
		testSingleToken("1e+10", JavaSymbolType.RealLiteral);
		testSingleToken("1E+10", JavaSymbolType.RealLiteral);
		testSingleToken("1e-10", JavaSymbolType.RealLiteral);
		testSingleToken("1E-10", JavaSymbolType.RealLiteral);
	}

	@Test
	public void realNumberWithTypeSuffixIsRecognized() {
		testSingleToken("3.14F", JavaSymbolType.RealLiteral);
		testSingleToken("3.14f", JavaSymbolType.RealLiteral);
		testSingleToken("3.14D", JavaSymbolType.RealLiteral);
		testSingleToken("3.14d", JavaSymbolType.RealLiteral);
	}

	@Test
	public void realNumberWithExponentIsRecognized() {
		testSingleToken("3.14E10", JavaSymbolType.RealLiteral);
		testSingleToken("3.14e10", JavaSymbolType.RealLiteral);
		testSingleToken("3.14E+10", JavaSymbolType.RealLiteral);
		testSingleToken("3.14e+10", JavaSymbolType.RealLiteral);
		testSingleToken("3.14E-10", JavaSymbolType.RealLiteral);
		testSingleToken("3.14e-10", JavaSymbolType.RealLiteral);
	}

	@Test
	public void realNumberWithExponentAndTypeSuffixIsRecognized() {
		testSingleToken("3.14E+10F", JavaSymbolType.RealLiteral);
	}

	@Test
	public void singleCharacterLiteralIsRecognized() {
		testSingleToken("'f'", JavaSymbolType.CharacterLiteral);
	}

	@Test
	public void multiCharacterLiteralIsRecognized() {
		testSingleToken("'foo'", JavaSymbolType.CharacterLiteral);
	}

	@Test
	public void characterLiteralIsTerminatedByEOFIfUnterminated() {
		testSingleToken("'foo bar", JavaSymbolType.CharacterLiteral);
	}

	@Test
	public void characterLiteralNotTerminatedByEscapedQuote() {
		testSingleToken("'foo\\'bar'", JavaSymbolType.CharacterLiteral);
	}

	@Test
	public void characterLiteralIsTerminatedByEOLIfUnterminated() {
		testTokenizer("'foo\n", new JavaSymbol(0, 0, 0, "'foo", JavaSymbolType.CharacterLiteral), getIgnoreRemaining());
	}

	@Test
	public void stringLiteralIsRecognized() {
		testSingleToken("\"foo\"", JavaSymbolType.StringLiteral);
	}

	@Test
	public void stringLiteralIsTerminatedByEOFIfUnterminated() {
		testSingleToken("\"foo bar", JavaSymbolType.StringLiteral);
	}

	@Test
	public void stringLiteralNotTerminatedByEscapedQuote() {
		testSingleToken("\"foo\\\"bar\"", JavaSymbolType.StringLiteral);
	}

	@Test
	public void stringLiteralIsTerminatedByEOLIfUnterminated() {
		testTokenizer("\"foo\n", new JavaSymbol(0, 0, 0, "\"foo", JavaSymbolType.StringLiteral), getIgnoreRemaining());
	}

	@Test
	public void verbatimStringLiteralCanContainNewlines() {
		testSingleToken("@\"foo\nbar\nbaz\"", JavaSymbolType.StringLiteral);
	}

	@Test
	public void verbatimStringLiteralNotTerminatedByEscapedDoubleQuote() {
		testSingleToken("@\"foo\"\"bar\"", JavaSymbolType.StringLiteral);
	}

	@Test
	public void verbatimStringLiteralIsTerminatedBySlashDoubleQuote() {
		testTokenizer("@\"foo\\\"bar\"", new JavaSymbol(0, 0, 0, "@\"foo\\\"", JavaSymbolType.StringLiteral), getIgnoreRemaining());
	}

	@Test
	public void verbatimStringLiteralIsTerminatedByEOF() {
		testSingleToken("@\"foo", JavaSymbolType.StringLiteral);
	}
}
