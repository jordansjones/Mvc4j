package nextmethod.razor.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.StringReader;

/**
 *
 */
@RunWith(JUnit4.class)
public class SimpleMarkupTest {

	final static String NL = System.getProperty("line.separator");

	@Test
	public void simpleParserTest() {
		final String markup = new StringBuilder()
			.append("<span>@model.Message</span>").append(NL)
			.append("<a href=\"@Model.url\">@Model.title</a>")
			.append("@@ test").append(NL)
			.append("@@test").append(NL)
			.append("@*").append(NL)
			.append("This is a server side").append(NL)
			.append("multiline comment").append(NL)
			.append("*@")
			.append("<this<>is an error</this>")
			.toString();

		final RazorParser parser = new RazorParser(new StringReader(markup));
		final RazorParserTokenManager tokenManager = parser.token_source;
		Token t = null;
		while (true) {
			t = parser.getNextToken();
			if (t.kind == RazorParserConstants.EOF) {
				break;
			}
			System.out.println(String.format("{TOKEN<%s> Kind=%s, Line=%d, Col=%d, '%s'}", getLexState(tokenManager.curLexState), getTokenName(t.kind), t.beginLine, t.beginColumn, t.toString()));
		}
	}

	private static String getLexState(final int state) {
		return RazorParserTokenManager.lexStateNames[state];
	}

	private static String getTokenName(final int kind) {
		switch (kind) {
			case RazorParserConstants.EOL:
				return "EOL";
			case RazorParserConstants.TAG_NAME:
				return "TAG_NAME";
			case RazorParserConstants.ENDTAG_START:
				return "ENDTAG_START";
			case RazorParserConstants.TAG_START:
				return "TAG_START";
			case RazorParserConstants.TAG_END:
				return "TAG_END";
			case RazorParserConstants.PCDATA:
				return "PCDATA";
			case RazorParserConstants.LST_ERROR:
				return "LST_ERROR";
			case RazorParserConstants.ATTR_NAME:
				return "ATTR_NAME";
			case RazorParserConstants.ATTR_EQ:
				return "ATTR_EQ";
			case RazorParserConstants.ATTR_VAL:
				return "ATTR_VAL";
			case RazorParserConstants.RAZOR_AT:
				return "RAZOR_AT";
			case RazorParserConstants.BLOCK_WORD:
				return "BLOCK_WORD";
//			case RazorParserConstants.RAZOR_ESCAPE_AT:
//				return "RAZOR_ESCAPE_AT";
			default:
				return String.valueOf(kind);
		}
	}

}
