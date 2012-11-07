package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.editor.SingleLineMarkupEditHandler;
import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.JavaLanguageCharacteristics;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.parser.syntaxtree.TemplateBlock;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class JavaTemplateTest extends JavaHtmlCodeParserTestBase {

	private static final String testTemplateCode = " @<p>Foo #@item</p>";

	private TemplateBlock testTemplate() {
		return new TemplateBlock(new MarkupBlock(
			factory().markupTransition().build(),
			factory().markup("<p>Foo #").build(),
			new ExpressionBlock(
				factory().codeTransition().build(),
				factory().code("item")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.NonWhiteSpace).build()
			),
			factory().markup("</p>").accepts(AcceptedCharacters.None).build()
		));
	}

	private static final String testNestedTemplateCode = " @<p>Foo #@Html.Repeat(10, @<p>@item</p>)</p>";

	private TemplateBlock testNestedTemplate() {
		return new TemplateBlock(
			new MarkupBlock(
				factory().markupTransition().build(),
				factory().markup("<p>Foo #").build(),
				new ExpressionBlock(
					factory().codeTransition().build(),
					factory().code("Html.Repeat(10, ")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords).build(),
					new TemplateBlock(
						new MarkupBlock(
							factory().markupTransition().build(),
							factory().markup("<p>").build(),
							new ExpressionBlock(
								factory().codeTransition().build(),
								factory().code("item")
									.asImplicitExpression(JavaCodeParser.DefaultKeywords)
									.accepts(AcceptedCharacters.NonWhiteSpace).build()
							),
							factory().markup("</p>").accepts(AcceptedCharacters.None).build()
						)
					),
					factory().code(")")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace).build()
				),
				factory().markup("</p>").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockHandlesSingleLineTemplate() {
		parseBlockTest(
			"{ var foo = @: bar" + Environment.NewLine + "; }",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None).build(),
				factory().code(" var foo = ").asStatement().build(),
				new TemplateBlock(
					new MarkupBlock(
						factory().markupTransition().build(),
						factory().metaMarkup(":", HtmlSymbolType.Colon).build(),
						factory().markup(" bar" + Environment.NewLine)
							.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()))
							.accepts(AcceptedCharacters.None).build()
					)
				),
				factory().code("; ").asStatement().build(),
				factory().metaCode("}").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockHandlesSingleLineImmediatelyFollowingStatementChar() {
		parseBlockTest(
			"{i@: bar" + Environment.NewLine + "}",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None).build(),
				factory().code("i").asStatement().build(),
				new TemplateBlock(
					new MarkupBlock(
						factory().markupTransition().build(),
						factory().metaMarkup(":", HtmlSymbolType.Colon).build(),
						factory().markup(" bar" + Environment.NewLine)
							.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()))
							.accepts(AcceptedCharacters.None).build()
					)
				),
				factory().emptyJava().asStatement().build(),
				factory().metaCode("}").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockHandlesSimpleTemplateInExplicitExpresionParens() {
		parseBlockTest(
			"(Html.Repeat(10," + testTemplateCode + "))",
			new ExpressionBlock(
				factory().metaCode("(").accepts(AcceptedCharacters.None).build(),
				factory().code("Html.Repeat(10, ").asExpression().build(),
				testTemplate(),
				factory().code(")").asExpression().build(),
				factory().metaCode(")").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockHandlesSimpleTemplateInImplicitExpressionParens() {
		parseBlockTest(
			"Html.Repeat(10," + testTemplateCode + ")",
			new ExpressionBlock(
				factory().code("Html.Repeat(10, ")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords).build(),
				testTemplate(),
				factory().code(")")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.NonWhiteSpace).build()
			)
		);
	}

	@Test
	public void parseBlockProducesErrorButCorrectlyParsesNestedTemplateInImplicitExpressionParens() {
		parseBlockTest(
			"Html.Repeat(10," + testNestedTemplateCode + ")",
			new ExpressionBlock(
				factory().code("Html.Repeat(10, ").asImplicitExpression(JavaCodeParser.DefaultKeywords).build(),
				testNestedTemplate(),
				factory().code(")")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.NonWhiteSpace).build()
			),
			getNestedTemplateError(42)
		);
	}

	@Test
	public void parseBlockHandlesSimpleTemplateInStatementWithinCodeBlock() {
		parseBlockTest(
			"foreach(foo in Bar) { Html.ExecuteTemplate(foo," + testTemplateCode + "); }",
			new StatementBlock(
				factory().code("foreach(foo in Bar) { Html.ExecuteTemplate(foo, ").asStatement().build(),
				testTemplate(),
				factory().code("); }")
					.asStatement()
					.accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockHandlesTwoTemplatesInStatementWithinCodeBlock() {
		parseBlockTest(
			"foreach(foo in Bar) { Html.ExecuteTemplate(foo," + testTemplateCode + "," + testTemplateCode + "); }",
			new StatementBlock(
				factory().code("foreach(foo in Bar) { Html.ExecuteTemplate(foo, ").asStatement().build(),
				testTemplate(),
				factory().code(", ").asStatement().build(),
				testTemplate(),
				factory().code("); }")
					.asStatement()
					.accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockProducesErrorButCorrectlyParsesNestedTemplateInStatementWithinCodeBlock() {
		parseBlockTest(
			"foreach(foo in Bar) { Html.ExecuteTemplate(foo," + testNestedTemplateCode + "); }",
			new StatementBlock(
				factory().code("foreach(foo in Bar) { Html.ExecuteTemplate(foo, ").asStatement().build(),
				testNestedTemplate(),
				factory().code("); }")
					.asStatement()
					.accepts(AcceptedCharacters.None).build()
			),
			getNestedTemplateError(74)
		);
	}

	@Test
	public void parseBlockHandlesSimpleTemplateInStatementWithinStatementBlock() {
		parseBlockTest(
			"{ var foo = bar; Html.ExecuteTemplate(foo," + testTemplateCode + "); }",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None).build(),
				factory().code(" var foo = bar; Html.ExecuteTemplate(foo, ").asStatement().build(),
				testTemplate(),
				factory().code("); ").asStatement().build(),
				factory().metaCode("}").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockHandlessTwoTemplatesInStatementWithinStatementBlock() {
		parseBlockTest("{ var foo = bar; Html.ExecuteTemplate(foo," + testTemplateCode + "," + testTemplateCode + "); }",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None).build(),
				factory().code(" var foo = bar; Html.ExecuteTemplate(foo, ").asStatement().build(),
				testTemplate(),
				factory().code(", ").asStatement().build(),
				testTemplate(),
				factory().code("); ").asStatement().build(),
				factory().metaCode("}").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockProducesErrorButCorrectlyParsesNestedTemplateInStatementWithinStatementBlock() {
		parseBlockTest("{ var foo = bar; Html.ExecuteTemplate(foo," + testNestedTemplateCode + "); }",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None).build(),
				factory().code(" var foo = bar; Html.ExecuteTemplate(foo, ").asStatement().build(),
				testNestedTemplate(),
				factory().code("); ").asStatement().build(),
				factory().metaCode("}").accepts(AcceptedCharacters.None).build()
			),
			getNestedTemplateError(69)
		);
	}

	private static RazorError getNestedTemplateError(int charIndex) {
		return new RazorError(
			RazorResources().parseErrorInlineMarkupBlocksCannotBeNested(),
			new SourceLocation(charIndex, 0, charIndex)
		);
	}
}
