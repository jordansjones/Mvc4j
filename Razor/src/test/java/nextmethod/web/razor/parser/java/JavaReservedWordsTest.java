package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.DirectiveBlock;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaReservedWordsTest extends JavaHtmlCodeParserTestBase {

	@Test
	public void reservedWords() {
		for (String word : new String[] { "namespace", "class", "package"}) {
			runReservedWordsTest(word);
		}
	}

	@Test
	public void reservedWordsAreCaseSensitive() {
		for (String word : new String[] { "Namespace", "Class", "NAMESPACE", "CLASS", "nameSpace", "NameSpace", "Package", "PACKage", "PACKAGE"}) {
			runReservedWordsAreCaseSensitiveTest(word);
		}
	}

	private void runReservedWordsTest(final String word) {
		parseBlockTest(
			word,
			new DirectiveBlock(
				factory().metaCode(word).accepts(AcceptedCharacters.None)
			),
			new RazorError(
				RazorResources().parseErrorReservedWord(word),
				SourceLocation.Zero
			)
		);
	}

	private void runReservedWordsAreCaseSensitiveTest(final String word) {
		parseBlockTest(
			word,
			new ExpressionBlock(
				factory().code(word).asImplicitExpression(JavaCodeParser.DefaultKeywords).accepts(AcceptedCharacters.NonWhiteSpace)
			)
		);
	}
}
