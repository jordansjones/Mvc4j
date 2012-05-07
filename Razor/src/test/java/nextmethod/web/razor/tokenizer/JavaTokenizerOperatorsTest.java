package nextmethod.web.razor.tokenizer;

import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import org.junit.Test;

public class JavaTokenizerOperatorsTest extends JavaTokenizerTestBase {

	@Test
	public void leftBraceIsRecognized() {
		testSingleToken("{", JavaSymbolType.LeftBrace);
	}

	@Test
	public void plusIsRecognized() {
		testSingleToken("+", JavaSymbolType.Plus);
	}

	@Test
	public void assignIsRecognized() {
		testSingleToken("=", JavaSymbolType.Assign);
	}

	@Test
	public void arrowIsRecognized() {
		testSingleToken("->", JavaSymbolType.Arrow);
	}

	@Test
	public void andAssignIsRecognized() {
		testSingleToken("&=", JavaSymbolType.AndAssign);
	}

	@Test
	public void rightBraceIsRecognized() {
		testSingleToken("}", JavaSymbolType.RightBrace);
	}

	@Test
	public void minusIsRecognized() {
		testSingleToken("-", JavaSymbolType.Minus);
	}

	@Test
	public void lessThanIsRecognized() {
		testSingleToken("<", JavaSymbolType.LessThan);
	}

	@Test
	public void equalsIsRecognized() {
		testSingleToken("==", JavaSymbolType.Equals);
	}

	@Test
	public void orAssignIsRecognized() {
		testSingleToken("|=", JavaSymbolType.OrAssign);
	}

	@Test
	public void leftBracketIsRecognized() {
		testSingleToken("[", JavaSymbolType.LeftBracket);
	}

	@Test
	public void starIsRecognized() {
		testSingleToken("*", JavaSymbolType.Star);
	}

	@Test
	public void greaterThanIsRecognized() {
		testSingleToken(">", JavaSymbolType.GreaterThan);
	}

	@Test
	public void notEqualIsRecognized() {
		testSingleToken("!=", JavaSymbolType.NotEqual);
	}

	@Test
	public void xorAssignIsRecognized() {
		testSingleToken("^=", JavaSymbolType.XorAssign);
	}

	@Test
	public void rightBracketIsRecognized() {
		testSingleToken("]", JavaSymbolType.RightBracket);
	}

	@Test
	public void slashIsRecognized() {
		testSingleToken("/", JavaSymbolType.Slash);
	}

	@Test
	public void questionMarkIsRecognized() {
		testSingleToken("?", JavaSymbolType.QuestionMark);
	}

	@Test
	public void lessThanEqualIsRecognized() {
		testSingleToken("<=", JavaSymbolType.LessThanEqual);
	}

	@Test
	public void leftShiftIsNotSpeciallyRecognized() {
		testTokenizer("<<",
			new JavaSymbol(0, 0, 0, "<", JavaSymbolType.LessThan),
			new JavaSymbol(1, 0, 1, "<", JavaSymbolType.LessThan));
	}

	@Test
	public void leftParenIsRecognized() {
		testSingleToken("(", JavaSymbolType.LeftParenthesis);
	}

	@Test
	public void moduloIsRecognized() {
		testSingleToken("%", JavaSymbolType.Modulo);
	}

	@Test
	public void nullCoalesceIsRecognized() {
		testSingleToken("??", JavaSymbolType.NullCoalesce);
	}

	@Test
	public void greaterThanEqualIsRecognized() {
		testSingleToken(">=", JavaSymbolType.GreaterThanEqual);
	}

	@Test
	public void equalGreaterThanIsRecognized() {
		testSingleToken("=>", JavaSymbolType.GreaterThanEqual);
	}

	@Test
	public void rightParenIsRecognized() {
		testSingleToken(")", JavaSymbolType.RightParenthesis);
	}

	@Test
	public void andIsRecognized() {
		testSingleToken("&", JavaSymbolType.And);
	}

	@Test
	public void doubleColonIsRecognized() {
		testSingleToken("::", JavaSymbolType.DoubleColon);
	}

	@Test
	public void plusAssignIsRecognized() {
		testSingleToken("+=", JavaSymbolType.PlusAssign);
	}

	@Test
	public void semicolonIsRecognized() {
		testSingleToken(";", JavaSymbolType.Semicolon);
	}

	@Test
	public void tildeIsRecognized() {
		testSingleToken("~", JavaSymbolType.Tilde);
	}

	@Test
	public void doubleOrIsRecognized() {
		testSingleToken("||", JavaSymbolType.DoubleOr);
	}

	@Test
	public void moduloAssignIsRecognized() {
		testSingleToken("%=", JavaSymbolType.ModuloAssign);
	}

	@Test
	public void colonIsRecognized() {
		testSingleToken(":", JavaSymbolType.Colon);
	}

	@Test
	public void notIsRecognized() {
		testSingleToken("!", JavaSymbolType.Not);
	}

	@Test
	public void doubleAndIsRecognized() {
		testSingleToken("&&", JavaSymbolType.DoubleAnd);
	}

	@Test
	public void divideAssignIsRecognized() {
		testSingleToken("/=", JavaSymbolType.DivideAssign);
	}

	@Test
	public void commaIsRecognized() {
		testSingleToken(",", JavaSymbolType.Comma);
	}

	@Test
	public void xorIsRecognized() {
		testSingleToken("^", JavaSymbolType.Xor);
	}

	@Test
	public void decrementIsRecognized() {
		testSingleToken("--", JavaSymbolType.Decrement);
	}

	@Test
	public void multiplyAssignIsRecognized() {
		testSingleToken("*=", JavaSymbolType.MultiplyAssign);
	}

	@Test
	public void dotIsRecognized() {
		testSingleToken(".", JavaSymbolType.Dot);
	}

	@Test
	public void orIsRecognized() {
		testSingleToken("|", JavaSymbolType.Or);
	}

	@Test
	public void incrementIsRecognized() {
		testSingleToken("++", JavaSymbolType.Increment);
	}

	@Test
	public void minusAssignIsRecognized() {
		testSingleToken("-=", JavaSymbolType.MinusAssign);
	}

	@Test
	public void rightShiftIsNotSpeciallyRecognized() {
		testTokenizer(">>",
			new JavaSymbol(0, 0, 0, ">", JavaSymbolType.GreaterThan),
			new JavaSymbol(1, 0, 1, ">", JavaSymbolType.GreaterThan));
	}

	@Test
	public void hashIsRecognized() {
		testSingleToken("#", JavaSymbolType.Hash);
	}

}
