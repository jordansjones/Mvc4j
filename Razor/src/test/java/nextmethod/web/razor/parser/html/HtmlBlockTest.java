package nextmethod.web.razor.parser.html;

import nextmethod.base.Strings;
import nextmethod.web.razor.editor.SingleLineMarkupEditHandler;
import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.generator.AttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.LiteralAttributeCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.HtmlMarkupParser;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.JavaLanguageCharacteristics;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class HtmlBlockTest extends JavaHtmlMarkupParserTestBase {

	@Test(expected = UnsupportedOperationException.class)
	public void parseBlockMethodThrowsArgNullExceptionOnNullContext() {
		new HtmlMarkupParser().parseBlock();
	}

	@Test
	public void parseBlockHandlesOpenAngleAtEof() {
		parseDocumentTest(
			"@{" + Environment.NewLine + "<",
			new MarkupBlock(
				factory().emptyHtml(),
				new StatementBlock(
					factory().codeTransition(),
					factory().metaCode("{").accepts(AcceptedCharacters.None),
					factory().code(Environment.NewLine).asStatement(),
					new MarkupBlock(
						factory().markup("<")
					)
				)
			),
			new RazorError(
				RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
					RazorResources().blockNameCode(),
					"}",
					"{"
				),
				1, 0, 1
			)
		);
	}

	@Test
	public void parseBlockHandlesOpenAngleWithProperTagFollowingIt() {
		parseDocumentTest(
			"@{" + Environment.NewLine + "<" + Environment.NewLine + "</html>",
			new MarkupBlock(
				factory().emptyHtml(),
				new StatementBlock(
					factory().codeTransition(),
					factory().metaCode("{").accepts(AcceptedCharacters.None),
					factory().code(Environment.NewLine).asStatement(),
					new MarkupBlock(
						factory().markup("<" + Environment.NewLine)
					),
					new MarkupBlock(
						factory().markup("</html>").accepts(AcceptedCharacters.None)
					),
					factory().emptyJava().asStatement()
				)
			),
			true, // DesignTimeParser
			new RazorError(
				RazorResources().parseErrorUnexpectedEndTag("html"),
				7, 2, 0
			),
			new RazorError(
				RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
					"code",
					"}",
					"{"
				),
				1, 0, 1
			)
		);
	}

	@Test
	public void tagWithoutCloseAngleDoesNotTerminateBlock() {
		parseBlockTest(
			"<                      " + Environment.NewLine + "   ",
			new MarkupBlock(
				factory().markup("<                      \r\n   ")
			),
			true,
			new RazorError(
				RazorResources().parseErrorUnfinishedTag(Strings.Empty),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void parseBlockAllowsStartAndEndTagsToDifferInCase() {
		singleSpanBlockTest("<li><p>Foo</P></lI>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockReadsToEndOfLineIfFirstCharacterAfterTransitionIsColon() {
		parseBlockTest(
			"@:<li>Foo Bar Baz" + Environment.NewLine + "bork",
			new MarkupBlock(
				factory().markupTransition(),
				factory().metaMarkup(":", HtmlSymbolType.Colon),
				factory().markup("<li>Foo Bar Baz\r\n")
					.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate(), AcceptedCharacters.None))
			)
		);
	}

	@Test
	public void parseBlockStopsParsingSingleLineBlockAtEOFIfNoEOLReached() {
		parseBlockTest(
			"@:foo bar",
			new MarkupBlock(
				factory().markupTransition(),
				factory().metaMarkup(":", HtmlSymbolType.Colon),
				factory().markup("foo bar")
					.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()))
			)
		);
	}

	@Test
	public void parseBlockStopsAtMatchingCloseTagToStartTag() {
		singleSpanBlockTest("<a><b></b></a><c></c>", "<a><b></b></a>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockParsesUntilMatchingEndTagIfFirstNonWhitespaceCharacterIsStartTag() {
		singleSpanBlockTest("<baz><boz><biz></biz></boz></baz>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockAllowsUnclosedTagsAsLongAsItCanRecoverToAnExpectedEndTag() {
		singleSpanBlockTest("<foo><bar><baz></foo>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockWithSelfClosingTagJustEmitsTag() {
		singleSpanBlockTest("<foo />", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockCanHandleSelfClosingTagsWithinBlock() {
		singleSpanBlockTest("<foo><bar /></foo>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsTagsWithAttributes() {
		parseBlockTest(
			"<foo bar=\"baz\"><biz><boz zoop=zork/></biz></foo>",
			new MarkupBlock(
				factory().markup("<foo"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("bar", new LocationTagged<>(" bar=\"", 4, 0, 4), new LocationTagged<>("\"", 13, 0, 13)),
					factory().markup(" bar=\"").with(SpanCodeGenerator.Null),
					factory().markup("baz").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(Strings.Empty, 10, 0, 10), new LocationTagged<String>("baz", 10, 0, 10))),
					factory().markup("\"").with(SpanCodeGenerator.Null)
				),
				factory().markup("><biz><boz"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("zoop", new LocationTagged<>(" zoop=", 24, 0, 24), new LocationTagged<>(Strings.Empty, 34, 0, 34)),
					factory().markup(" zoop=").with(SpanCodeGenerator.Null),
					factory().markup("zork").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<String>(Strings.Empty, 30, 0, 30), new LocationTagged<String>("zork", 30, 0, 30)))
				),
				factory().markup("/></biz></foo>").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockAllowsCloseAngleBracketInAttributeValueIfDoubleQuoted() {
		parseBlockTest(
			"<foo><bar baz=\">\" /></foo>",
			new MarkupBlock(
				factory().markup("<foo><bar"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("baz", new LocationTagged<>(" baz=\"", 9, 0, 9), new LocationTagged<>("\"", 16, 0, 16)),
					factory().markup(" baz=\"").with(SpanCodeGenerator.Null),
					factory().markup(">").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(Strings.Empty, 15, 0, 15), new LocationTagged<String>(">", 15, 0, 15))),
					factory().markup("\"").with(SpanCodeGenerator.Null)
				),
				factory().markup(" /></foo>").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockAllowsCloseAngleBracketInAttributeValueIfSingleQuoted() {
		parseBlockTest(
			"<foo><bar baz=\'>\' /></foo>",
			new MarkupBlock(
				factory().markup("<foo><bar"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("baz", new LocationTagged<>(" baz='", 9, 0, 9), new LocationTagged<>("'", 16, 0, 16)),
					factory().markup(" baz='").with(SpanCodeGenerator.Null),
					factory().markup(">").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(Strings.Empty, 15, 0, 15), new LocationTagged<String>(">", 15, 0, 15))),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" /></foo>").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockAllowsSlashInAttributeValueIfDoubleQuoted() {
		parseBlockTest(
			"<foo><bar baz=\"/\"></bar></foo>",
			new MarkupBlock(
				factory().markup("<foo><bar"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("baz", new LocationTagged<>(" baz=\"", 9, 0, 9), new LocationTagged<>("\"", 16, 0, 16)),
					factory().markup(" baz=\"").with(SpanCodeGenerator.Null),
					factory().markup("/").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(Strings.Empty, 15, 0, 15), new LocationTagged<String>("/", 15, 0, 15))),
					factory().markup("\"").with(SpanCodeGenerator.Null)
				),
				factory().markup("></bar></foo>").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockAllowsSlashInAttributeValueIfSingleQuoted() {
		parseBlockTest(
			"<foo><bar baz=\'/\'></bar></foo>",
			new MarkupBlock(
				factory().markup("<foo><bar"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("baz", new LocationTagged<>(" baz='", 9, 0, 9), new LocationTagged<>("'", 16, 0, 16)),
					factory().markup(" baz='").with(SpanCodeGenerator.Null),
					factory().markup("/").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(Strings.Empty, 15, 0, 15), new LocationTagged<String>("/", 15, 0, 15))),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup("></bar></foo>").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockTerminatesAtEOF() {
		singleSpanBlockTest("<foo>", "<foo>", BlockType.Markup, SpanKind.Markup,
			new RazorError(
				RazorResources().parseErrorMissingEndTag("foo"),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void parseBlockSupportsCommentAsBlock() {
		singleSpanBlockTest("<!-- foo -->", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsCommentWithinBlock() {
		singleSpanBlockTest("<foo>bar<!-- zoop -->baz</foo>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockProperlyBalancesCommentStartAndEndTags() {
		singleSpanBlockTest("<!--<foo></bar>-->", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockTerminatesAtEOFWhenParsingComment() {
		singleSpanBlockTest("<!--<foo>", "<!--<foo>", BlockType.Markup, SpanKind.Markup);
	}

	@Test
	public void parseBlockOnlyTerminatesCommentOnFullEndSequence() {
		singleSpanBlockTest("<!--<foo>--</bar>-->", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockTerminatesCommentAtFirstOccurrenceOfEndSequence() {
		singleSpanBlockTest("<foo><!--<foo></bar-->--></foo>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockTreatsMalformedTagsAsContent() {
		singleSpanBlockTest(
			"<foo></!-- bar --></foo>",
			"<foo></!-- bar -->",
			BlockType.Markup,
			SpanKind.Markup,
			AcceptedCharacters.SetOfNone,
			new RazorError(
				RazorResources().parseErrorMissingEndTag("foo"),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void parseBlockParsesSGMLDeclarationAsEmptyTag() {
		singleSpanBlockTest("<foo><!DOCTYPE foo bar baz></foo>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockTerminatesSGMLDeclarationAtFirstCloseAngle() {
		singleSpanBlockTest("<foo><!DOCTYPE foo bar> baz></foo>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockParsesXMLProcessingInstructionAsEmptyTag() {
		singleSpanBlockTest("<foo><?xml foo bar baz?></foo>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockTerminatesXMLProcessingInstructionAtQuestionMarkCloseAnglePair() {
		singleSpanBlockTest("<foo><?xml foo bar?> baz</foo>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockDoesNotTerminateXMLProcessingInstructionAtCloseAngleUnlessPreceededByQuestionMark() {
		singleSpanBlockTest("<foo><?xml foo bar> baz?></foo>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsScriptTagsWithLessThanSignsInThem() {
		singleSpanBlockTest("<script>if(foo<bar) { alert(\"baz\");)</script>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsScriptTagsWithSpacedLessThanSignsInThem() {
		singleSpanBlockTest("<script>if(foo < bar) { alert(\"baz\");)</script>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockAcceptsEmptyTextTag() {
		parseBlockTest(
			"<text/>",
			new MarkupBlock(
				factory().markupTransition("<text/>")
			)
		);
	}

	@Test
	public void parseBlockAcceptsTextTagAsOuterTagButDoesNotRender() {
		parseBlockTest(
			"<text>Foo Bar <foo> Baz</text> zoop",
			new MarkupBlock(
				factory().markupTransition("<text>"),
				factory().markup("Foo Bar <foo> Baz"),
				factory().markupTransition("</text>"),
				factory().markup(" ").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockRendersLiteralTextTagIfDoubled() {
		parseBlockTest(
			"<text><text>Foo Bar <foo> Baz</text></text> zoop",
			new MarkupBlock(
				factory().markupTransition("<text>"),
				factory().markup("<text>Foo Bar <foo> Baz</text>"),
				factory().markupTransition("</text>"),
				factory().markup(" ").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockDoesNotConsiderPsuedoTagWithinMarkupBlock() {
		parseBlockTest(
			"<foo><text><bar></bar></foo>",
			new MarkupBlock(
				factory().markup("<foo><text><bar></bar></foo>").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockStopsParsingMidEmptyTagIfEOFReached() {
		parseBlockTest(
			"<br/",
			new MarkupBlock(
				factory().markup("<br/")
			),
			new RazorError(
				RazorResources().parseErrorUnfinishedTag("br"),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void parseBlockCorrectlyHandlesSingleLineOfMarkupWithEmbeddedStatement() {
		parseBlockTest(
			"<div>Foo @if(true) {} Bar</div>",
			new MarkupBlock(
				factory().markup("<div>Foo "),
				new StatementBlock(
					factory().codeTransition(),
					factory().code("if(true) {}").asStatement()
				),
				factory().markup(" Bar</div>").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockIgnoresTagsInContentsOfScriptTag() {
		parseBlockTest(
			"<script>foo<bar baz='@boz'></script>",
			new MarkupBlock(
				factory().markup("<script>foo<bar baz='"),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().code("boz")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords, false)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory().markup("'></script>").accepts(AcceptedCharacters.None)
			)
		);
	}

}
