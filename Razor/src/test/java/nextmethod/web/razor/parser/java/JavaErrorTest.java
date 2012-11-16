package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.editor.AutoCompleteEditHandler;
import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.JavaLanguageCharacteristics;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

// TODO
public class JavaErrorTest extends JavaHtmlCodeParserTestBase {

	@Test
	public void parseBlockHandlesQuotesAfterTransition() {
		parseBlockTest(
			"@\"",
			new ExpressionBlock(
				factory().codeTransition(),
				factory().emptyJava()
					.asImplicitExpression(getKeywordSet())
					.accepts(AcceptedCharacters.NonWhiteSpace)
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtStartOfCodeBlock("\""),
				1, 0, 1
			)
		);
	}

	@Test
	public void parseBlockCapturesWhitespaceToEndOfLineInInvalidUsingStatementAndTreatsAsFileCode() {
		parseBlockTest(
			"using          " + Environment.NewLine + Environment.NewLine,
			new StatementBlock(
				factory().code("using          " + Environment.NewLine).asStatement()
			)
		);
	}

	@Test
	public void parseBlockMethodOutputsOpenCurlyAsCodeSpanIfEofFoundAfterOpenCurlyBrace() {
		parseBlockTest(
			"{",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().emptyJava()
					.asStatement()
					.with(new AutoCompleteEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()) {{
						this.setAutoCompleteString("}");
					}})
			),
			new RazorError(
				RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
					RazorResources().blockNameCode(),
					"}",
					"{"
				),
				SourceLocation.Zero
			)
		);
	}
}
