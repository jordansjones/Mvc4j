package nextmethod.web.razor.parser.html;

import nextmethod.base.NotImplementedException;
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
				factory().emptyHtmlAndBuild(),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().metaCode("{").acceptsNoneAndBuild(),
					factory().code(Environment.NewLine).asStatementAndBuild(),
					new MarkupBlock(
						factory().markupAndBuild("<")
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
				factory().emptyHtmlAndBuild(),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().metaCode("{").acceptsNoneAndBuild(),
					factory().code(Environment.NewLine).asStatementAndBuild(),
					new MarkupBlock(
						factory().markupAndBuild("<" + Environment.NewLine)
					),
					new MarkupBlock(
						factory().markup("</html>").acceptsNoneAndBuild()
					),
					factory().emptyJava().asStatementAndBuild()
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
				factory().markupAndBuild("<                      \r\n   ")
			),
			true,
			new RazorError(
				RazorResources().parseErrorUnfinishedTag(""),
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
				factory().markupTransition().build(),
				factory().metaMarkup(":", HtmlSymbolType.Colon).build(),
				factory().markup("<li>Foo Bar Baz\r\n")
					.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate(), AcceptedCharacters.None))
					.build()
			)
		);
	}

	@Test
	public void parseBlockStopsParsingSingleLineBlockAtEOFIfNoEOLReached() {
		parseBlockTest(
			"@:foo bar",
			new MarkupBlock(
				factory().markupTransition().build(),
				factory().metaMarkup(":", HtmlSymbolType.Colon).build(),
				factory().markup("foo bar")
					.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()))
					.build()
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
				factory().markupAndBuild("<foo"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("bar", new LocationTagged<>(" bar=\"", 4, 0, 4), new LocationTagged<>("\"", 13, 0, 13)),
					factory().markup(" bar=\"").with(SpanCodeGenerator.Null).build(),
					factory().markup("baz").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>("", 10, 0, 10), new LocationTagged<String>("baz", 10, 0, 10))).build(),
					factory().markup("\"").with(SpanCodeGenerator.Null).build()
				),
				factory().markupAndBuild("><biz><boz"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("zoop", new LocationTagged<>(" zoop=", 24, 0, 24), new LocationTagged<>("", 34, 0, 34)),
					factory().markup(" zoop=").with(SpanCodeGenerator.Null).build(),
					factory().markup("zork").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<String>("", 30, 0, 30), new LocationTagged<String>("zork", 30, 0, 30))).build()
				),
				factory().markup("/></biz></foo>").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void parseBlockAllowsCloseAngleBracketInAttributeValueIfDoubleQuoted() {
		parseBlockTest(
			"<foo><bar baz=\">\" /></foo>",
			new MarkupBlock(
				factory().markupAndBuild("<foo><bar"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("baz", new LocationTagged<>(" baz=\"", 9, 0, 9), new LocationTagged<>("\"", 16, 0, 16)),
					factory().markup(" baz=\"").with(SpanCodeGenerator.Null).build(),
					factory().markup(">").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>("", 15, 0, 15), new LocationTagged<String>(">", 15, 0, 15))).build(),
					factory().markup("\"").with(SpanCodeGenerator.Null).build()
				),
				factory().markup(" /></foo>").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void parseBlockAllowsCloseAngleBracketInAttributeValueIfSingleQuoted() {
		parseBlockTest(
			"<foo><bar baz=\'>\' /></foo>",
			new MarkupBlock(
				factory().markupAndBuild("<foo><bar"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("baz", new LocationTagged<>(" baz='", 9, 0, 9), new LocationTagged<>("'", 16, 0, 16)),
					factory().markup(" baz='").with(SpanCodeGenerator.Null).build(),
					factory().markup(">").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>("", 15, 0, 15), new LocationTagged<String>(">", 15, 0, 15))).build(),
					factory().markup("'").with(SpanCodeGenerator.Null).build()
				),
				factory().markup(" /></foo>").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void parseBlockAllowsSlashInAttributeValueIfDoubleQuoted() {
		parseBlockTest(
			"<foo><bar baz=\"/\"></bar></foo>",
			new MarkupBlock(
				factory().markupAndBuild("<foo><bar"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("baz", new LocationTagged<>(" baz=\"", 9, 0, 9), new LocationTagged<>("\"", 16, 0, 16)),
					factory().markup(" baz=\"").with(SpanCodeGenerator.Null).build(),
					factory().markup("/").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>("", 15, 0, 15), new LocationTagged<String>("/", 15, 0, 15))).build(),
					factory().markup("\"").with(SpanCodeGenerator.Null).build()
				),
				factory().markup("></bar></foo>").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void parseBlockAllowsSlashInAttributeValueIfSingleQuoted() {
		parseBlockTest(
			"<foo><bar baz=\'/\'></bar></foo>",
			new MarkupBlock(
				factory().markupAndBuild("<foo><bar"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("baz", new LocationTagged<>(" baz='", 9, 0, 9), new LocationTagged<>("'", 16, 0, 16)),
					factory().markup(" baz='").with(SpanCodeGenerator.Null).build(),
					factory().markup("/").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>("", 15, 0, 15), new LocationTagged<String>("/", 15, 0, 15))).build(),
					factory().markup("'").with(SpanCodeGenerator.Null).build()
				),
				factory().markup("></bar></foo>").acceptsNoneAndBuild()
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
				factory().markupTransition("<text/>").build()
			)
		);
	}

	@Test
	public void parseBlockAcceptsTextTagAsOuterTagButDoesNotRender() {
		parseBlockTest(
			"<text>Foo Bar <foo> Baz</text> zoop",
			new MarkupBlock(
				factory().markupTransition("<text>").build(),
				factory().markupAndBuild("Foo Bar <foo> Baz"),
				factory().markupTransition("</text>").build(),
				factory().markup(" ").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void parseBlockRendersLiteralTextTagIfDoubled() {
		parseBlockTest(
			"<text><text>Foo Bar <foo> Baz</text></text> zoop",
			new MarkupBlock(
				factory().markupTransition("<text>").build(),
				factory().markupAndBuild("<text>Foo Bar <foo> Baz</text>"),
				factory().markupTransition("</text>").build(),
				factory().markup(" ").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void parseBlockDoesNotConsiderPsuedoTagWithinMarkupBlock() {
		parseBlockTest(
			"<foo><text><bar></bar></foo>",
			new MarkupBlock(
				factory().markup("<foo><text><bar></bar></foo>").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void parseBlockStopsParsingMidEmptyTagIfEOFReached() {
		parseBlockTest(
			"<br/",
			new MarkupBlock(
				factory().markupAndBuild("<br/")
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
				factory().markupAndBuild("<div>Foo "),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().code("if(true) {}").asStatementAndBuild()
				),
				factory().markup(" Bar</div>").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void parseBlockIgnoresTagsInContentsOfScriptTag() {
		parseBlockTest(
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
				factory().markup("'></script>").acceptsNoneAndBuild()
			)
		);
	}

}
