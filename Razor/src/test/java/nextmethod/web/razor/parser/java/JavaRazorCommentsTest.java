package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.CommentBlock;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaRazorCommentsTest extends JavaHtmlMarkupParserTestBase {

	@Test
	public void unterminatedRazorComment() {
		parseDocumentTest(
			"@*",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new CommentBlock(
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition).acceptsNoneAndBuild(),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
					factory().span(
						SpanKind.Comment,
						new HtmlSymbol(
							factory().getLocationTracker().getCurrentLocation(),
							"",
							HtmlSymbolType.Unknown
						)
					).accepts(AcceptedCharacters.Any).build()
				)
			),
			new RazorError(
				RazorResources().parseErrorRazorCommentNotTerminated(),
				0, 0, 0
			)
		);
	}

	@Test
	public void emptyRazorComment() {
		parseDocumentTest(
			"@**@",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new CommentBlock(
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition).acceptsNoneAndBuild(),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
					factory().span(
						SpanKind.Comment,
						new HtmlSymbol(
							factory().getLocationTracker().getCurrentLocation(),
							"",
							HtmlSymbolType.Unknown
						)
					).accepts(AcceptedCharacters.Any).build(),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition).acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
			)
		);
	}

	@Test
	public void razorCommentInImplicitExpressionMethodCall() {
		parseDocumentTest(
			"@foo(" + Environment.NewLine + "@**@" + Environment.NewLine,
			new MarkupBlock(
				factory().emptyHtml().build(),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().code("foo(" + Environment.NewLine)
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.build(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).acceptsNoneAndBuild(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().span(
							SpanKind.Comment,
							new JavaSymbol(
								factory().getLocationTracker().getCurrentLocation(),
								"",
								JavaSymbolType.Unknown
							)
						).accepts(AcceptedCharacters.Any).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).acceptsNoneAndBuild()
					),
					factory().code(Environment.NewLine).asImplicitExpression(JavaCodeParser.DefaultKeywords).build()
				)
			),
			new RazorError(
				RazorResources().parseErrorExpectedCloseBracketBeforeEof("(", ")"),
				4, 0, 4
			)
		);
	}

	@Test
	public void unterminatedRazorCommentInImplicitExpressionMethodCall() {
		parseDocumentTest(
			"@foo(@*",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().code("foo(").asImplicitExpression(JavaCodeParser.DefaultKeywords).build(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).acceptsNoneAndBuild(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().span(
							SpanKind.Comment,
							new JavaSymbol(
								factory().getLocationTracker().getCurrentLocation(),
								"",
								JavaSymbolType.Unknown
							)
						).accepts(AcceptedCharacters.Any).build()
					)
				)
			),
			new RazorError(RazorResources().parseErrorRazorCommentNotTerminated(), 5, 0, 5),
			new RazorError(RazorResources().parseErrorExpectedCloseBracketBeforeEof("(", ")"), 4, 0, 4)
		);
	}

	@Test
	public void razorCommentInVerbatimBlock() {
		parseDocumentTest(
			"@{" + Environment.NewLine + "    <text" + Environment.NewLine + "    @**@" + Environment.NewLine + "}",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().metaCode("{").acceptsNoneAndBuild(),
					factory().code(Environment.NewLine).asStatementAndBuild(),
					new MarkupBlock(
						factory().markup("    ").build(),
						factory().markupTransition("<text").accepts(AcceptedCharacters.Any).build(),
						factory().markup(Environment.NewLine + "    ").build(),
						new CommentBlock(
							factory().markupTransition(HtmlSymbolType.RazorCommentTransition).acceptsNoneAndBuild(),
							factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
							factory().span(
								SpanKind.Comment,
								new HtmlSymbol(
									factory().getLocationTracker().getCurrentLocation(),
									"",
									HtmlSymbolType.Unknown
								)
							).accepts(AcceptedCharacters.Any).build(),
							factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
							factory().markupTransition(HtmlSymbolType.RazorCommentTransition).acceptsNoneAndBuild()
						),
						factory().markup(Environment.NewLine + "}").build()
					)
				)
			),
			new RazorError(RazorResources().parseErrorTextTagCannotContainAttributes(), 8, 1, 4),
			new RazorError(RazorResources().parseErrorMissingEndTag("text"), 8, 1, 4),
			new RazorError(RazorResources().parseErrorExpectedEndOfBlockBeforeEof(RazorResources().blockNameCode(), "}", "{"), 1, 0, 1)
		);
	}

	@Test
	public void unterminatedRazorCommentInVerbatimBlock() {
		parseDocumentTest(
			"@{@*",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().metaCode("{").acceptsNoneAndBuild(),
					factory().emptyJava().asStatementAndBuild(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).acceptsNoneAndBuild(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().span(
							SpanKind.Comment,
							new JavaSymbol(
								factory().getLocationTracker().getCurrentLocation(),
								"",
								JavaSymbolType.Unknown
							)
						).accepts(AcceptedCharacters.Any).build()
					)
				)
			),
			new RazorError(RazorResources().parseErrorRazorCommentNotTerminated(), 2, 0, 2),
			new RazorError(RazorResources().parseErrorExpectedEndOfBlockBeforeEof(RazorResources().blockNameCode(), "}", "{"), 1, 0, 1)
		);
	}

}
