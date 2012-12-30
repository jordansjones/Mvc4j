package nextmethod.web.razor.parser.html;

import nextmethod.base.Strings;
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
			Strings.Empty,
			new MarkupBlock(
				factory().emptyHtml()
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
				factory().emptyHtml(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().emptyJava()
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory().emptyHtml()
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
				factory().markup("<div>Foo "),
				new StatementBlock(
					factory().codeTransition(),
					factory().code("if(true) {}").asStatement()
				),
				factory().markup(" Bar</div>")
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
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("Foo"),
					factory().codeTransition(),
					factory().metaCode("section Foo {")
						.autoCompleteWith(null, true),
					new MarkupBlock(
						factory().markup("\r\n    <html></html>\r\n")
					),
					factory().metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
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
				factory().markup("foo "),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().code("bar")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory().markup(" baz")
			)
		);
	}

	@Test
	public void parseDocumentEmitsAtSignAsMarkupIfAtEndOfFile() {
		parseDocumentTest(
			"foo @",
			new MarkupBlock(
				factory().markup("foo "),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().emptyJava()
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory().emptyHtml()
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
				factory().emptyHtml(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().code("bar")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory().emptyHtml()
			)
		);
	}

	@Test
	public void parseDocumentDoesNotSwitchToCodeOnEmailAddressInText() {
		singleSpanDocumentTest("<foo>jordansjones@gmail.com</foo>", BlockType.Markup, SpanKind.Markup);
	}

	@Test
	public void parseDocumentDoesNotSwitchToCodeOnEmailAddressInAttribute() {
		parseDocumentTest(
			"<a href=\"mailto:jordansjones@gmail.com\">Email me</a>",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("href", new LocationTagged<>(" href=\"", 2, 0, 2), new LocationTagged<>("\"", 38, 0, 38)),
					factory().markup(" href=\"").with(SpanCodeGenerator.Null),
					factory().markup("mailto:jordansjones@gmail.com")
						.with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(Strings.Empty, 9, 0, 9), new LocationTagged<>("mailto:jordansjones@gmail.com", 9, 0, 9))),
					factory().markup("\"").with(SpanCodeGenerator.Null)
				),
				factory().markup(">Email me</a>")
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
				factory().markup("<foo>${bar}</foo>")
			)
		);
	}

	@Test
	public void parseDocumentIgnoresTagsInContentsOfScriptTag() {
		parseDocumentTest(
			"<script>foo<bar baz='@boz'></script>",
			new MarkupBlock(
				factory().markup("<script>foo<bar baz='"),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().code("boz")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords, false)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory().markup("'></script>")
			)
		);
	}

	@Test
	public void parseSectionIgnoresTagsInContentsOfScriptTag() {
		parseDocumentTest(
			"@section Foo { <script>foo<bar baz='@boz'></script> }",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("Foo"),
					factory().codeTransition(),
					factory().metaCode("section Foo {"),
					new MarkupBlock(
						factory().markup(" <script>foo<bar baz='"),
						new ExpressionBlock(
							factory().codeTransition(),
							factory().code("boz")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords, false)
								.accepts(AcceptedCharacters.NonWhiteSpace)
						),
						factory().markup("'></script> ")
					),
					factory().metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
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
