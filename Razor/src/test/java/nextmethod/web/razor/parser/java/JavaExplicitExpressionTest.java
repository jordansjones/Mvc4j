package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class JavaExplicitExpressionTest extends JavaHtmlCodeParserTestBase {

	private static final String NewLine = "\r\n";

	@Test
	public void parseBlockShouldOutputZeroLengthCodeSpanIfExplicitExpressionIsEmpty() {
		parseBlockTest(
			"@()",
			new ExpressionBlock(
				factory().codeTransition().build(),
				factory().metaCode("(").accepts(AcceptedCharacters.None).build(),
				factory().emptyJava().asExpression().build(),
				factory().metaCode(")").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockShouldOutputZeroLengthCodeSpanIfEOFOccursAfterStartOfExplicitExpression() {
		parseBlockTest(
			"@(",
			new ExpressionBlock(
				factory().codeTransition().build(),
				factory().metaCode("(").accepts(AcceptedCharacters.None).build(),
				factory().emptyJava().asExpression().build()
			),
			new RazorError(
				RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
					RazorResources().blockNameExplicitExpression(),
					")",
					"("
				),
				new SourceLocation(1, 0, 1)
			)
		);
	}

	@Test
	public void parseBlockShouldAcceptEscapedQuoteInNonVerbatimStrings() {
		parseBlockTest(
			"@(\"\\\"\")",
			new ExpressionBlock(
				factory().codeTransition().build(),
				factory().metaCode("(").accepts(AcceptedCharacters.None).build(),
				factory().code("\"\\\"\"").asExpression().build(),
				factory().metaCode(")").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockShouldAcceptEscapedQuoteInVerbatimStrings() {
		parseBlockTest(
			"@(@\"\"\"\")",
			new ExpressionBlock(
				factory().codeTransition().build(),
				factory().metaCode("(").accepts(AcceptedCharacters.None).build(),
				factory().code("@\"\"\"\"").asExpression().build(),
				factory().metaCode(")").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockShouldAcceptMultipleRepeatedEscapedQuoteInVerbatimStrings() {
		parseBlockTest(
			"@(@\"\"\"\"\"\")",
			new ExpressionBlock(
				factory().codeTransition().build(),
				factory().metaCode("(").accepts(AcceptedCharacters.None).build(),
				factory().code("@\"\"\"\"\"\"").asExpression().build(),
				factory().metaCode(")").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockShouldAcceptMultiLineVerbatimStrings() {
		parseBlockTest(
			"@(@\"" + NewLine
			+ "Foo" + NewLine
			+ "Bar" + NewLine
			+ "Baz" + NewLine
			+ "\")",
			new ExpressionBlock(
				factory().codeTransition().build(),
				factory().metaCode("(").accepts(AcceptedCharacters.None).build(),
				factory().code("@\"\r\nFoo\r\nBar\r\nBaz\r\n\"").asExpression().build(),
				factory().metaCode(")").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockShouldAcceptMultipleEscapedQuotesInNonVerbatimStrings() {
		parseBlockTest(
			"@(\"\\\"hello, world\\\"\")",
			new ExpressionBlock(
				factory().codeTransition().build(),
				factory().metaCode("(").accepts(AcceptedCharacters.None).build(),
				factory().code("\"\\\"hello, world\\\"\"").asExpression().build(),
				factory().metaCode(")").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockShouldAcceptMultipleEscapedQuotesInVerbatimStrings() {
		parseBlockTest(
			"@(@\"\"\"hello, world\"\"\")",
			new ExpressionBlock(
				factory().codeTransition().build(),
				factory().metaCode("(").accepts(AcceptedCharacters.None).build(),
				factory().code("@\"\"\"hello, world\"\"\"").asExpression().build(),
				factory().metaCode(")").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockShouldAcceptConsecutiveEscapedQuotesInNonVerbatimStrings() {
		parseBlockTest(
			"@(\"\\\"\\\"\")",
			new ExpressionBlock(
				factory().codeTransition().build(),
				factory().metaCode("(").accepts(AcceptedCharacters.None).build(),
				factory().code("\"\\\"\\\"\"").asExpression().build(),
				factory().metaCode(")").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockShouldAcceptConsecutiveEscapedQuotesInVerbatimStrings() {
		parseBlockTest(
			"@(@\"\"\"\"\"\")",
			new ExpressionBlock(
				factory().codeTransition().build(),
				factory().metaCode("(").accepts(AcceptedCharacters.None).build(),
				factory().code("@\"\"\"\"\"\"").asExpression().build(),
				factory().metaCode(")").accepts(AcceptedCharacters.None).build()
			)
		);
	}
}
