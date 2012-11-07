package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import org.junit.Test;

public class JavaWhitespaceHandlingTest extends JavaHtmlMarkupParserTestBase {

	@Test
	public void statementBlockDoesNotAcceptTrailingNewlineIfNewlinesAreSignificantToAncestor() {
		parseBlockTest(
			"@: @if (true) { }" + Environment.NewLine + "}",
			new MarkupBlock(
				factory().markupTransition().acceptsNoneAndBuild(),
				factory().metaMarkup(":", HtmlSymbolType.Colon).build(),
				factory().markup(" ").build(),
				new StatementBlock(
					factory().codeTransition().acceptsNoneAndBuild(),
					factory().code("if (true) { }").asStatementAndBuild()
				),
				factory().markup(Environment.NewLine).acceptsNoneAndBuild()
			)
		);
	}

}
