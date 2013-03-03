package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
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
				factory().markupTransition().accepts(AcceptedCharacters.None),
				factory().metaMarkup(":", HtmlSymbolType.Colon),
				factory().markup(" "),
				new StatementBlock(
					factory().codeTransition().accepts(AcceptedCharacters.None),
					factory().code("if (true) { }").asStatement()
				),
				factory().markup(Environment.NewLine).accepts(AcceptedCharacters.None)
			)
		);
	}

}
