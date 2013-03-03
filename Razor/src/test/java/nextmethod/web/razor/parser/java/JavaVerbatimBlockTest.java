package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaVerbatimBlockTest extends JavaHtmlCodeParserTestBase {

	@Test
	public void verbatimBlock() {
		parseBlockTest(
			"@{ foo(); }",
			new StatementBlock(
				factory().codeTransition(),
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code(" foo(); ").asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void innerImplicitExpressionWithOnlySingleAtOutputsZeroLengthCodeSpan() {
		parseBlockTest(
			"{@}",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().emptyJava().asStatement(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().emptyJava()
						.asImplicitExpression(getKeywordSet(), true)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						
				),
				factory().emptyJava().asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			),
			true,
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtStartOfCodeBlock("}"),
				2, 0, 2
			)
		);
	}

	@Test
	public void innerImplicitExpressionDoesNotAcceptDotAfterAt() {
		parseBlockTest(
			"{@.}",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().emptyJava().asStatement(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().emptyJava()
						.asImplicitExpression(getKeywordSet(), true)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						
				),
				factory().code(".").asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			),
			true,
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtStartOfCodeBlock("."),
				2, 0, 2
			)
		);
	}

	@Test
	public void innerImplicitExpressionWithOnlySingleAtAcceptsSingleSpaceOrNewlineAtDesignTime() {
		parseBlockTest(
			"{" + Environment.NewLine + "    @" + Environment.NewLine + "}",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code("\r\n    ").asStatement(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().emptyJava()
						.asImplicitExpression(getKeywordSet(), true)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						
				),
				factory().code("\r\n").asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			),
			true,
			new RazorError(
				RazorResources().parseErrorUnexpectedWhiteSpaceAtStartOfCodeBlock(),
				8, 1, 5
			)
		);
	}

	@Test
	public void innerImplicitExpressionDoesNotAcceptTrailingNewlineInRunTimeMode() {
		parseBlockTest(
			"{@foo." + Environment.NewLine + "}",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().emptyJava().asStatement(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().code("foo.")
						.asImplicitExpression(getKeywordSet(), true)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						
				),
				factory().code("\r\n").asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void innerImplicitExpressionAcceptsTrailingNewlineInDesignTimeMode() {
		parseBlockTest(
			"{@foo." + Environment.NewLine + "}",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().emptyJava().asStatement(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().code("foo.")
						.asImplicitExpression(getKeywordSet(), true)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						
				),
				factory().code("\r\n").asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			),
			true
		);
	}

}
