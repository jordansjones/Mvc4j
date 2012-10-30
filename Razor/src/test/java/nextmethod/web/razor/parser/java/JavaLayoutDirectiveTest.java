package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import org.junit.Test;

// TODO
public class JavaLayoutDirectiveTest extends JavaHtmlCodeParserTestBase {

	@Test
	public void layoutKeywordIsCaseSensitive() {
		for(String keyword : new String[] {"Layout", "LAYOUT", "layOut", "LayOut"}) {
			runLayoutKeywordIsCaseSensitive(keyword);
		}
	}

	private void runLayoutKeywordIsCaseSensitive(final String word) {
		parseBlockTest(
			word,
			new ExpressionBlock(
				factory().code(word)
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.NonWhiteSpace)
					.build()
			)
		);
	}
}
