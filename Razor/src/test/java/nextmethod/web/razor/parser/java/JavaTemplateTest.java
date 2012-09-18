package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.editor.SingleLineMarkupEditHandler;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.JavaLanguageCharacteristics;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.parser.syntaxtree.TemplateBlock;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.fail;

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
									.accepts(AcceptedCharacters.WhiteSpace).build()
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
	public void testParseBlockHandlesSingleLineTemplate() {
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
	public void testParseBlockHandlesSingleLineImmediatelyFollowingStatementChar() {
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
	public void testParseBlockHandlesSimpleTemplateInExplicitExpresionParens() {
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
	public void testParseBlockHandlesSimpleTemplateInImplicitExpressionParens() {
		parseBlockTest(
			"Html.Repeate(10," + testTemplateCode + ")",
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

}
