package nextmethod.web.razor.framework;

import nextmethod.web.razor.parser.HtmlMarkupParser;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.ParserBase;

import java.util.Set;

/**
 *
 */
public abstract class JavaHtmlMarkupParserTestBase extends MarkupParserTestBase {

	@Override
	protected Set<String> getKeywordSet() {
		return JavaCodeParser.DefaultKeywords;
	}

	@Override
	protected SpanFactory createSpanFactory() {
		return SpanFactory.createJavaHtml();
	}

	@Override
	public ParserBase createMarkupParser() {
		return new HtmlMarkupParser();
	}

	@Override
	public ParserBase createCodeParser() {
		return new JavaCodeParser();
	}

}