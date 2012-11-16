package nextmethod.web.razor.parser.html;

import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.generator.AttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.LiteralAttributeCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class HtmlErrorTest extends JavaHtmlMarkupParserTestBase {

	@Test
	public void parseBlockAllowsInvalidTagNamesAsLongAsParserCanIdentifyEndTag() {
		singleSpanBlockTest("<1-foo+bar>foo</1-foo+bar>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockThrowsErrorIfStartTextTagContainsTextAfterName() {
		parseBlockTest(
			"<text foo bar></text>",
			new MarkupBlock(
				factory().markupTransition("<text").accepts(AcceptedCharacters.Any).build(),
				factory().markupAndBuild(" foo bar>"),
				factory().markupTransition("</text>").build()
			),
			new RazorError(
				RazorResources().parseErrorTextTagCannotContainAttributes(),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void parseBlockThrowsErrorIfEndTextTagContainsTextAfterName() {
		parseBlockTest(
			"<text></text foo bar>",
			new MarkupBlock(
				factory().markupTransition("<text>").build(),
				factory().markupTransition("</text").accepts(AcceptedCharacters.Any).build(),
				factory().markupAndBuild(" ")
			),
			new RazorError(
				RazorResources().parseErrorTextTagCannotContainAttributes(),
				6, 0, 6
			)
		);
	}

	@Test
	public void parseBlockThrowsExceptionIfBlockDoesNotStartWithTag() {
		parseBlockTest(
			"foo bar <baz>",
			new MarkupBlock(
			),
			new RazorError(
				RazorResources().parseErrorMarkupBlockMustStartWithTag(),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void parseBlockStartingWithEndTagProducesRazorErrorThenOutputsMarkupSegmentAndEndsBlock() {
		parseBlockTest(
			"</foo> bar baz",
			new MarkupBlock(
				factory().markup("</foo> ").acceptsNoneAndBuild()
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedEndTag("foo"),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void parseBlockWithUnclosedTopLevelTagThrowsMissingEndTagParserExceptionOnOutermostUnclosedTag() {
		parseBlockTest(
			"<p><foo></bar>",
			new MarkupBlock(
				factory().markup("<p><foo></bar>").acceptsNoneAndBuild()
			),
			new RazorError(
				RazorResources().parseErrorMissingEndTag("p"),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void parseBlockWithUnclosedTagAtEOFThrowsMissingEndTagException() {
		parseBlockTest(
			"<foo>blah blah blah blah blah",
			new MarkupBlock(
				factory().markupAndBuild("<foo>blah blah blah blah blah")
			),
			new RazorError(
				RazorResources().parseErrorMissingEndTag("foo"),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void parseBlockWithUnfinishedTagAtEOFThrowsIncompleteTagException() {
		parseBlockTest(
			"<foo bar=baz",
			new MarkupBlock(
				factory().markupAndBuild("<foo"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("bar", new LocationTagged<>(" bar=", 4, 0, 4), new LocationTagged<>("", 12, 0, 12)),
					factory().markup(" bar=").with(SpanCodeGenerator.Null).build(),
					factory().markup("baz")
						.with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<String>("", 9, 0, 9), new LocationTagged<String>("baz", 9, 0, 9)))
						.build()
				)
			),
			new RazorError(
				RazorResources().parseErrorUnfinishedTag("foo"),
				SourceLocation.Zero
			)
		);
	}

}
