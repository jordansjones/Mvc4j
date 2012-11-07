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
				factory().metaCode("inherits").accepts(AcceptedCharacters.Any).build()
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
				factory().metaCode("inherits ").acceptsNoneAndBuild(),
				factory().code("Foo.Bar<Biz<Qux>, string, int>.Baz")
					.asBaseType("Foo.Bar<Biz<Qux>, string, int>.Baz")
					.build()
			)
		);
	}

	@Test
	public void inheritsBlockOutputsErrorIfInheritsNotFollowedByTypeButAcceptsEntireLineAsCode() {
		parseBlockTest(
			"inherits                " + Environment.NewLine + "foo",
			new DirectiveBlock(
				factory().metaCode("inherits ").acceptsNoneAndBuild(),
				factory().code("               \r\n")
					.asBaseType("")
					.build()
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
				factory().metaCode("{").acceptsNoneAndBuild(),
				factory().code(" using Foo.Bar.Baz; var foo = bar; ")
					.asStatementAndBuild(),
				factory().metaCode("}").acceptsNoneAndBuild()
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
				factory().metaCode("{").acceptsNoneAndBuild(),
				factory().code(" using Foo = Bar.Baz; var foo = bar; ")
					.asStatementAndBuild(),
				factory().metaCode("}").acceptsNoneAndBuild()
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
				factory().metaCode("{").acceptsNoneAndBuild(),
				factory().code(" functions Foo; ")
					.asStatementAndBuild(),
				factory().metaCode("}").acceptsNoneAndBuild()
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
				factory().metaCode("{").acceptsNoneAndBuild(),
				factory().code("\r\n    List<dynamic> photos = gallery.Photo.ToList();\r\n")
					.asStatementAndBuild(),
				factory().metaCode("}").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void parseBlockBalancesBracesOutsideStringsIfFirstCharacterIsBraceAndReturnsSpanOfTypeCode() {
		final String code = "foo\"b}ar\" if(condition) { String.Format(\"{0}\"); } ";

		parseBlockTest(
			"{" + code + "}",
			new StatementBlock(
				factory().metaCode("{").acceptsNoneAndBuild(),
				factory().code(code)
					.asStatementAndBuild(),
				factory().metaCode("}").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void parseBlockBalancesParensOutsideStringsIfFirstCharacterIsParenAndReturnsSpanOfTypeExpression() {
		final String code = "foo\"b)ar\" if(condition) { String.Format(\"{0}\"); } ";

		parseBlockTest(
			"(" + code + ")",
			new ExpressionBlock(
				factory().metaCode("(").acceptsNoneAndBuild(),
				factory().code(code)
					.asExpressionAndBuild(),
				factory().metaCode(")").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void parseBlockBalancesBracesAndOutputsContentAsClassLevelCodeSpanIfFirstIdentifierIsFunctionsKeyword() {
		final String code = " foo(); \"bar}baz\" ";

		parseBlockTest(
			"functions {" + code + "} zoop",
			new FunctionsBlock(
				factory().metaCode("functions {").acceptsNoneAndBuild(),
				factory().code(code)
					.asFunctionsBody()
					.build(),
				factory().metaCode("}").acceptsNoneAndBuild()
			)
		);
	}

	@Test
	public void parseBlockDoesNoErrorRecoveryForFunctionsBlock() {
		parseBlockTest(
			"functions { { { { { } zoop",
			new FunctionsBlock(
				factory().metaCode("functions {").acceptsNoneAndBuild(),
				factory().code(" { { { { } zoop")
					.asFunctionsBody()
					.build()
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
					.build()
			)
		);
	}

	@Test
	public void parseBlockIgnoresSingleSlashAtStart() {
		parseBlockTest(
			"@/ foo",
			new ExpressionBlock(
				factory().codeTransitionAndBuild(),
				factory().emptyJava()
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.NonWhiteSpace)
					.build()
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
				factory().code("if(!false) {\r\n    // Foo\r\n").asStatementAndBuild(),
				new MarkupBlock(
					factory().markup("\t<p>A real tag!</p>\r\n").acceptsNoneAndBuild()
				),
				factory().code("}").asStatementAndBuild()
			)
		);
	}

}
