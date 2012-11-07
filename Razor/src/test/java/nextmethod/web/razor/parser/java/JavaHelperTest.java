package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.generator.HelperCodeGenerator;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.HelperBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.LocationTagged;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class JavaHelperTest extends JavaHtmlMarkupParserTestBase {

	@Test
	public void parseHelperCorrectlyParsesHelperWithNoSpaceInBody() {
		parseDocumentTest(
			"@helper Foo(){@Bar()}",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					new HelperCodeGenerator(new LocationTagged<>("Foo(){", 8, 0, 8), true),
					factory().codeTransition().build(),
					factory().metaCode("helper ").acceptsNoneAndBuild(),
					factory().code("Foo(){").hidden().acceptsNoneAndBuild(),
					new StatementBlock(
						factory().emptyJava().asStatementAndBuild(),
						new ExpressionBlock(
							factory().codeTransitionAndBuild(),
							factory().code("Bar()")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords, true)
								.accepts(AcceptedCharacters.NonWhiteSpace)
								.build()
						),
						factory().emptyJava().asStatementAndBuild()
					),
					factory().code("}").hidden().acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
			)
		);
	}

	@Test
	public void parseHelperCorrectlyParsesIncompleteHelperPreceedingCodeBlock() {
		parseDocumentTest(
			"@helper" + Environment.NewLine
			+ "@{}",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper").build()
				),
				factory().markup("\r\n").build(),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().metaCode("{").acceptsNoneAndBuild(),
					factory().emptyJava().asStatementAndBuild(),
					factory().metaCode("}").acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(
					RazorResources().errorComponentNewline()
				),
				7, 0, 7
			)
		);
	}

	@Test
	public void parseHelperRequiresSpaceBeforeSignature() {
		parseDocumentTest(
			"@helper{",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper").build()
				),
				factory().markup("{").build()
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(
					RazorResources().errorComponentCharacter("{")
				),
				7, 0, 7
			)
		);
	}

	@Test
	public void parseHelperOutputsErrorButContinuesIfLParenFoundAfterHelperKeyword() {
		parseDocumentTest(
			"@helper () {",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					new HelperCodeGenerator(new LocationTagged<>("() {", 8, 0, 8), true),
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper ").acceptsNoneAndBuild(),
					factory().code("() {").hidden().acceptsNoneAndBuild(),
					new StatementBlock(
						factory().emptyJava()
							.asStatement()
							.autoCompleteWith("}").build()
					)
				)
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(
					RazorResources().errorComponentCharacter("(")
				),
				8, 0, 8
			),
			new RazorError(
				RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
					"helper", "}", "{"
				),
				1, 0, 1
			)
		);
	}

	@Test
	public void parseHelperStatementOutputsMarkerHelperHeaderSpanOnceKeywordComplete() {
		parseDocumentTest(
			"@helper ",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					new HelperCodeGenerator(new LocationTagged<>("", 8, 0, 8), false),
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper ").acceptsNoneAndBuild(),
					factory().emptyJava().hidden().build()
				)
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(
					RazorResources().errorComponentEndOfFile()
				),
				8, 0, 8
			)
		);
	}

	@Test
	public void parseHelperStatementMarksHelperSpanAsCanGrowIfMissingTrailingSpace() {
		parseDocumentTest(
			"@helper",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper").accepts(AcceptedCharacters.Any).build()
				)
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(
					RazorResources().errorComponentEndOfFile()
				),
				7, 0, 7
			)
		);
	}

	@Test
	public void parseHelperStatementCapturesWhitespaceToEndOfLineIfHelperStatementMissingName() {
		parseDocumentTest(
			"@helper                       " + Environment.NewLine
			+ "    ",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					new HelperCodeGenerator(new LocationTagged<>("                      ", 8, 0, 8), false),
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper ").acceptsNoneAndBuild(),
					factory().code("                      \r\n").hidden().build()
				),
				factory().markup("    ").build()
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(
					RazorResources().errorComponentNewline()
				),
				30, 0, 30
			)
		);
	}

	@Test
	public void parseHelperStatementCapturesWhitespaceToEndOfLineIfHelperStatementMissingOpenParen() {
		parseDocumentTest(
			"@helper Foo    " + Environment.NewLine + "    ",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					new HelperCodeGenerator(new LocationTagged<>("Foo    ", 8, 0, 8), false),
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper ").acceptsNoneAndBuild(),
					factory().code("Foo    \r\n").hidden().build()
				),
				factory().markup("    ").build()
			),
			new RazorError(
				RazorResources().parseErrorMissingCharAfterHelperName("("),
				15, 0, 15
			)
		);
	}

	@Test
	public void parseHelperStatementCapturesAllContentToEndOfFileIfHelperStatementMissingCloseParenInParameterList() {
		parseDocumentTest(
			"@helper Foo(Foo Bar" + Environment.NewLine + "Biz" + Environment.NewLine + "Boz",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					new HelperCodeGenerator(new LocationTagged<>("Foo(Foo Bar\r\nBiz\r\nBoz", 8, 0, 8), false),
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper ").acceptsNoneAndBuild(),
					factory().code("Foo(Foo Bar\r\nBiz\r\nBoz").hidden().build()
				)
			),
			new RazorError(
				RazorResources().parseErrorUnterminatedHelperParameterList(),
				11, 0, 11
			)
		);
	}

	@Test
	public void parseHelperStatementCapturesWhitespaceToEndOfLineIfHelperStatementMissingOpenBraceAfterParameterList() {
		parseDocumentTest(
			"@helper Foo(String foo)    " + Environment.NewLine,
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					new HelperCodeGenerator(new LocationTagged<>("Foo(String foo)    ", 8, 0, 8), false),
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper ").acceptsNoneAndBuild(),
					factory().code("Foo(String foo)    \r\n").hidden().build()
				)
			),
			new RazorError(
				RazorResources().parseErrorMissingCharAfterHelperParameters("{"),
				29, 1, 0
			)
		);
	}

	@Test
	public void parseHelperStatementContinuesParsingHelperUntilEOF() {
		parseDocumentTest(
			"@helper Foo(String foo) {    " + Environment.NewLine + "    <p>Foo</p>",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					new HelperCodeGenerator(new LocationTagged<>("Foo(String foo) {", 8, 0, 8), true),
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper ").acceptsNoneAndBuild(),
					factory().code("Foo(String foo) {").hidden().acceptsNoneAndBuild(),
					new StatementBlock(
						factory().code("    \r\n").asStatement().autoCompleteWith("}").build(),
						new MarkupBlock(
							factory().markup("    <p>Foo</p>").acceptsNoneAndBuild()
						),
						factory().emptyJava().asStatementAndBuild()
					)
				)
			),
			new RazorError(
				RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
					"helper", "}", "{"
				),
				1, 0, 1
			)
		);
	}

	@Test
	public void parseHelperStatementCorrectlyParsesHelperWithEmbeddedCode() {
		parseDocumentTest(
			"@helper Foo(String foo) {    " + Environment.NewLine + "    <p>@foo</p>" + Environment.NewLine + "}",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					new HelperCodeGenerator(new LocationTagged<>("Foo(String foo) {", 8, 0, 8), true),
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper ").acceptsNoneAndBuild(),
					factory().code("Foo(String foo) {").hidden().acceptsNoneAndBuild(),
					new StatementBlock(
						factory().code("    \r\n").asStatementAndBuild(),
						new MarkupBlock(
							factory().markup("    <p>").build(),
							new ExpressionBlock(
								factory().codeTransitionAndBuild(),
								factory().code("foo")
									.asImplicitExpression(JavaCodeParser.DefaultKeywords)
									.accepts(AcceptedCharacters.NonWhiteSpace)
									.build()
							),
							factory().markup("</p>\r\n").acceptsNoneAndBuild()
						),
						factory().emptyJava().asStatementAndBuild()
					),
					factory().code("}").hidden().acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
			)
		);
	}

	@Test
	public void parseHelperStatementCorrectlyParsesHelperWithNewlinesBetweenCloseParenAndOpenBrace() {
		parseDocumentTest(
			"@helper Foo(String foo)" + Environment.NewLine + Environment.NewLine + Environment.NewLine + Environment.NewLine + "{    " + Environment.NewLine + "    <p>@foo</p>" + Environment.NewLine + "}",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					new HelperCodeGenerator(new LocationTagged<>("Foo(String foo)\r\n\r\n\r\n\r\n{", 8, 0, 8), true),
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper ").acceptsNoneAndBuild(),
					factory().code("Foo(String foo)\r\n\r\n\r\n\r\n{").hidden().acceptsNoneAndBuild(),
					new StatementBlock(
						factory().code("    \r\n").asStatementAndBuild(),
						new MarkupBlock(
							factory().markup("    <p>").build(),
							new ExpressionBlock(
								factory().codeTransitionAndBuild(),
								factory().code("foo")
									.asImplicitExpression(JavaCodeParser.DefaultKeywords)
									.accepts(AcceptedCharacters.NonWhiteSpace)
									.build()
							),
							factory().markup("</p>\r\n").acceptsNoneAndBuild()
						),
						factory().emptyJava().asStatementAndBuild()
					),
					factory().code("}").hidden().acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
			)
		);
	}

	@Test
	public void parseHelperStatementGivesWhitespaceAfterOpenBraceToMarkupInDesignMode() {
		parseDocumentTest(
			"@helper Foo(String foo) {    " + Environment.NewLine + "    ",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					new HelperCodeGenerator(new LocationTagged<>("Foo(String foo) {", 8, 0, 8), true),
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper ").acceptsNoneAndBuild(),
					factory().code("Foo(String foo) {").hidden().acceptsNoneAndBuild(),
					new StatementBlock(
						factory().code("    \r\n    ").asStatement().autoCompleteWith("}").build()
					)
				)
			),
			true, // designTimeParser
			new RazorError(
				RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
					"helper", "}", "{"
				),
				1, 0, 1
			)
		);
	}

	@Test
	public void parseHelperAcceptsNestedHelpersButOutputsError() {
		parseDocumentTest(
			"@helper Foo(String foo) {" + Environment.NewLine
			+ "    @helper Bar(String baz) {" + Environment.NewLine
			+ "    }" + Environment.NewLine
			+ "}",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new HelperBlock(
					new HelperCodeGenerator(new LocationTagged<>("Foo(String foo) {", 8, 0, 8), true),
					factory().codeTransitionAndBuild(),
					factory().metaCode("helper ").acceptsNoneAndBuild(),
					factory().code("Foo(String foo) {").hidden().acceptsNoneAndBuild(),
					new StatementBlock(
						factory().code("\r\n    ").asStatementAndBuild(),
						new HelperBlock(
							new HelperCodeGenerator(new LocationTagged<>("Bar(String baz) {", 39, 1, 12), true),
							factory().codeTransitionAndBuild(),
							factory().metaCode("helper ").acceptsNoneAndBuild(),
							factory().code("Bar(String baz) {").hidden().acceptsNoneAndBuild(),
							new StatementBlock(
								factory().code("\r\n    ").asStatementAndBuild()
							),
							factory().code("}").hidden().acceptsNoneAndBuild()
						),
						factory().code("\r\n").asStatementAndBuild()
					),
					factory().code("}").hidden().acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
			),
			true, // designTimeParser
			new RazorError(
				RazorResources().parseErrorHelpersCannotBeNested(),
				38, 1, 11
			)
		);
	}
}
