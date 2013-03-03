package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.DirectiveBlock;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.FunctionsBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaSpecialBlockTest extends JavaHtmlCodeParserTestBase {

	@Test
	public void parseInheritsStatementMarksInheritsSpanAsCanGrowIfMissingTrailingSpace() {
		parseBlockTest(
			"inherits",
			new DirectiveBlock(
				factory().metaCode("inherits").accepts(AcceptedCharacters.Any)
			),
			new RazorError(
				RazorResources().parseErrorInheritsKeywordMustBeFollowedByTypeName(),
				8, 0, 8
			)
		);
	}

	@Test
	public void inheritsBlockAcceptsMultipleGenericArguments() {
		parseBlockTest(
			"inherits Foo.Bar<Biz<Qux>, string, int>.Baz",
			new DirectiveBlock(
				factory().metaCode("inherits ").accepts(AcceptedCharacters.None),
				factory().code("Foo.Bar<Biz<Qux>, string, int>.Baz")
					.asBaseType("Foo.Bar<Biz<Qux>, string, int>.Baz")
					
			)
		);
	}

	@Test
	public void inheritsBlockOutputsErrorIfInheritsNotFollowedByTypeButAcceptsEntireLineAsCode() {
		parseBlockTest(
			"inherits                " + Environment.NewLine + "foo",
			new DirectiveBlock(
				factory().metaCode("inherits ").accepts(AcceptedCharacters.None),
				factory().code("               \r\n")
					.asBaseType("")
					
			),
			new RazorError(
				RazorResources().parseErrorInheritsKeywordMustBeFollowedByTypeName(),
				24, 0, 24
			)
		);
	}

	@Test
	public void namespaceImportInsideCodeBlockCausesError() {
		parseBlockTest(
			"{ using Foo.Bar.Baz; var foo = bar; }",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code(" using Foo.Bar.Baz; var foo = bar; ")
					.asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			),
			new RazorError(
				RazorResources().parseErrorNamespaceImportAndTypeAliasCannotExistWithinCodeBlock(),
				2, 0, 2
			)
		);
	}

	@Test
	public void typeAliasInsideCodeBlockIsNotHandledSpecially() {
		parseBlockTest(
			"{ using Foo = Bar.Baz; var foo = bar; }",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code(" using Foo = Bar.Baz; var foo = bar; ")
					.asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			),
			new RazorError(
				RazorResources().parseErrorNamespaceImportAndTypeAliasCannotExistWithinCodeBlock(),
				2, 0, 2
			)
		);
	}

	@Test
	public void plan9FunctionsKeywordInsideCodeBlockIsNotHandledSpecially() {
		parseBlockTest(
			"{ functions Foo; }",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code(" functions Foo; ")
					.asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void nonKeywordStatementInCodeBlockIsHandledCorrectly() {
		parseBlockTest(
			"{" + Environment.NewLine
			+ "    List<dynamic> photos = gallery.Photo.ToList();" + Environment.NewLine
			+ "}",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code("\r\n    List<dynamic> photos = gallery.Photo.ToList();\r\n")
					.asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockBalancesBracesOutsideStringsIfFirstCharacterIsBraceAndReturnsSpanOfTypeCode() {
		final String code = "foo\"b}ar\" if(condition) { String.Format(\"{0}\"); } ";

		parseBlockTest(
			"{" + code + "}",
			new StatementBlock(
				factory().metaCode("{").accepts(AcceptedCharacters.None),
				factory().code(code)
					.asStatement(),
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockBalancesParensOutsideStringsIfFirstCharacterIsParenAndReturnsSpanOfTypeExpression() {
		final String code = "foo\"b)ar\" if(condition) { String.Format(\"{0}\"); } ";

		parseBlockTest(
			"(" + code + ")",
			new ExpressionBlock(
				factory().metaCode("(").accepts(AcceptedCharacters.None),
				factory().code(code)
					.asExpression(),
				factory().metaCode(")").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockBalancesBracesAndOutputsContentAsClassLevelCodeSpanIfFirstIdentifierIsFunctionsKeyword() {
		final String code = " foo(); \"bar}baz\" ";

		parseBlockTest(
			"functions {" + code + "} zoop",
			new FunctionsBlock(
				factory().metaCode("functions {").accepts(AcceptedCharacters.None),
				factory().code(code)
					.asFunctionsBody()
					,
				factory().metaCode("}").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void parseBlockDoesNoErrorRecoveryForFunctionsBlock() {
		parseBlockTest(
			"functions { { { { { } zoop",
			new FunctionsBlock(
				factory().metaCode("functions {").accepts(AcceptedCharacters.None),
				factory().code(" { { { { } zoop")
					.asFunctionsBody()
					
			),
			new RazorError(
				RazorResources().parseErrorExpectedEndOfBlockBeforeEof("functions", "}", "{"),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void parseBlockIgnoresFunctionsUnlessAllLowerCase() {
		parseBlockTest(
			"Functions { foo() }",
			new ExpressionBlock(
				factory().code("Functions")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.NonWhiteSpace)
					
			)
		);
	}

	@Test
	public void parseBlockIgnoresSingleSlashAtStart() {
		parseBlockTest(
			"@/ foo",
			new ExpressionBlock(
				factory().codeTransition(),
				factory().emptyJava()
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.NonWhiteSpace)
					
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedCharacterAtStartOfCodeBlock("/"),
				1, 0, 1
			)
		);
	}

	@Test
	public void parseBlockTerminatesSingleLineCommentAtEndOfLine() {
		parseBlockTest(
			"if(!false) {" + Environment.NewLine
				+ "    // Foo" + Environment.NewLine
				+ "\t<p>A real tag!</p>" + Environment.NewLine
				+ "}",
			new StatementBlock(
				factory().code("if(!false) {\r\n    // Foo\r\n").asStatement(),
				new MarkupBlock(
					factory().markup("\t<p>A real tag!</p>\r\n").accepts(AcceptedCharacters.None)
				),
				factory().code("}").asStatement()
			)
		);
	}

}
