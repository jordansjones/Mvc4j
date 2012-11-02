package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.generator.MarkupCodeGenerator;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import org.junit.Test;

public class JavaNestedStatementsTest extends JavaHtmlCodeParserTestBase {

	@Test
	public void nestedSimpleStatement() {
		parseBlockTest(
			"@while(true) { foo(); }",
			new StatementBlock(
				factory().codeTransitionAndBuild(),
				factory().code("while(true) { foo(); }")
					.asStatement()
					.acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void nestedKeywordStatement() {
		parseBlockTest(
			"@while(true) { for(int i = 0; i < 10; i++) { foo(); } }",
			new StatementBlock(
				factory().codeTransitionAndBuild(),
				factory().code("while(true) { for(int i = 0; i < 10; i++) { foo(); } }")
					.asStatement()
					.acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void nestedCodeBlock() {
		parseBlockTest(
			"@while(true) { { { { foo(); } } } }",
			new StatementBlock(
				factory().codeTransitionAndBuild(),
				factory().code("while(true) { { { { foo(); } } } }")
					.asStatement()
					.acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void nestedImplicitExpression() {
		parseBlockTest(
			"@while(true) { @foo }",
			new StatementBlock(
				factory().codeTransitionAndBuild(),
				factory().code("while(true) { ").asStatementAndBuild(),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().code("foo")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords, true)
						.accepts(AcceptedCharacters.NonWhiteSpace).build()
				),
				factory().code(" }")
					.asStatement()
					.acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void nestedExplicitExpression() {
		parseBlockTest(
			"@while(true) { @(foo) }",
			new StatementBlock(
				factory().codeTransitionAndBuild(),
				factory().code("while(true) { ").asStatementAndBuild(),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().metaCode("(").acceptsNoneAndBuild(),
					factory().code("foo")
						.asExpressionAndBuild(),
					factory().metaCode(")").acceptsNoneAndBuild()
				),
				factory().code(" }")
					.asStatement()
					.acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void nestedMarkupBlock() {
		parseBlockTest(
			"@while(true) { <p>Hello</p> }",
			new StatementBlock(
				factory().codeTransitionAndBuild(),
				factory().code("while(true) {").asStatementAndBuild(),
				new MarkupBlock(
					factory().markup(" <p>Hello</p> ")
						.with(new MarkupCodeGenerator())
						.acceptsNoneAndBuild()
				),
				factory().code("}")
					.asStatement()
					.acceptsNoneAndBuild()
			)
		);
	}

}
