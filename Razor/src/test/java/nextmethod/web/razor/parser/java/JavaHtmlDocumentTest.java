package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.CommentBlock;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaHtmlDocumentTest extends JavaHtmlMarkupParserTestBase {

	@Test
	public void unterminatedBlockCommentCausesRazorError() {
		parseDocumentTest(
			"@* Foo Bar",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new CommentBlock(
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition).build(),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
					factory().comment(" Foo Bar", HtmlSymbolType.RazorComment).build()
				)
			),
			new RazorError(
				RazorResources().parseErrorRazorCommentNotTerminated(),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void blockCommentInMarkupDocumentIsHandledCorrectly() {
		parseDocumentTest(
			"<ul>" + Environment.NewLine
				+ "                @* This is a block comment </ul> *@ foo",
			new MarkupBlock(
				factory().markup("<ul>\r\n                ").build(),
				new CommentBlock(
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition).build(),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
					factory().comment(" This is a block comment </ul> ", HtmlSymbolType.RazorComment).build(),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition).build()
				),
				factory().markup(" foo").build()
			)
		);
	}

	@Test
	public void blockCommentInMarkupBlockIsHandledCorrectly() {
		parseBlockTest(
			"<ul>" + Environment.NewLine
				+ "                @* This is a block comment </ul> *@ foo </ul>",
			new MarkupBlock(
				factory().markup("<ul>\r\n                ").build(),
				new CommentBlock(
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition).build(),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
					factory().comment(" This is a block comment </ul> ", HtmlSymbolType.RazorComment).build(),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition).build()
				),
				factory().markup(" foo </ul>").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void blockCommentAtStatementStartInCodeBlockIsHandledCorrectly() {
		parseDocumentTest(
			"@if(Request.IsAuthenticated) {" + Environment.NewLine
				+ "    @* User is logged in! } *@" + Environment.NewLine
				+ "    Write(\"Hello friend!\");" + Environment.NewLine
				+ "}",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().code("if(Request.IsAuthenticated) {\r\n    ").asStatementAndBuild(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().comment(" User is logged in! } ", JavaSymbolType.RazorComment).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).build()
					),
					factory().code("\r\n    Write(\"Hello friend!\");\r\n}").asStatementAndBuild()
				)
			)
		);
	}

	@Test
	public void blockCommentInStatementInCodeBlockIsHandledCorrectly() {
		parseDocumentTest(
			"@if(Request.IsAuthenticated) {" + Environment.NewLine
				+ "    var foo = @* User is logged in! ; *@;" + Environment.NewLine
				+ "    Write(\"Hello friend!\");" + Environment.NewLine
				+ "}",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().code("if(Request.IsAuthenticated) {\r\n    var foo = ").asStatementAndBuild(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().comment(" User is logged in! ; ", JavaSymbolType.RazorComment).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).build()
					),
					factory().code(";\r\n    Write(\"Hello friend!\");\r\n}").asStatementAndBuild()
				)
			)
		);
	}

	@Test
	public void blockCommentInStringIsIgnored() {
		parseDocumentTest(
			"@if(Request.IsAuthenticated) {" + Environment.NewLine
				+ "    var foo = \"@* User is logged in! ; *\";" + Environment.NewLine
				+ "    Write(\"Hello friend!\");" + Environment.NewLine
				+ "}",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().code("if(Request.IsAuthenticated) {" + Environment.NewLine
						+ "    var foo = \"@* User is logged in! ; *\";" + Environment.NewLine
						+ "    Write(\"Hello friend!\");" + Environment.NewLine
						+ "}")
						.asStatementAndBuild()
				)
			)
		);
	}

	@Test
	public void blockCommentInCSharpBlockCommentIsIgnored() {
		parseDocumentTest(
			"@if(Request.IsAuthenticated) {" + Environment.NewLine
				+ "    var foo = /*@* User is logged in! */ *@ */;" + Environment.NewLine
				+ "    Write(\"Hello friend!\");" + Environment.NewLine
				+ "}",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().code("if(Request.IsAuthenticated) {" + Environment.NewLine
						+ "    var foo = /*@* User is logged in! */ *@ */;" + Environment.NewLine
						+ "    Write(\"Hello friend!\");" + Environment.NewLine
						+ "}")
						.asStatementAndBuild()
				)
			)
		);
	}

	@Test
	public void blockCommentInCSharpLineCommentIsIgnored() {
		parseDocumentTest(
			"@if(Request.IsAuthenticated) {" + Environment.NewLine
				+ "    var foo = //@* User is logged in! */ *@;" + Environment.NewLine
				+ "    Write(\"Hello friend!\");" + Environment.NewLine
				+ "}",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().code("if(Request.IsAuthenticated) {" + Environment.NewLine
						+ "    var foo = //@* User is logged in! */ *@;" + Environment.NewLine
						+ "    Write(\"Hello friend!\");" + Environment.NewLine
						+ "}")
						.asStatementAndBuild()
				)
			)
		);
	}

	@Test
	public void blockCommentInImplicitExpressionIsHandledCorrectly() {
		parseDocumentTest(
			"@Html.Foo@*bar*@",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().code("Html.Foo")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						.build()
				),
				factory().emptyHtml().build(),
				new CommentBlock(
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition).build(),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
					factory().comment("bar", HtmlSymbolType.RazorComment).build(),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition).build()
				),
				factory().emptyHtml().build()
			)
		);
	}

	@Test
	public void blockCommentAfterDotOfImplicitExpressionIsHandledCorrectly() {
		parseDocumentTest(
			"@Html.@*bar*@",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().code("Html")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						.build()
				),
				factory().markup(".").build(),
				new CommentBlock(
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition).build(),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
					factory().comment("bar", HtmlSymbolType.RazorComment).build(),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition).build()
				),
				factory().emptyHtml().build()
			)
		);
	}

	@Test
	public void blockCommentInParensOfImplicitExpressionIsHandledCorrectly() {
		parseDocumentTest(
			"@Html.Foo(@*bar*@ 4)",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().code("Html.Foo(")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.Any)
						.build(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().comment("bar", JavaSymbolType.RazorComment).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).build()
					),
					factory().code(" 4)")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						.build()
				),
				factory().emptyHtml().build()
			)
		);
	}

	@Test
	public void blockCommentInBracketsOfImplicitExpressionIsHandledCorrectly() {
		parseDocumentTest(
			"@Html.Foo[@*bar*@ 4]",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().code("Html.Foo[")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.Any)
						.build(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().comment("bar", JavaSymbolType.RazorComment).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).build()
					),
					factory().code(" 4]")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						.build()
				),
				factory().emptyHtml().build()
			)
		);
	}

	@Test
	public void blockCommentInParensOfConditionIsHandledCorrectly() {
		parseDocumentTest(
			"@if(@*bar*@) {}",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new StatementBlock(
					factory().codeTransitionAndBuild(),
					factory().code("if(").asStatementAndBuild(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().comment("bar", JavaSymbolType.RazorComment).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).build()
					),
					factory().code(") {}").asStatementAndBuild()
				)
			)
		);
	}

	@Test
	public void blockCommentInExplicitExpressionIsHandledCorrectly() {
		parseDocumentTest(
			"@(1 + @*bar*@ 1)",
			new MarkupBlock(
				factory().emptyHtml().build(),
				new ExpressionBlock(
					factory().codeTransitionAndBuild(),
					factory().metaCode("(").acceptsNoneAndBuild(),
					factory().code("1 + ").asExpressionAndBuild(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().comment("bar", JavaSymbolType.RazorComment).build(),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).acceptsNoneAndBuild(),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition).build()
					),
					factory().code(" 1").asExpressionAndBuild(),
					factory().metaCode(")").acceptsNoneAndBuild()
				),
				factory().emptyHtml().build()
			)
		);
	}

}
