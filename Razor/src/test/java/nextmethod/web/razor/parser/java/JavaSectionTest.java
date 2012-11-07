package nextmethod.web.razor.parser.java;

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
import static org.junit.Assert.fail;

public class JavaSectionTest extends JavaHtmlMarkupParserTestBase {

	@Test
	public void parseSectionBlockCapturesNewlineImmediatelyFollowing() {
		parseDocumentTest(
			"@section" + Environment.NewLine,
			new MarkupBlock(
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator(""),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section" + Environment.NewLine).build()
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
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator("Foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section Foo         \r\n").build()
				),
				factory().markup("    ").build()
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
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator(""),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section         \r\n").build()
				),
				factory().markup("    ").build()
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
				factory().emptyHtml().build(),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().code("Section")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						.build()
				),
				factory().markup(" foo").build()
			)
		);
	}

	@Test
	public void parseSectionBlockReportsErrorAndTerminatesSectionBlockIfKeywordNotFollowedByIdentifierStartCharacter() {
		parseDocumentTest(
			"@section 9 { <p>Foo</p> }",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator(""),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section ").build()
				),
				factory().markup("9 { <p>Foo</p> }").build()
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
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section foo").build()
				),
				factory().markup("-bar { <p>Foo</p> }").build()
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
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section foo {")
						.autoCompleteWith(null, true)
						.build(),
					new MarkupBlock(
						factory().markup(" ").build(),
						new SectionBlock(
							new SectionCodeGenerator("bar"),
							factory().codeTransitionAndBuild(),
							factory().metaCode("section bar {")
								.autoCompleteWith(null, true)
								.build(),
							new MarkupBlock(
								factory().markup(" <p>Foo</p> ").build()
							),
							factory().metaCode("}").acceptsNoneAndBuild()
						),
						factory().markup(" ").build()
					),
					factory().metaCode("}").acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
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
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section foo {").autoCompleteWith("}", true).build(),
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
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section foo {").autoCompleteWith("}", true).build(),
					new MarkupBlock(
						// Need to provide the markup span as fragments, since the parser will split the {} into separate symbols.
						factory().markup(" <p>Foo", "{", "}", "</p>").build()
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
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section foo      \r\n").build()
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
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section foo      \r\n\r\n\r\n\r\n\r\n\r\n{")
						.autoCompleteWith(null, true)
						.build(),
					new MarkupBlock(
						factory().markup("\r\n<p>Foo</p>\r\n").build()
					),
					factory().metaCode("}").acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
			)
		);
	}

	@Test
	public void parseSectionBlockParsesNamedSectionCorrectly() {
		parseDocumentTest(
			"@section foo { <p>Foo</p> }",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section foo {")
						.autoCompleteWith(null, true)
						.build(),
					new MarkupBlock(
						factory().markup(" <p>Foo</p> ").build()
					),
					factory().metaCode("}").acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
			)
		);
	}

	@Test
	public void parseSectionBlockDoesNotRequireSpaceBetweenSectionNameAndOpenBrace() {
		parseDocumentTest(
			"@section foo{ <p>Foo</p> }",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section foo{")
						.autoCompleteWith(null, true)
						.build(),
					new MarkupBlock(
						factory().markup(" <p>Foo</p> ").build()
					),
					factory().metaCode("}").acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
			)
		);
	}

	@Test
	public void parseSectionBlockBalancesBraces() {
		parseDocumentTest(
			"@section foo { <script>(function foo() { return 1; })();</script> }",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section foo {")
						.autoCompleteWith(null, true)
						.build(),
					new MarkupBlock(
						factory().markup(" <script>(function foo() { return 1; })();</script> ").build()
					),
					factory().metaCode("}").acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
			)
		);
	}

	@Test
	public void parseSectionBlockAllowsBracesInCSharpExpression() {
		parseDocumentTest(
			"@section foo { I really want to render a close brace, so here I go: @(\"}\") }",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator("foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section foo {")
						.autoCompleteWith(null, true)
						.build(),
					new MarkupBlock(
						factory().markup(" I really want to render a close brace, so here I go: ").build(),
						new ExpressionBlock(
							factory().codeTransitionAndBuild(),
							factory().metaCode("(").acceptsNoneAndBuild(),
							factory().code("\"}\"").asExpressionAndBuild(),
							factory().metaCode(")").acceptsNoneAndBuild()
						),
						factory().markup(" ").build()
					),
					factory().metaCode("}").acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
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
				factory().emptyHtml().build(),
				new SectionBlock(
					new SectionCodeGenerator("Foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section Foo {")
						.autoCompleteWith(null, true)
						.build(),
					new MarkupBlock(
						factory().markup("\r\n").build(),
						new StatementBlock(
							factory().codeTransitionAndBuild(),
							factory().code("if(true) {\r\n}\r\n").asStatementAndBuild()
						)
					),
					factory().metaCode("}").acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
			)
		);
	}

}
