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

import static org.junit.Assert.fail;

/**
 *
 */
public class JavaTemplateTest extends JavaHtmlCodeParserTestBase {

	private final String testTemplateCode = " @<p>Foo #@item</p>";

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

		fail("SOmething went wrong");
	}

}
