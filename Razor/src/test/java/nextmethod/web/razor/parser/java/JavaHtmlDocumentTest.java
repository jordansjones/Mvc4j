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
				factory().emptyHtml(),
				new CommentBlock(
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
					factory().comment(" Foo Bar", HtmlSymbolType.RazorComment)
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
				factory().markup("<ul>\r\n                "),
				new CommentBlock(
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
					factory().comment(" This is a block comment </ul> ", HtmlSymbolType.RazorComment),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition)
				),
				factory().markup(" foo")
			)
		);
	}

	@Test
	public void blockCommentInMarkupBlockIsHandledCorrectly() {
		parseBlockTest(
			"<ul>" + Environment.NewLine
				+ "                @* This is a block comment </ul> *@ foo </ul>",
			new MarkupBlock(
				factory().markup("<ul>\r\n                "),
				new CommentBlock(
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
					factory().comment(" This is a block comment </ul> ", HtmlSymbolType.RazorComment),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition)
				),
				factory().markup(" foo </ul>").accepts(AcceptedCharacters.None)
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
				factory().emptyHtml(),
				new StatementBlock(
					factory().codeTransition(),
					factory().code("if(Request.IsAuthenticated) {\r\n    ").asStatement(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
						factory().comment(" User is logged in! } ", JavaSymbolType.RazorComment),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition)
					),
					factory().code("\r\n    Write(\"Hello friend!\");\r\n}").asStatement()
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
				factory().emptyHtml(),
				new StatementBlock(
					factory().codeTransition(),
					factory().code("if(Request.IsAuthenticated) {\r\n    var foo = ").asStatement(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
						factory().comment(" User is logged in! ; ", JavaSymbolType.RazorComment),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition)
					),
					factory().code(";\r\n    Write(\"Hello friend!\");\r\n}").asStatement()
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
				factory().emptyHtml(),
				new StatementBlock(
					factory().codeTransition(),
					factory().code("if(Request.IsAuthenticated) {" + Environment.NewLine
						+ "    var foo = \"@* User is logged in! ; *\";" + Environment.NewLine
						+ "    Write(\"Hello friend!\");" + Environment.NewLine
						+ "}")
						.asStatement()
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
				factory().emptyHtml(),
				new StatementBlock(
					factory().codeTransition(),
					factory().code("if(Request.IsAuthenticated) {" + Environment.NewLine
						+ "    var foo = /*@* User is logged in! */ *@ */;" + Environment.NewLine
						+ "    Write(\"Hello friend!\");" + Environment.NewLine
						+ "}")
						.asStatement()
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
				factory().emptyHtml(),
				new StatementBlock(
					factory().codeTransition(),
					factory().code("if(Request.IsAuthenticated) {" + Environment.NewLine
						+ "    var foo = //@* User is logged in! */ *@;" + Environment.NewLine
						+ "    Write(\"Hello friend!\");" + Environment.NewLine
						+ "}")
						.asStatement()
				)
			)
		);
	}

	@Test
	public void blockCommentInImplicitExpressionIsHandledCorrectly() {
		parseDocumentTest(
			"@Html.Foo@*bar*@",
			new MarkupBlock(
				factory().emptyHtml(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().code("Html.Foo")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						
				),
				factory().emptyHtml(),
				new CommentBlock(
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
					factory().comment("bar", HtmlSymbolType.RazorComment),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition)
				),
				factory().emptyHtml()
			)
		);
	}

	@Test
	public void blockCommentAfterDotOfImplicitExpressionIsHandledCorrectly() {
		parseDocumentTest(
			"@Html.@*bar*@",
			new MarkupBlock(
				factory().emptyHtml(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().code("Html")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						
				),
				factory().markup("."),
				new CommentBlock(
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
					factory().comment("bar", HtmlSymbolType.RazorComment),
					factory().metaMarkup("*", HtmlSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
					factory().markupTransition(HtmlSymbolType.RazorCommentTransition)
				),
				factory().emptyHtml()
			)
		);
	}

	@Test
	public void blockCommentInParensOfImplicitExpressionIsHandledCorrectly() {
		parseDocumentTest(
			"@Html.Foo(@*bar*@ 4)",
			new MarkupBlock(
				factory().emptyHtml(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().code("Html.Foo(")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.Any)
						,
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
						factory().comment("bar", JavaSymbolType.RazorComment),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition)
					),
					factory().code(" 4)")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						
				),
				factory().emptyHtml()
			)
		);
	}

	@Test
	public void blockCommentInBracketsOfImplicitExpressionIsHandledCorrectly() {
		parseDocumentTest(
			"@Html.Foo[@*bar*@ 4]",
			new MarkupBlock(
				factory().emptyHtml(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().code("Html.Foo[")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.Any)
						,
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
						factory().comment("bar", JavaSymbolType.RazorComment),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition)
					),
					factory().code(" 4]")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						
				),
				factory().emptyHtml()
			)
		);
	}

	@Test
	public void blockCommentInParensOfConditionIsHandledCorrectly() {
		parseDocumentTest(
			"@if(@*bar*@) {}",
			new MarkupBlock(
				factory().emptyHtml(),
				new StatementBlock(
					factory().codeTransition(),
					factory().code("if(").asStatement(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
						factory().comment("bar", JavaSymbolType.RazorComment),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition)
					),
					factory().code(") {}").asStatement()
				)
			)
		);
	}

	@Test
	public void blockCommentInExplicitExpressionIsHandledCorrectly() {
		parseDocumentTest(
			"@(1 + @*bar*@ 1)",
			new MarkupBlock(
				factory().emptyHtml(),
				new ExpressionBlock(
					factory().codeTransition(),
					factory().metaCode("(").accepts(AcceptedCharacters.None),
					factory().code("1 + ").asExpression(),
					new CommentBlock(
						factory().codeTransition(JavaSymbolType.RazorCommentTransition),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
						factory().comment("bar", JavaSymbolType.RazorComment),
						factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None),
						factory().codeTransition(JavaSymbolType.RazorCommentTransition)
					),
					factory().code(" 1").asExpression(),
					factory().metaCode(")").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
			)
		);
	}

}
