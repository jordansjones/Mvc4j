package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import org.junit.Test;

public class JavaWhitespaceHandlingTest extends JavaHtmlMarkupParserTestBase {

	private static final String NewLine = "\r\n";

	@Test
	public void statementBlockDoesNotAcceptTrailingNewlineIfNewlinesAreSignificantToAncestor() {
		parseBlockTest(
			"@: @if (true) { }" + NewLine + "}",
			new MarkupBlock(
				factory().markupTransition().acceptsNoneAndBuild(),
				factory().metaMarkup(":", HtmlSymbolType.Colon).build(),
				factory().markup(" ").build(),
				new StatementBlock(
					factory().codeTransition().acceptsNoneAndBuild(),
					factory().code("if (true) { }").asStatementAndBuild()
				),
				factory().markup(NewLine).acceptsNoneAndBuild()
			)
		);
	}

}
