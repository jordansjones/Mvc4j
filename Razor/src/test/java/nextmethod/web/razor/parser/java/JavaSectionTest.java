package nextmethod.web.razor.parser.java;

import nextmethod.base.Strings;
import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.generator.SectionCodeGenerator;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SectionBlock;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaSectionTest extends JavaHtmlMarkupParserTestBase {

	@Test
	public void parseSectionBlockCapturesNewlineImmediatelyFollowing() {
		parseDocumentTest(
			"@section" + Environment.NewLine,
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator(Strings.Empty),
					factory().codeTransition(),
					factory().metaCode("section" + Environment.NewLine)
				)
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtSectionNameStart(
					RazorResources().errorComponentEndOfFile()
				),
				10, 1, 0
			)
		);
	}

	@Test
	public void parseSectionBlockCapturesWhitespaceToEndOfLineInSectionStatementMissingOpenBrace() {
		parseDocumentTest(
			"@section Foo         " + Environment.NewLine + "    ",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("Foo"),
					factory().codeTransition(),
					factory().metaCode("section Foo         \r\n")
				),
				factory().markup("    ")
			),
			new RazorError(
				RazorResources().parseErrorMissingOpenBraceAfterSection(),
				12, 0, 12
			)
		);
	}

	@Test
	public void parseSectionBlockCapturesWhitespaceToEndOfLineInSectionStatementMissingName() {
		parseDocumentTest(
			"@section         " + Environment.NewLine + "    ",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator(Strings.Empty),
					factory().codeTransition(),
					factory().metaCode("section         \r\n")
				),
				factory().markup("    ")
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtSectionNameStart(
					RazorResources().errorComponentEndOfFile()
				),
				23, 1, 4
			)
		);
	}

	@Test
	public void parseSectionBlockIgnoresSectionUnlessAllLowerCase() {
		parseDocumentTest(
			"@Section foo",
			new MarkupBlock(
				factory().emptyHtml(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().code("Section")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						
				),
				factory().markup(" foo")
			)
		);
	}

	@Test
	public void parseSectionBlockReportsErrorAndTerminatesSectionBlockIfKeywordNotFollowedByIdentifierStartCharacter() {
		parseDocumentTest(
			"@section 9 { <p>Foo</p> }",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator(Strings.Empty),
					factory().codeTransition(),
					factory().metaCode("section ")
				),
				factory().markup("9 { <p>Foo</p> }")
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtSectionNameStart(
					RazorResources().errorComponentCharacter("9")
				),
				9, 0, 9
			)
		);
	}

	@Test
	public void parseSectionBlockReportsErrorAndTerminatesSectionBlockIfNameNotFollowedByOpenBrace() {
		parseDocumentTest(
			"@section foo-bar { <p>Foo</p> }",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransition(),
					factory().metaCode("section foo")
				),
				factory().markup("-bar { <p>Foo</p> }")
			),
			new RazorError(
				RazorResources().parseErrorMissingOpenBraceAfterSection(),
				12, 0, 12
			)
		);
	}

	@Test
	public void parserOutputsErrorOnNestedSections() {
		parseDocumentTest(
			"@section foo { @section bar { <p>Foo</p> } }",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransition(),
					factory().metaCode("section foo {")
						.autoCompleteWith(null, true)
						,
					new MarkupBlock(
						factory().markup(" "),
						new SectionBlock(
							new SectionCodeGenerator("bar"),
							factory().codeTransition(),
							factory().metaCode("section bar {")
								.autoCompleteWith(null, true)
								,
							new MarkupBlock(
								factory().markup(" <p>Foo</p> ")
							),
							factory().metaCode("}").accepts(AcceptedCharacters.None)
						),
						factory().markup(" ")
					),
					factory().metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
			),
			new RazorError(
				RazorResources().parseErrorSectionsCannotBeNested(
					RazorResources().sectionExample()
				),
				23, 0, 23
			)
		);
	}

	@Test
	public void parseSectionBlockHandlesEOFAfterOpenBrace() {
		parseDocumentTest(
			"@section foo {",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransition(),
					factory().metaCode("section foo {").autoCompleteWith("}", true),
					new MarkupBlock()
				)
			),
			new RazorError(
				RazorResources().parseErrorExpectedX("}"),
				14, 0, 14
			)
		);
	}

	@Test
	public void parseSectionBlockHandlesUnterminatedSection() {
		parseDocumentTest(
			"@section foo { <p>Foo{}</p>",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransition(),
					factory().metaCode("section foo {").autoCompleteWith("}", true),
					new MarkupBlock(
						// Need to provide the markup span as fragments, since the parser will split the {} into separate symbols.
						factory().markup(" <p>Foo", "{", "}", "</p>")
					)
				)
			),
			new RazorError(
				RazorResources().parseErrorExpectedX("}"),
				27, 0, 27
			)
		);
	}

	@Test
	public void parseSectionBlockReportsErrorAndAcceptsWhitespaceToEndOfLineIfSectionNotFollowedByOpenBrace() {
		parseDocumentTest(
			"@section foo      " + Environment.NewLine,
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransition(),
					factory().metaCode("section foo      \r\n")
				)
			),
			new RazorError(
				RazorResources().parseErrorMissingOpenBraceAfterSection(),
				12, 0, 12
			)
		);
	}

	@Test
	public void parseSectionBlockAcceptsOpenBraceMultipleLinesBelowSectionName() {
		parseDocumentTest(
			"@section foo      " + Environment.NewLine
				+ Environment.NewLine
				+ Environment.NewLine
				+ Environment.NewLine
				+ Environment.NewLine
				+ Environment.NewLine
				+ "{" + Environment.NewLine
				+ "<p>Foo</p>" + Environment.NewLine
				+ "}",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransition(),
					factory().metaCode("section foo      \r\n\r\n\r\n\r\n\r\n\r\n{")
						.autoCompleteWith(null, true)
						,
					new MarkupBlock(
						factory().markup("\r\n<p>Foo</p>\r\n")
					),
					factory().metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
			)
		);
	}

	@Test
	public void parseSectionBlockParsesNamedSectionCorrectly() {
		parseDocumentTest(
			"@section foo { <p>Foo</p> }",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransition(),
					factory().metaCode("section foo {")
						.autoCompleteWith(null, true)
						,
					new MarkupBlock(
						factory().markup(" <p>Foo</p> ")
					),
					factory().metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
			)
		);
	}

	@Test
	public void parseSectionBlockDoesNotRequireSpaceBetweenSectionNameAndOpenBrace() {
		parseDocumentTest(
			"@section foo{ <p>Foo</p> }",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransition(),
					factory().metaCode("section foo{")
						.autoCompleteWith(null, true)
						,
					new MarkupBlock(
						factory().markup(" <p>Foo</p> ")
					),
					factory().metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
			)
		);
	}

	@Test
	public void parseSectionBlockBalancesBraces() {
		parseDocumentTest(
			"@section foo { <script>(function foo() { return 1; })();</script> }",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransition(),
					factory().metaCode("section foo {")
						.autoCompleteWith(null, true)
						,
					new MarkupBlock(
						factory().markup(" <script>(function foo() { return 1; })();</script> ")
					),
					factory().metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
			)
		);
	}

	@Test
	public void parseSectionBlockAllowsBracesInCSharpExpression() {
		parseDocumentTest(
			"@section foo { I really want to render a close brace, so here I go: @(\"}\") }",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransition(),
					factory().metaCode("section foo {")
						.autoCompleteWith(null, true)
						,
					new MarkupBlock(
						factory().markup(" I really want to render a close brace, so here I go: "),
						new ExpressionBlock(
							factory().codeTransition(),
							factory().metaCode("(").accepts(AcceptedCharacters.None),
							factory().code("\"}\"").asExpression(),
							factory().metaCode(")").accepts(AcceptedCharacters.None)
						),
						factory().markup(" ")
					),
					factory().metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
			)
		);
	}

	@Test
	public void sectionIsCorrectlyTerminatedWhenCloseBraceImmediatelyFollowsCodeBlock() {
		parseDocumentTest(
			"@section Foo {" + Environment.NewLine
				+ "@if(true) {" + Environment.NewLine
				+ "}" + Environment.NewLine
				+ "}",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("Foo"),
					factory().codeTransition(),
					factory().metaCode("section Foo {")
						.autoCompleteWith(null, true)
						,
					new MarkupBlock(
						factory().markup("\r\n"),
						new StatementBlock(
							factory().codeTransition(),
							factory().code("if(true) {\r\n}\r\n").asStatement()
						)
					),
					factory().metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
			)
		);
	}

}
