package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.editor.SingleLineMarkupEditHandler;
import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.JavaLanguageCharacteristics;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.parser.syntaxtree.TemplateBlock;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaToMarkupSwitchTest extends JavaHtmlCodeParserTestBase {

	@Test
	public void singleAngleBracketDoesNotCauseSwitchIfOuterBlockIsTerminated() {
		parseBlockTest(
			"{ List< }",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code(" List< ").asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockGivesSpacesToCodeOnAtTagTemplateTransitionInDesignTimeMode() {
		parseBlockTest(
			"Foo(    @<p>Foo</p>    )",
			new ExpressionBlock(
				factory().code("Foo(    ")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.Any)
					,
				new TemplateBlock(
					new MarkupBlock(
						factory().markupTransition(),
						factory().markup("<p>Foo</p>").accepts(AcceptedCharacters.None)
					)
				),
				factory().code("    )")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.NonWhiteSpace)
					
			),
			true
		);
	}

	@Test
	public void parseBlockGivesSpacesToCodeOnAtColonTemplateTransitionInDesignTimeMode() {
		parseBlockTest(
			"Foo(    " + Environment.NewLine
				+ "@:<p>Foo</p>    " + Environment.NewLine
				+ ")",
			new ExpressionBlock(
				factory().code("Foo(    \r\n")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					,
				new TemplateBlock(
					new MarkupBlock(
						factory().markupTransition(),
						factory().metaMarkup(":", HtmlSymbolType.Colon),
						factory().markup("<p>Foo</p>    \r\n")
							.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate(), AcceptedCharacters.None))
							
					)
				),
				factory().code(")")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.NonWhiteSpace)
					
			),
			true
		);
	}

	@Test
	public void parseBlockGivesSpacesToCodeOnTagTransitionInDesignTimeMode() {
		parseBlockTest(
			"{" + Environment.NewLine
				+ "    <p>Foo</p>    " + Environment.NewLine
				+ "}",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code("\r\n    ").asStatement(),
				new MarkupBlock(
					factory().markup("<p>Foo</p>").accepts(AcceptedCharacters.None)
				),
				factory().code("    \r\n").asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			),
			true
		);
	}

	@Test
	public void parseBlockGivesSpacesToCodeOnInvalidAtTagTransitionInDesignTimeMode() {
		parseBlockTest(
			"{" + Environment.NewLine
				+ "    @<p>Foo</p>    " + Environment.NewLine
				+ "}",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code("\r\n    ").asStatement(),
				new MarkupBlock(
					factory().markupTransition(),
					factory().markup("<p>Foo</p>").accepts(AcceptedCharacters.None)
				),
				factory().code("    \r\n").asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			),
			true,
			new RazorError(
				RazorResources().parseErrorAtInCodeMustBeFollowedByColonParenOrIdentifierStart(),
				7, 1, 4
			)
		);
	}

	@Test
	public void parseBlockGivesSpacesToCodeOnAtColonTransitionInDesignTimeMode() {
		parseBlockTest(
			"{" + Environment.NewLine
				+ "    @:<p>Foo</p>    " + Environment.NewLine
				+ "}",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code("\r\n    ").asStatement(),
				new MarkupBlock(
					factory().markupTransition(),
					factory().metaMarkup(":", HtmlSymbolType.Colon),
					factory().markup("<p>Foo</p>    \r\n")
						.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate(), AcceptedCharacters.None))
						
				),
				factory().emptyJava().asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			),
			true
		);
	}

	@Test
	public void parseBlockShouldSupportSingleLineMarkupContainingStatementBlock() {
		parseBlockTest(
			"Repeat(10," + Environment.NewLine
				+ "    @: @{}" + Environment.NewLine
				+ ")",
			new ExpressionBlock(
				factory().code("Repeat(10,\r\n    ")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					,
				new TemplateBlock(
					new MarkupBlock(
						factory().markupTransition(),
						factory().metaMarkup(":", HtmlSymbolType.Colon),
						factory().markup(" ")
							.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()))
							,
						new StatementBlock(
							factory().codeTransition(),
							factory().metaCode("{").accepts(AcceptedCharacters.None),
							factory().emptyJava().asStatement(),
							factory().metaCode("}").accepts(AcceptedCharacters.None)
						),
						factory().markup("\r\n")
							.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate(), AcceptedCharacters.None))
							
					)
				),
				factory().code(")")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.NonWhiteSpace)
					
			)
		);
	}

	@Test
	public void parseBlockShouldSupportMarkupWithoutPreceedingWhitespace() {
		parseBlockTest(
			"foreach(var file in files){" + Environment.NewLine
				+ Environment.NewLine
				+ Environment.NewLine
				+ "@:Baz" + Environment.NewLine
				+ "<br/>" + Environment.NewLine
				+ "<a>Foo</a>" + Environment.NewLine
				+ "@:Bar" + Environment.NewLine
				+ "}",
			new StatementBlock(
				factory().code("foreach(var file in files){\r\n\r\n\r\n").asStatement(),
				new MarkupBlock(
					factory().markupTransition(),
					factory().metaMarkup(":", HtmlSymbolType.Colon),
					factory().markup("Baz\r\n")
						.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate(), AcceptedCharacters.None))
						
				),
				new MarkupBlock(
					factory().markup("<br/>\r\n")
						.accepts(AcceptedCharacters.None)
				),
				new MarkupBlock(
					factory().markup("<a>Foo</a>\r\n")
						.accepts(AcceptedCharacters.None)
				),
				new MarkupBlock(
					factory().markupTransition(),
					factory().metaMarkup(":", HtmlSymbolType.Colon),
					factory().markup("Bar\r\n")
						.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate(), AcceptedCharacters.None))
						
				),
				factory().code("}")
					.asStatement()
					.accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockGivesAllWhitespaceOnSameLineExcludingPreceedingNewlineButIncludingTrailingNewLineToMarkup() {
		parseBlockTest(
			"if(foo) {" + Environment.NewLine
				+ "    var foo = \"After this statement there are 10 spaces\";          " + Environment.NewLine
				+ "    <p>" + Environment.NewLine
				+ "        Foo" + Environment.NewLine
				+ "        @bar" + Environment.NewLine
				+ "    </p>" + Environment.NewLine
				+ "    @:Hello!" + Environment.NewLine
				+ "    var biz = boz;" + Environment.NewLine
				+ "}",
			new StatementBlock(
				factory().code("if(foo) {\r\n    var foo = \"After this statement there are 10 spaces\";          \r\n").asStatement(),
				new MarkupBlock(
					factory().markup("    <p>\r\n        Foo\r\n"),
					new ExpressionBlock(
						factory().code("        ").asStatement(),
						factory().codeTransition(),
						factory().code("bar")
							.asImplicitExpression(JavaCodeParser.DefaultKeywords)
							.accepts(AcceptedCharacters.NonWhiteSpace)
							
					),
					factory().markup("\r\n    </p>\r\n").accepts(AcceptedCharacters.None)
				),
				new MarkupBlock(
					factory().markup("    "),
					factory().markupTransition(),
					factory().metaMarkup(":", HtmlSymbolType.Colon),
					factory().markup("Hello!\r\n")
						.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate(), AcceptedCharacters.None))
						
				),
				factory().code("    var biz = boz;\r\n}")
					.asStatement()
			)
		);
	}

	@Test
	public void parseBlockAllowsMarkupInIfBodyWithBraces() {
		parseBlockTest(
			"if(foo) { <p>Bar</p> } else if(bar) { <p>Baz</p> } else { <p>Boz</p> }",
			new StatementBlock(
				factory().code("if(foo) {").asStatement(),
				new MarkupBlock(
					factory().markup(" <p>Bar</p> ").accepts(AcceptedCharacters.None)
				),
				factory().code("} else if(bar) {").asStatement(),
				new MarkupBlock(
					factory().markup(" <p>Baz</p> ").accepts(AcceptedCharacters.None)
				),
				factory().code("} else {").asStatement(),
				new MarkupBlock(
					factory().markup(" <p>Boz</p> ").accepts(AcceptedCharacters.None)
				),
				factory().code("}")
					.asStatement()
					.accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockAllowsMarkupInIfBodyWithBracesWithinCodeBlock() {
		parseBlockTest(
			"{ if(foo) { <p>Bar</p> } else if(bar) { <p>Baz</p> } else { <p>Boz</p> } }",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code(" if(foo) {").asStatement(),
				new MarkupBlock(
					factory().markup(" <p>Bar</p> ").accepts(AcceptedCharacters.None)
				),
				factory().code("} else if(bar) {").asStatement(),
				new MarkupBlock(
					factory().markup(" <p>Baz</p> ").accepts(AcceptedCharacters.None)
				),
				factory().code("} else {").asStatement(),
				new MarkupBlock(
					factory().markup(" <p>Boz</p> ").accepts(AcceptedCharacters.None)
				),
				factory().code("} ")
					.asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockSupportsMarkupInCaseAndDefaultBranchesOfSwitch() {
		parseBlockTest(
			"switch(foo) {" + Environment.NewLine
				+ "    case 0:" + Environment.NewLine
				+ "        <p>Foo</p>" + Environment.NewLine
				+ "        break;" + Environment.NewLine
				+ "    case 1:" + Environment.NewLine
				+ "        <p>Bar</p>" + Environment.NewLine
				+ "        return;" + Environment.NewLine
				+ "    case 2:" + Environment.NewLine
				+ "        {" + Environment.NewLine
				+ "            <p>Baz</p>" + Environment.NewLine
				+ "            <p>Boz</p>" + Environment.NewLine
				+ "        }" + Environment.NewLine
				+ "    default:" + Environment.NewLine
				+ "        <p>Biz</p>" + Environment.NewLine
				+ "}",
			new StatementBlock(
				factory().code("switch(foo) {\r\n    case 0:\r\n").asStatement(),
				new MarkupBlock(
					factory().markup("        <p>Foo</p>\r\n").accepts(AcceptedCharacters.None)
				),
				factory().code("        break;\r\n    case 1:\r\n").asStatement(),
				new MarkupBlock(
					factory().markup("        <p>Bar</p>\r\n").accepts(AcceptedCharacters.None)
				),
				factory().code("        return;\r\n    case 2:\r\n        {\r\n").asStatement(),
				new MarkupBlock(
					factory().markup("            <p>Baz</p>\r\n").accepts(AcceptedCharacters.None)
				),
				new MarkupBlock(
					factory().markup("            <p>Boz</p>\r\n").accepts(AcceptedCharacters.None)
				),
				factory().code("        }\r\n    default:\r\n").asStatement(),
				new MarkupBlock(
					factory().markup("        <p>Biz</p>\r\n").accepts(AcceptedCharacters.None)
				),
				factory().code("}")
					.asStatement()
					.accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockSupportsMarkupInCaseAndDefaultBranchesOfSwitchInCodeBlock() {
		parseBlockTest(
			"{ switch(foo) {" + Environment.NewLine
				+ "    case 0:" + Environment.NewLine
				+ "        <p>Foo</p>" + Environment.NewLine
				+ "        break;" + Environment.NewLine
				+ "    case 1:" + Environment.NewLine
				+ "        <p>Bar</p>" + Environment.NewLine
				+ "        return;" + Environment.NewLine
				+ "    case 2:" + Environment.NewLine
				+ "        {" + Environment.NewLine
				+ "            <p>Baz</p>" + Environment.NewLine
				+ "            <p>Boz</p>" + Environment.NewLine
				+ "        }" + Environment.NewLine
				+ "    default:" + Environment.NewLine
				+ "        <p>Biz</p>" + Environment.NewLine
				+ "} }",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code(" switch(foo) {\r\n    case 0:\r\n").asStatement(),
				new MarkupBlock(
					factory().markup("        <p>Foo</p>\r\n").accepts(AcceptedCharacters.None)
				),
				factory().code("        break;\r\n    case 1:\r\n").asStatement(),
				new MarkupBlock(
					factory().markup("        <p>Bar</p>\r\n").accepts(AcceptedCharacters.None)
				),
				factory().code("        return;\r\n    case 2:\r\n        {\r\n").asStatement(),
				new MarkupBlock(
					factory().markup("            <p>Baz</p>\r\n").accepts(AcceptedCharacters.None)
				),
				new MarkupBlock(
					factory().markup("            <p>Boz</p>\r\n").accepts(AcceptedCharacters.None)
				),
				factory().code("        }\r\n    default:\r\n").asStatement(),
				new MarkupBlock(
					factory().markup("        <p>Biz</p>\r\n").accepts(AcceptedCharacters.None)
				),
				factory().code("} ")
					.asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockParsesMarkupStatementOnOpenAngleBracket() {
		parseBlockTest(
			"for(int i = 0; i < 10; i++) { <p>Foo</p> }",
			new StatementBlock(
				factory().code("for(int i = 0; i < 10; i++) {").asStatement(),
				new MarkupBlock(
					factory().markup(" <p>Foo</p> ").accepts(AcceptedCharacters.None)
				),
				factory().code("}")
					.asStatement()
					.accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockParsesMarkupStatementOnOpenAngleBracketInCodeBlock() {
		parseBlockTest(
			"{ for(int i = 0; i < 10; i++) { <p>Foo</p> } }",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code(" for(int i = 0; i < 10; i++) {").asStatement(),
				new MarkupBlock(
					factory().markup(" <p>Foo</p> ").accepts(AcceptedCharacters.None)
				),
				factory().code("} ")
					.asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockParsesMarkupStatementOnSwitchCharacterFollowedByColon() {
		parseBlockTest(
			"if(foo) { @:Bar" + Environment.NewLine
				+ "} zoop",
			new StatementBlock(
				factory().code("if(foo) {").asStatement(),
				new MarkupBlock(
					factory().markup(" "),
					factory().markupTransition(),
					factory().metaMarkup(":", HtmlSymbolType.Colon),
					factory().markup("Bar\r\n")
						.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate(), AcceptedCharacters.None))
						
				),
				factory().code("}")
					.asStatement()
			)
		);
	}

	@Test
	public void parseBlockParsesMarkupStatementOnSwitchCharacterFollowedByColonInCodeBlock() {
		parseBlockTest(
			"{ if(foo) { @:Bar" + Environment.NewLine
				+ "} } zoop",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code(" if(foo) {").asStatement(),
				new MarkupBlock(
					factory().markup(" "),
					factory().markupTransition(),
					factory().metaMarkup(":", HtmlSymbolType.Colon),
					factory().markup("Bar\r\n")
						.accepts(AcceptedCharacters.None)
				),
				factory().code("} ")
					.asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockCorrectlyReturnsFromMarkupBlockWithPseudoTag() {
		parseBlockTest(
			"if (i > 0) { <text>;</text> }",
			new StatementBlock(
				factory().code("if (i > 0) {").asStatement(),
				new MarkupBlock(
					factory().markup(" "),
					factory().markupTransition("<text>").accepts(AcceptedCharacters.None),
					factory().markup(";"),
					factory().markupTransition("</text>").accepts(AcceptedCharacters.None),
					factory().markup(" ").accepts(AcceptedCharacters.None)
				),
				factory().code("}")
					.asStatement()
			)
		);
	}

	@Test
	public void parseBlockCorrectlyReturnsFromMarkupBlockWithPseudoTagInCodeBlock() {
		parseBlockTest(
			"{ if (i > 0) { <text>;</text> } }",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code(" if (i > 0) {").asStatement(),
				new MarkupBlock(
					factory().markup(" "),
					factory().markupTransition("<text>").accepts(AcceptedCharacters.None),
					factory().markup(";"),
					factory().markupTransition("</text>").accepts(AcceptedCharacters.None),
					factory().markup(" ").accepts(AcceptedCharacters.None)
				),
				factory().code("} ")
					.asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockSupportsAllKindsOfImplicitMarkupInCodeBlock() {
		parseBlockTest(
			"{" + Environment.NewLine
				+ "    if(true) {" + Environment.NewLine
				+ "        @:Single Line Markup" + Environment.NewLine
				+ "    }" + Environment.NewLine
				+ "    foreach (var p in Enumerable.Range(1, 10)) {" + Environment.NewLine
				+ "        <text>The number is @p</text>" + Environment.NewLine
				+ "    }" + Environment.NewLine
				+ "    if(!false) {" + Environment.NewLine
				+ "        <p>A real tag!</p>" + Environment.NewLine
				+ "    }" + Environment.NewLine
				+ "}",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code("\r\n    if(true) {\r\n").asStatement(),
				new MarkupBlock(
					factory().markup("        "),
					factory().markupTransition(),
					factory().metaMarkup(":", HtmlSymbolType.Colon),
					factory().markup("Single Line Markup\r\n")
						.with(new SingleLineMarkupEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate(), AcceptedCharacters.None))
						
				),
				factory().code("    }\r\n    foreach (var p in Enumerable.Range(1, 10)) {\r\n").asStatement(),
				new MarkupBlock(
					factory().markup("        "),
					factory().markupTransition("<text>").accepts(AcceptedCharacters.None),
					factory().markup("The number is "),
					new ExpressionBlock(
						factory().codeTransition(),
						factory().code("p")
							.asImplicitExpression(JavaCodeParser.DefaultKeywords)
							.accepts(AcceptedCharacters.NonWhiteSpace)
							
					),
					factory().markupTransition("</text>").accepts(AcceptedCharacters.None),
					factory().markup("\r\n").accepts(AcceptedCharacters.None)
				),
				factory().code("    }\r\n    if(!false) {\r\n").asStatement(),
				new MarkupBlock(
					factory().markup("        <p>A real tag!</p>\r\n").accepts(AcceptedCharacters.None)
				),
				factory().code("    }\r\n").asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

}
