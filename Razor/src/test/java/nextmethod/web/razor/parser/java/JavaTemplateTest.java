package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.editor.SingleLineMarkupEditHandler;
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
			getFactory().markupTransition().build(),
			getFactory().markup("<p>Foo #").build(),
			new ExpressionBlock(
				getFactory().codeTransition().build(),
				getFactory().code("item")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.NonWhiteSpace).build()
			),
			getFactory().markup("</p>").accepts(AcceptedCharacters.None).build()
		));
	}

	private static final String testNestedTemplateCode = " @<p>Foo #@Html.Repeat(10, @<p>@item</p>)</p>";

	private TemplateBlock testNestedTemplate() {
		return new TemplateBlock(
			new MarkupBlock(
				getFactory().markupTransition().build(),
				getFactory().markup("<p>Foo #").build(),
				new ExpressionBlock(
					getFactory().codeTransition().build(),
					getFactory().code("Html.Repeat(10, ")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords).build(),
					new TemplateBlock(
						new MarkupBlock(
							getFactory().markupTransition().build(),
							getFactory().markup("<p>").build(),
							new ExpressionBlock(
								getFactory().codeTransition().build(),
								getFactory().code("item")
									.asImplicitExpression(JavaCodeParser.DefaultKeywords)
									.accepts(AcceptedCharacters.NonWhiteSpace).build()
							),
							getFactory().markup("</p>").accepts(AcceptedCharacters.None).build()
						)
					),
					getFactory().code(")")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace).build()
				),
				getFactory().markup("</p>").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockHandlesSingleLineTemplate() {
		parseBlockTest(
			"{ var foo = @: bar\r\n; }",
			new StatementBlock(
				getFactory().metaCode("{").accepts(AcceptedCharacters.None).build(),
				getFactory().code(" var foo = ").asStatement().build(),
				new TemplateBlock(
					new MarkupBlock(
						getFactory().markupTransition().build(),
						getFactory().metaMarkup(":", HtmlSymbolType.Colon).build(),
						getFactory().markup(" bar\r\n")
							.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()))
							.accepts(AcceptedCharacters.None).build()
					)
				),
				getFactory().code("; ").asStatement().build(),
				getFactory().metaCode("}").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockHandlesSingleLineImmediatelyFollowingStatementChar() {
		parseBlockTest(
			"{i@: bar\r\n}",
			new StatementBlock(
				getFactory().metaCode("{").accepts(AcceptedCharacters.None).build(),
				getFactory().code("i").asStatement().build(),
				new TemplateBlock(
					new MarkupBlock(
						getFactory().markupTransition().build(),
						getFactory().metaMarkup(":", HtmlSymbolType.Colon).build(),
						getFactory().markup(" bar\r\n")
							.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()))
							.accepts(AcceptedCharacters.None).build()
					)
				),
				getFactory().emptyJava().asStatement().build(),
				getFactory().metaCode("}").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockHandlesSimpleTemplateInExplicitExpresionParens() {
		parseBlockTest(
			"(Html.Repeat(10," + testTemplateCode + "))",
			new ExpressionBlock(
				getFactory().metaCode("(").accepts(AcceptedCharacters.None).build(),
				getFactory().code("Html.Repeat(10, ").asExpression().build(),
				testTemplate(),
				getFactory().code(")").asExpression().build(),
				getFactory().metaCode(")").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockHandlesSimpleTemplateInImplicitExpressionParens() {
		parseBlockTest(
			"Html.Repeat(10," + testTemplateCode + ")",
			new ExpressionBlock(
				getFactory().code("Html.Repeat(10, ")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords).build(),
				testTemplate(),
				getFactory().code(")")
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
				getFactory().code("Html.Repeat(10, ").asImplicitExpression(JavaCodeParser.DefaultKeywords).build(),
				testNestedTemplate(),
				getFactory().code(")")
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
				getFactory().code("foreach(foo in Bar) { Html.ExecuteTemplate(foo, ").asStatement().build(),
				testTemplate(),
				getFactory().code("); }")
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
				getFactory().code("foreach(foo in Bar) { Html.ExecuteTemplate(foo, ").asStatement().build(),
				testTemplate(),
				getFactory().code(", ").asStatement().build(),
				testTemplate(),
				getFactory().code("); }")
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
				getFactory().code("foreach(foo in Bar) { Html.ExecuteTemplate(foo, ").asStatement().build(),
				testNestedTemplate(),
				getFactory().code("); }")
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
				getFactory().metaCode("{").accepts(AcceptedCharacters.None).build(),
				getFactory().code(" var foo = bar; Html.ExecuteTemplate(foo, ").asStatement().build(),
				testTemplate(),
				getFactory().code("); ").asStatement().build(),
				getFactory().metaCode("}").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockHandlessTwoTemplatesInStatementWithinStatementBlock() {
		parseBlockTest("{ var foo = bar; Html.ExecuteTemplate(foo," + testTemplateCode + "," + testTemplateCode + "); }",
			new StatementBlock(
				getFactory().metaCode("{").accepts(AcceptedCharacters.None).build(),
				getFactory().code(" var foo = bar; Html.ExecuteTemplate(foo, ").asStatement().build(),
				testTemplate(),
				getFactory().code(", ").asStatement().build(),
				testTemplate(),
				getFactory().code("); ").asStatement().build(),
				getFactory().metaCode("}").accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void parseBlockProducesErrorButCorrectlyParsesNestedTemplateInStatementWithinStatementBlock() {
		parseBlockTest("{ var foo = bar; Html.ExecuteTemplate(foo," + testNestedTemplateCode + "); }",
			new StatementBlock(
				getFactory().metaCode("{").accepts(AcceptedCharacters.None).build(),
				getFactory().code(" var foo = bar; Html.ExecuteTemplate(foo, ").asStatement().build(),
				testNestedTemplate(),
				getFactory().code("); ").asStatement().build(),
				getFactory().metaCode("}").accepts(AcceptedCharacters.None).build()
			),
			getNestedTemplateError(69)
		);
	}

	private static RazorError getNestedTemplateError(int charIndex) {
		return new RazorError(
			RazorResources().getString("parseError.inlineMarkup.blocks.cannot.be.nested"),
			new SourceLocation(charIndex, 0, charIndex)
		);
	}
}
