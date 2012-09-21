package nextmethod.web.razor.parser;

import org.junit.Test;

public class ParserContextTest {

	@SuppressWarnings("ConstantConditions")
	@Test(expected = NullPointerException.class)
	public void constructorRequiresNonNullSource() {
		final JavaCodeParser parser = new JavaCodeParser();
		new ParserContext(null, parser, new HtmlMarkupParser(), parser);
	}
}
