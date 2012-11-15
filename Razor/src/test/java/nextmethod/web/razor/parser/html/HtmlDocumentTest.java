package nextmethod.web.razor.parser.html;

import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.generator.AttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.LiteralAttributeCodeGenerator;
import nextmethod.web.razor.generator.SectionCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.HtmlMarkupParser;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SectionBlock;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.utils.SimpleMarkupBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class HtmlDocumentTest extends JavaHtmlMarkupParserTestBase {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void parseDocumentMethodThrowsArgNullExceptionOnNullContext() {
		expectedException.expect(UnsupportedOperationException.class);
		expectedException.expectMessage(RazorResources().parserContextNotSet());

		new HtmlMarkupParser().parseDocument();
	}

	@Test
	public void parseSectionMethodThrowsArgNullExceptionOnNullContext() {
		expectedException.expect(UnsupportedOperationException.class);
		expectedException.expectMessage(RazorResources().parserContextNotSet());

		new HtmlMarkupParser().parseSection(null, true);
	}

	@Test
	public void parseDocumentOutputsEmptyBlockWithEmptyMarkupSpanIfContentIsEmptyString() {
		parseDocumentTest(
			"",
			new MarkupBlock(
				factory().emptyHtmlAndBuild()
			)
		);
	}

	@Test
	public void parseDocumentOutputsWhitespaceOnlyContentAsSingleWhitespaceMarkupSpan() {
		singleSpanDocumentTest("          ", BlockType.Markup, SpanKind.Markup);
	}

	@Test
	public void parseDocumentAcceptsSwapTokenAtEndOfFileAndOutputsZeroLengthCodeSpan() {
		parseDocumentTest(
			"@",
			new MarkupBlock(
				factory().emptyHtmlAndBuild(),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().emptyJava()
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						.build()
				),
				factory().emptyHtmlAndBuild()
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedEndOfFileAtStartOfCodeBlock(),
				1, 0, 1
			)
		);
	}

	@Test
	public void parseDocumentCorrectlyHandlesSingleLineOfMarkupWithEmbeddedStatement() {
		parseDocumentTest(
			"<div>Foo @if(true) {} Bar</div>",
			new MarkupBlock(
				factory().markupAndBuild("<div>Foo "),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().code("if(true) {}").asStatementAndBuild()
				),
				factory().markupAndBuild(" Bar</div>")
			)
		);
	}

	@Test
	public void parseDocumentWithinSectionDoesNotCreateDocumentLevelSpan() {
		parseDocumentTest(
			"@section Foo {" + Environment.NewLine
				+ "    <html></html>" + Environment.NewLine
				+ "}",
			new MarkupBlock(
				factory().emptyHtmlAndBuild(),
				new SectionBlock(
					new SectionCodeGenerator("Foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section Foo {")
						.autoCompleteWith(null, true)
						.build(),
					new MarkupBlock(
						factory().markupAndBuild("\r\n    <html></html>\r\n")
					),
					factory().metaCode("}").acceptsNoneAndBuild()
				),
				factory().emptyHtmlAndBuild()
			)
		);
	}

	@Test
	public void parseDocumentParsesWholeContentAsOneSpanIfNoSwapCharacterEncountered() {
		singleSpanDocumentTest("foo <bar>baz</bar>", BlockType.Markup, SpanKind.Markup);
	}

	@Test
	public void parseDocumentHandsParsingOverToCodeParserWhenAtSignEncounteredAndEmitsOutput() {
		parseDocumentTest(
			"foo @bar baz",
			new MarkupBlock(
				factory().markupAndBuild("foo "),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().code("bar")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						.build()
				),
				factory().markupAndBuild(" baz")
			)
		);
	}

	@Test
	public void parseDocumentEmitsAtSignAsMarkupIfAtEndOfFile() {
		parseDocumentTest(
			"foo @",
			new MarkupBlock(
				factory().markupAndBuild("foo "),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().emptyJava()
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						.build()
				),
				factory().emptyHtmlAndBuild()
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedEndOfFileAtStartOfCodeBlock(),
				5, 0, 5
			)
		);
	}

	@Test
	public void parseDocumentEmitsCodeBlockIfFirstCharacterIsSwapCharacter() {
		parseDocumentTest(
			"@bar",
			new MarkupBlock(
				factory().emptyHtmlAndBuild(),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().code("bar")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						.build()
				),
				factory().emptyHtmlAndBuild()
			)
		);
	}

	@Test
	public void parseDocumentDoesNotSwitchToCodeOnEmailAddressInText() {
		singleSpanDocumentTest("<foo>anurse@microsoft.com</foo>", BlockType.Markup, SpanKind.Markup);
	}

	@Test
	public void parseDocumentDoesNotSwitchToCodeOnEmailAddressInAttribute() {
		parseDocumentTest(
			"<a href=\"mailto:anurse@microsoft.com\">Email me</a>",
			new MarkupBlock(
				factory().markupAndBuild("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("href", new LocationTagged<>(" href=\"", 2, 0, 2), new LocationTagged<>("\"", 36, 0, 36)),
					factory().markup(" href=\"").with(SpanCodeGenerator.Null).build(),
					factory().markup("mailto:anurse@microsoft.com")
						.with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<String>("", 9, 0, 9), new LocationTagged<String>("mailto:anurse@microsoft.com", 9, 0, 9)))
						.build(),
					factory().markup("\"").with(SpanCodeGenerator.Null).build()
				),
				factory().markupAndBuild(">Email me</a>")
			)
		);
	}

	@Test
	public void parseDocumentDoesNotReturnErrorOnMismatchedTags() {
		singleSpanDocumentTest("Foo <div><p></p></p> Baz", BlockType.Markup, SpanKind.Markup);
	}

	@Test
	public void parseDocumentReturnsOneMarkupSegmentIfNoCodeBlocksEncountered() {
		singleSpanDocumentTest("Foo <p>Baz<!--Foo-->Bar<!-F> Qux", BlockType.Markup, SpanKind.Markup);
	}

	@Test
	public void parseDocumentRendersTextPseudoTagAsMarkup() {
		singleSpanDocumentTest("Foo <text>Foo</text>", BlockType.Markup, SpanKind.Markup);
	}

	@Test
	public void parseDocumentAcceptsEndTagWithNoMatchingStartTag() {
		singleSpanDocumentTest("Foo </div> Bar", BlockType.Markup, SpanKind.Markup);
	}

	@Test
	public void parseDocumentNoLongerSupportsDollarOpenBraceCombination() {
		parseDocumentTest(
			"<foo>${bar}</foo>",
			new MarkupBlock(
				factory().markupAndBuild("<foo>${bar}</foo>")
			)
		);
	}

	@Test
	public void parseDocumentIgnoresTagsInContentsOfScriptTag() {
		parseDocumentTest(
			"<script>foo<bar baz='@boz'></script>",
			new MarkupBlock(
				factory().markupAndBuild("<script>foo<bar baz='"),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().code("boz")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords, false)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						.build()
				),
				factory().markupAndBuild("'></script>")
			)
		);
	}

	@Test
	public void parseSectionIgnoresTagsInContentsOfScriptTag() {
		parseDocumentTest(
			"@section Foo { <script>foo<bar baz='@boz'></script> }",
			new MarkupBlock(
				factory().emptyHtmlAndBuild(),
				new SectionBlock(
					new SectionCodeGenerator("Foo"),
					factory().codeTransitionAndBuild(),
					factory().metaCode("section Foo {").build(),
					new MarkupBlock(
						factory().markupAndBuild(" <script>foo<bar baz='"),
						new ExpressionBlock(
							factory().codeTransitionAndBuild(),
							factory().code("boz")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords, false)
								.accepts(AcceptedCharacters.NonWhiteSpace)
								.build()
						),
						factory().markupAndBuild("'></script> ")
					),
					factory().metaCode("}").acceptsNoneAndBuild()
				),
				factory().emptyHtmlAndBuild()
			)
		);
	}

	@Test
	public void parseBlockCanParse1000NestedElements() {
		final String content = createNestedDocument(1000);
		singleSpanDocumentTest(content, BlockType.Markup, SpanKind.Markup);
	}

	private String createNestedDocument(final int nestedLength) {
		final SimpleMarkupBuilder parent = SimpleMarkupBuilder.create("outer");
		createNested(nestedLength, parent);
		return parent.toString();
	}

	private SimpleMarkupBuilder createNested(int idx, final SimpleMarkupBuilder parent) {
		if (idx == 0) {
			return parent;
		}
		idx -= 1;
		return createNested(
			idx,
			parent
				.newChild(String.format("element-%d", idx))
		);
	}

}
