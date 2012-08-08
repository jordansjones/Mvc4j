package nextmethod.web.razor.parser;

import org.junit.Test;

public class ParserContextTest {

	@Test(expected = NullPointerException.class)
	public void testConstructorRequiresNonNullSource() {
		final JavaCodeParser parser = new JavaCodeParser();
		new ParserContext(null, parser, new HtmlMarkupParser(), parser);
	}
}
