package nextmethod.web.razor.parser.java;

import com.google.common.base.Strings;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.generator.AttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.DynamicAttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.CommentBlock;
import nextmethod.web.razor.parser.syntaxtree.DirectiveBlock;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;


/**
 *
 */
public class JavaBlockTest extends JavaHtmlCodeParserTestBase {

	@Test(expected = UnsupportedOperationException.class)
	public void parseBlockMethodThrowsArgNullExceptionOnNullContext() {
		new JavaCodeParser().parseBlock();
	}

	@Test
	public void balancingBracketsIgnoresStringLiteralCharactersAndBracketsInsideSingleLineComments() {
		singleSpanBlockTest(
			"if(foo) {" + newLine() + "\t// bar } \"\"'" + newLine() + "\tzoop();" + newLine() + "}",
			BlockType.Statement,
			SpanKind.Code
		);
	}

	@Test
	public void nestedCodeBlockWithAtCausesError() {
		parseBlockTest(
			"if (true) { @if(false) { } }",
			new StatementBlock(
				factory().code("if (true) { ").asStatement().build(),
				new StatementBlock(
					factory().codeTransition().build(),
					factory().code("if(false) { }").asStatement().build()
				),
				factory().code(" }").asStatement().build()
			),
			new RazorError(
				RazorResources().parseErrorUnexpectedKeywordAfterAt("if"),
				13, 0, 13
			)
		);
	}

	@SuppressWarnings("StringBufferReplaceableByString")
	@Test
	public void balancingBracketsIgnoresStringLiteralCharactersAndBracketsInsideBlockComments() {
		singleSpanBlockTest(
			new StringBuilder("if(foo) {").append(newLine())
				.append("\t").append("/* bar } ").append('"').append('"').append(" */ ' baz } '").append(newLine())
				.append("\t").append("zoop();").append(newLine())
				.append("}")
				.toString(),
			BlockType.Statement,
			SpanKind.Code
		);
	}

	@Test
	public void parseBlockSkipsParenthesisedExpressionAndThenBalancesBracesIfFirstIdentifierIsForKeyword() {
		singleSpanBlockTest("for(int i = 0; i < 10; new Foo { Bar = \"baz\" }) { Debug.WriteLine(@\"foo } bar\"); }", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSkipsParenthesisedExpressionAndThenBalancesBracesIfFirstIdentifierIsForeachKeyword() {
		singleSpanBlockTest("foreach(int i = 0; i < 10; new Foo { Bar = \"baz\" }) { Debug.WriteLine(@\"foo } bar\"); }", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSkipsParenthesisedExpressionAndThenBalancesBracesIfFirstIdentifierIsWhileKeyword() {
		singleSpanBlockTest("while(int i = 0; i < 10; new Foo { Bar = \"baz\" }) { Debug.WriteLine(@\"foo } bar\"); }", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSkipsParenthesisedExpressionAndThenBalancesBracesIfFirstIdentifierIsUsingKeywordFollowedByParen() {
		singleSpanBlockTest("using(int i = 0; i < 10; new Foo { Bar = \"baz\" }) { Debug.WriteLine(@\"foo } bar\"); }", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsUsingsNestedWithinOtherBlocks() {
		singleSpanBlockTest("if(foo) { using(int i = 0; i < 10; new Foo { Bar = \"baz\" }) { Debug.WriteLine(@\"foo } bar\"); } }", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockSkipsParenthesisedExpressionAndThenBalancesBracesIfFirstIdentifierIsIfKeywordWithNoElseBranches() {
		singleSpanBlockTest("if(int i = 0; i < 10; new Foo { Bar = \"baz\" }) { Debug.WriteLine(@\"foo } bar\"); }", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockAllowsEmptyBlockStatement() {
		singleSpanBlockTest("if(false) { }", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockTerminatesParenBalancingAtEOF() {
		implicitExpressionTest(
			"Html.En(code()",
			"Html.En(code()",
			AcceptedCharacters.Any,
			new RazorError(
				RazorResources().parseErrorExpectedCloseBracketBeforeEof("(", ")"),
				8, 0, 8
			)
		);
	}

	@Test
	public void parseBlockSupportsBlockCommentBetweenIfAndElseClause() {
		singleSpanBlockTest("if(foo) { bar(); } /* Foo */ /* Bar */ else { baz(); }", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsRazorCommentBetweenIfAndElseClause() {
		runRazorCommentBetweenClausesTest("if(foo) { bar(); } ", " else { baz(); }", AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsBlockCommentBetweenElseIfAndElseClause() {
		singleSpanBlockTest("if(foo) { bar(); } else if(bar) { baz(); } /* Foo */ /* Bar */ else { biz(); }", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsRazorCommentBetweenElseIfAndElseClause() {
		runRazorCommentBetweenClausesTest("if(foo) { bar(); } else if(bar) { baz(); } ", " else { baz(); }", AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsBlockCommentBetweenIfAndElseIfClause() {
		singleSpanBlockTest("if(foo) { bar(); } /* Foo */ /* Bar */ else if(bar) { baz(); }", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockSupportsRazorCommentBetweenIfAndElseIfClause() {
		runRazorCommentBetweenClausesTest("if(foo) { bar(); } ", " else if(bar) { baz(); }");
	}

	@SuppressWarnings("StringBufferReplaceableByString")
	@Test
	public void parseBlockSupportsLineCommentBetweenIfAndElseClause() {
		singleSpanBlockTest(
			new StringBuilder("if(foo) { bar(); }").append(newLine())
				.append("// Foo").append(newLine())
				.append("// Bar").append(newLine())
				.append("else { baz(); }")
				.toString(),
			BlockType.Statement,
			SpanKind.Code,
			AcceptedCharacters.None
		);
	}

	@SuppressWarnings("StringBufferReplaceableByString")
	@Test
	public void parseBlockSupportsLineCommentBetweenElseIfAndElseClause() {
		singleSpanBlockTest(
			new StringBuilder("if(foo) { bar(); } else if(bar) { baz(); }").append(newLine())
				.append("// Foo").append(newLine())
				.append("// Bar").append(newLine())
				.append("else { biz(); }")
				.toString(),
			BlockType.Statement,
			SpanKind.Code,
			AcceptedCharacters.None
		);
	}

	@SuppressWarnings("StringBufferReplaceableByString")
	@Test
	public void parseBlockSupportsLineCommentBetweenIfAndElseIfClause() {
		singleSpanBlockTest(
			new StringBuilder("if(foo) { bar(); }").append(newLine())
				.append("// Foo").append(newLine())
				.append("// Bar").append(newLine())
				.append("else if(bar) { baz(); }")
				.toString(),
			BlockType.Statement,
			SpanKind.Code
		);
	}

	@Test
	public void parseBlockParsesElseIfBranchesOfIfStatement() {
		final String ifStatement = "if(int i = 0; i < 10; new Foo { Bar = \"baz\" }) {" + newLine() + "\tDebug.WriteLine(@\"foo } bar\");" + newLine() + "}";
		final String elseIfBranch = " else if(int i = 0; i < 10; new Foo { Bar = \"baz\" }) {" + newLine() + "\tDebug.WriteLine(@\"bar } baz\");" + newLine() + "}";

		final String document = ifStatement + elseIfBranch;

		singleSpanBlockTest(document, BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockParsesMultipleElseIfBranchesOfIfStatement() {
		final String ifStatement = "if(int i = 0; i < 10; new Foo { Bar = \"baz\" }) {" + newLine() + "\tDebug.WriteLine(@\"foo } bar\");" + newLine() + "}";
		final String elseIfBranch = " else if(int i = 0; i < 10; new Foo { Bar = \"baz\" }) {" + newLine() + "\tDebug.WriteLine(@\"bar } baz\");" + newLine() + "}";

		final String document = ifStatement + elseIfBranch + elseIfBranch + elseIfBranch + elseIfBranch;

		singleSpanBlockTest(document, BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockParsesMultipleElseIfBranchesOfIfStatementFollowedByOneElseBranch() {
		final String ifStatement = "if(int i = 0; i < 10; new Foo { Bar = \"baz\" }) {" + newLine() + "\tDebug.WriteLine(@\"foo } bar\");" + newLine() + "}";
		final String elseIfBranch = " else if(int i = 0; i < 10; new Foo { Bar = \"baz\" }) {" + newLine() + "\tDebug.WriteLine(@\"bar } baz\");" + newLine() + "}";
		final String elseBranch = " else { Debug.WriteLine(@\"bar } baz\"); }";

		final String document = ifStatement + elseIfBranch + elseIfBranch + elseBranch;

		singleSpanBlockTest(document, BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockStopsParsingCodeAfterElseBranch() {
		final String ifStatement = "if(int i = 0; i < 10; new Foo { Bar = \"baz\" }) {" + newLine() + "\tDebug.WriteLine(@\"foo } bar\");" + newLine() + "}";
		final String elseIfBranch = " else if(int i = 0; i < 10; new Foo { Bar = \"baz\" }) {" + newLine() + "\tDebug.WriteLine(@\"bar } baz\");" + newLine() + "}";
		final String elseBranch = " else { Debug.WriteLine(@\"bar } baz\"); }";

		final String document = ifStatement + elseIfBranch + elseBranch + elseIfBranch;
		final String expected = ifStatement + elseIfBranch + elseBranch;

		parseBlockTest(document, new StatementBlock(factory().code(expected).asStatement().accepts(AcceptedCharacters.None).build()));
	}

	@Test
	public void parseBlockStopsParsingIfIfStatementNotFollowedByElse() {
		final String document = "if(int i = 0; i < 10; new Foo { Bar = \"baz\" }) {" + newLine() + "\tDebug.WriteLine(@\"foo } bar\");" + newLine() + "}";

		singleSpanBlockTest(document, BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockAcceptsElseIfWithNoCondition() {
		// We don't want to be a full Java parser - If the else if is missing it's condition, the Java compiler can handle that, we have all the info we need to keep parsing
		final String ifBranch = "if(int i = 0; i < 10; new Foo { Bar = \"baz\" }) {" + newLine() + "\tDebug.WriteLine(@\"foo } bar\");" + newLine() + "}";
		final String elseIfBranch = " else if { foo(); }";

		final String document = ifBranch + elseIfBranch;

		singleSpanBlockTest(document, BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockCorrectlyParsesDoWhileBlock() {
		singleSpanBlockTest("do { var foo = bar; } while(foo != bar);", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockCorrectlyParsesDoWhileBlockMissingSemicolon() {
		singleSpanBlockTest("do { var foo = bar; } while(foo != bar)", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockCorrectlyParsesDoWhileBlockMissingWhileCondition() {
		singleSpanBlockTest("do { var foo = bar; } while", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockCorrectlyParsesDoWhileBlockMissingWhileConditionWithSemicolon() {
		singleSpanBlockTest("do { var foo = bar; } while;", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockCorrectlyParsesDoWhileBlockMissingWhileClauseEntirely() {
		singleSpanBlockTest("do { var foo = bar; } narf;", "do { var foo = bar; }", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockSupportsBlockCommentBetweenDoAndWhileClause() {
		singleSpanBlockTest("do { var foo = bar; } /* Foo */ /* Bar */ while(true);", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsLineCommentBetweenDoAndWhileClause() {
		singleSpanBlockTest(
			"do { var foo = bar; }" + newLine() + "// Foo" + newLine() + "// Bar" + newLine() + "while(true);",
			BlockType.Statement,
			SpanKind.Code,
			AcceptedCharacters.None
		);
	}

	@Test
	public void parseBlockSupportsRazorCommentBetweenDoAndWhileClause() {
		runRazorCommentBetweenClausesTest("do { var foo = bar; } ", " while(true);", AcceptedCharacters.None);
	}

	@Test
	public void parseBlockCorrectlyParsesMarkupInDoWhileBlock() {
		parseBlockTest("@do { var foo = bar; <p>Foo</p> foo++; } while (foo<bar>);",
			new StatementBlock(
				factory().codeTransition().build(),
				factory().code("do { var foo = bar;").asStatement().build(),
				new MarkupBlock(
					factory().markup(" <p>Foo</p> ").accepts(AcceptedCharacters.None).build()
				),
				factory().code("foo++; } while (foo<bar>);").asStatement().accepts(AcceptedCharacters.None).build()
			));
	}

	@SuppressWarnings("StringBufferReplaceableByString")
	@Test
	public void parseBlockSkipsParenthesisedExpressionAndThenBalancesBracesIfFirstIdentifierIsSwitchKeyword() {
		singleSpanBlockTest(
			new StringBuilder()
				.append("switch(foo) {").append(newLine())
				.append("\tcase 0:").append(newLine())
				.append("\t\tbreak;").append(newLine())
				.append("\tcase 1:").append(newLine())
				.append("\t\t{").append(newLine())
				.append("\t\t\tbreak;").append(newLine())
				.append("\t\t}").append(newLine())
				.append("\tcase 2:").append(newLine())
				.append("\t\treturn;").append(newLine())
				.append("\tdefault:").append(newLine())
				.append("\t\treturn;").append(newLine())
				.append("}")
				.toString(),
			BlockType.Statement,
			SpanKind.Code,
			AcceptedCharacters.None
		);
	}

	@Test
	public void parseBlockSkipsParenthesisedExpressionAndThenBalancesBracesIfFirstIdentifierIsLockKeyword() {
		singleSpanBlockTest("lock(foo) { Debug.WriteLine(@\"foo } bar\"); }", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockHasErrorsIfPackageImportMissingSemicolon() {
		packageImportTest("using Foo.Bar.Baz", " Foo.Bar.Baz", new SourceLocation(17, 0, 17), AcceptedCharacters.NonWhiteSpace, AcceptedCharacters.WhiteSpace);
	}

	@Test
	public void parseBlockHasErrorsIfPackageAliasMissingSemicolon() {
		packageImportTest("using Foo.Bar.Baz = FooBarBaz", " Foo.Bar.Baz = FooBarBaz", new SourceLocation(29, 0, 29), AcceptedCharacters.NonWhiteSpace, AcceptedCharacters.WhiteSpace);
	}

	@Test
	public void parseBlockParsesPackageImportWithSemicolonForUsingKeywordIfIsInValidFormat() {
		packageImportTest("using Foo.Bar.Baz;", " Foo.Bar.Baz", AcceptedCharacters.NonWhiteSpace, AcceptedCharacters.WhiteSpace);
	}

	@Test
	public void parseBlockDoesntCaptureWhitespaceAfterUsing() {
		parseBlockTest(
			"using Foo   ",
			new DirectiveBlock(
				factory().code("using Foo")
					.asPackageImport(" Foo", JavaCodeParser.UsingKeywordLength)
					.accepts(AcceptedCharacters.NonWhiteSpace, AcceptedCharacters.WhiteSpace)
					.build()
			)
		);
	}

	@Test
	public void parseBlockParsesPackageAliasWithSemicolonForUsingKeywordIfIsInValidFormat() {
		packageImportTest("using FooBarBaz = FooBarBaz;", " FooBarBaz = FooBarBaz", AcceptedCharacters.NonWhiteSpace, AcceptedCharacters.WhiteSpace);
	}

	@Test
	public void parseBlockTerminatesUsingKeywordAtEOFAndOutputsFileCodeBlock() {
		singleSpanBlockTest("using                    ", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockTerminatesSingleLineCommentAtEndOfFile() {
		final String document = "foreach(var f in Foo) { // foo bar baz";
		singleSpanBlockTest(
			document,
			document,
			BlockType.Statement,
			SpanKind.Code,
			new RazorError(
				RazorResources().parseErrorExpectedEndOfBlockBeforeEof("foreach", "}", "{"),
				SourceLocation.Zero)
		);
	}

	@Test
	public void parseBlockTerminatesBlockCommentAtEndOfFile() {
		final String document = "foreach(var f in Foo) { /* foo bar baz";
		singleSpanBlockTest(
			document,
			document,
			BlockType.Statement,
			SpanKind.Code,
			new RazorError(RazorResources().parseErrorBlockCommentNotTerminated(), 24, 0, 24),
			new RazorError(
				RazorResources().parseErrorExpectedEndOfBlockBeforeEof("foreach", "}", "{"),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void parseBlockTerminatesSingleSlashAtEndOfFile() {
		final String document = "foreach(var f in Foo) { / foo bar baz";
		singleSpanBlockTest(
			document,
			document,
			BlockType.Statement,
			SpanKind.Code,
			new RazorError(
				RazorResources().parseErrorExpectedEndOfBlockBeforeEof("foreach", "}", "{"),
				SourceLocation.Zero
			)
		);
	}

	@Test
	public void parseBlockSupportsBlockCommentBetweenTryAndFinallyClause() {
		singleSpanBlockTest("try { bar(); } /* Foo */ /* Bar */ finally { baz(); }", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsRazorCommentBetweenTryAndFinallyClause() {
		runRazorCommentBetweenClausesTest("try { bar(); } ", " finally { biz(); }", AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsBlockCommentBetweenCatchAndFinallyClause() {
		singleSpanBlockTest("try { bar(); } catch(bar) { baz(); } /* Foo */ /* Bar */ finally { biz(); }", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsRazorCommentBetweenCatchAndFinallyClause() {
		runRazorCommentBetweenClausesTest("try { bar(); } catch(bar) { baz(); } ", " finally { biz(); }", AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsBlockCommentBetweenTryAndCatchClause() {
		singleSpanBlockTest("try { bar(); } /* Foo */ /* Bar */ catch(bar) { baz(); }", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockSupportsRazorCommentBetweenTryAndCatchClause() {
		runRazorCommentBetweenClausesTest("try { bar(); }", " catch(bar) { baz(); }");
	}

	@Test
	public void parseBlockSupportsLineCommentBetweenTryAndFinallyClause() {
		singleSpanBlockTest(
			"try { bar(); }" + newLine() +
				"// Foo" + newLine() +
				"// Bar" + newLine() +
				"finally { baz(); }",
			BlockType.Statement,
			SpanKind.Code,
			AcceptedCharacters.None
		);
	}

	@Test
	public void parseBlockSupportsLineCommentBetweenCatchAndFinallyClause() {
		singleSpanBlockTest(
			"try { bar(); } catch(bar) { baz(); }" + newLine() +
				"// Foo" + newLine() +
				"// Bar" + newLine() +
				"finally { biz(); }",
			BlockType.Statement,
			SpanKind.Code,
			AcceptedCharacters.None
		);
	}

	@Test
	public void parseBlockSupportsLineCommentBetweenTryAndCatchClause() {
		singleSpanBlockTest(
			"try { bar(); }" + newLine() +
				"// Foo" + newLine() +
				"// Bar" + newLine() +
				"catch(bar) {baz();}",
			BlockType.Statement,
			SpanKind.Code
		);
	}

	@Test
	public void parseBlockSupportsTryStatementWithNoAdditionalClauses() {
		singleSpanBlockTest("try { var foo = new { } }", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockSupportsMarkupWithinTryClause() {
		runSimpleWrappedMarkupTest("try {", " <p>Foo</p> ", "}");
	}

	@Test
	public void parseBlockSupportsTryStatementWithOneCatchClause() {
		singleSpanBlockTest("try { var foo = new { } } catch(Foo Bar Baz) { var foo = new { } }", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockSupportsMarkupWithinCatchClause() {
		runSimpleWrappedMarkupTest("try { var foo = new { } } catch(Foo Bar Baz) {", " <p>Foo</p> ", "}");
	}

	@Test
	public void parseBlockSupportsTryStatementWithMultipleCatchClause() {
		singleSpanBlockTest("try { var foo = new { } } catch(Foo Bar Baz) { var foo = new { } } catch(Foo Bar Baz) { var foo = new { } } catch(Foo Bar Baz) { var foo = new { } }", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockSupportsExceptionLessCatchClauses() {
		singleSpanBlockTest("try { var foo = new { } } catch { var foo = new { } }", BlockType.Statement, SpanKind.Code);
	}

	@Test
	public void parseBlockSupportsMarkupWithinAdditionalCatchClauses() {
		runSimpleWrappedMarkupTest("try { var foo = new { } } catch(Foo Bar Baz) { var foo = new { } } catch(Foo Bar Baz) { var foo = new { } } catch(Foo Bar Baz) {", " <p>Foo</p> ", "}");
	}

	@Test
	public void parseBlockSupportsTryStatementWithFinallyClause() {
		singleSpanBlockTest("try { var foo = new { } } finally { var foo = new { } }", BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockSupportsMarkupWithinFinallyClause() {
		runSimpleWrappedMarkupTest("try { var foo = new { } } finally {", " <p>Foo</p> ", "}", AcceptedCharacters.SetOfNone);
	}

	@Test
	public void parseBlockStopsParsingCatchClausesAfterFinallyBlock() {
		final String expectedContent = "try { var foo = new { } } finally { var foo = new { } }";
		singleSpanBlockTest(expectedContent + " catch(Foo Bar Baz) { }", expectedContent, BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockDoesNotAllowMultipleFinallyBlocks() {
		final String expectedContent = "try { var foo = new { } } finally { var foo = new { } }";
		singleSpanBlockTest(expectedContent + " finally { }", expectedContent, BlockType.Statement, SpanKind.Code, AcceptedCharacters.None);
	}

	@Test
	public void parseBlockAcceptsTrailingDotIntoImplicitExpressionWhenEmbeddedInCode() {
		// Arrange
		parseBlockTest("if(foo) { @foo. }",
			new StatementBlock(
				factory().code("if(foo) { ").asStatement().build(),
				new ExpressionBlock(
					factory().codeTransition().build(),
					factory().code("foo.")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords, true)
						.accepts(AcceptedCharacters.NonWhiteSpace).build()
				),
				factory().code(" }").asStatement().build()
			)
		);
	}

	@Test
	public void parseBlockParsesExpressionOnSwitchCharacterFollowedByOpenParen() {
		// Arrange
		parseBlockTest("if(foo) { @(foo + bar) }",
			new StatementBlock(
				factory().code("if(foo) { ").asStatement().build(),
				new ExpressionBlock(
					factory().codeTransition().build(),
					factory().metaCode("(").accepts(AcceptedCharacters.None).build(),
					factory().code("foo + bar").asExpression().build(),
					factory().metaCode(")").accepts(AcceptedCharacters.None).build()
				),
				factory().code(" }").asStatement().build()
			)
		);
	}

	@Test
	public void parseBlockParsesExpressionOnSwitchCharacterFollowedByIdentifierStart() {
		// Arrange
		parseBlockTest("if(foo) { @foo[4].bar() }",
			new StatementBlock(
				factory().code("if(foo) { ").asStatement().build(),
				new ExpressionBlock(
					factory().codeTransition().build(),
					factory().code("foo[4].bar()")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords, true)
						.accepts(AcceptedCharacters.NonWhiteSpace)
						.build()
				),
				factory().code(" }").asStatement().build()
			)
		);
	}

	@Test
	public void parseBlockTreatsDoubleAtSignAsEscapeSequenceIfAtStatementStart() {
		// Arrange
		parseBlockTest("if(foo) { @@class.Foo() }",
			new StatementBlock(
				factory().code("if(foo) { ").asStatement().build(),
				factory().code("@").hidden().build(),
				factory().code("@class.Foo() }").asStatement().build()
			)
		);
	}

	@Test
	public void parseBlockTreatsAtSignsAfterFirstPairAsPartOfCSharpStatement() {
		// Arrange
		parseBlockTest("if(foo) { @@@@class.Foo() }",
			new StatementBlock(
				factory().code("if(foo) { ").asStatement().build(),
				factory().code("@").hidden().build(),
				factory().code("@@@class.Foo() }").asStatement().build()
			)
		);
	}

	@Test
	public void parseBlockDoesNotParseMarkupStatementOrExpressionOnSwitchCharacterNotFollowedByOpenAngleOrColon() {
		// Arrange
		parseBlockTest("if(foo) { @\"Foo\".ToString(); }",
			new StatementBlock(
				factory().code("if(foo) { @\"Foo\".ToString(); }").asStatement().build()
			)
		);
	}

	@Test
	public void parsersCanNestRecursively() {
		// Arrange
		parseBlockTest(
			"foreach(var c in db.Categories) {" + newLine()
			+ "            <div>" + newLine()
			+ "                <h1>@c.Name</h1>" + newLine()
			+ "                <ul>" + newLine()
			+ "                    @foreach(var p in c.Products) {" + newLine()
			+ "                        <li><a href=\"@Html.ActionUrl(\"Products\", \"Detail\", new { id = p.Id })\">@p.Name</a></li>" + newLine()
			+ "                    }" + newLine()
			+ "                </ul>" + newLine()
			+ "            </div>" + newLine()
			+ "        }",
			new StatementBlock(
				factory().code("foreach(var c in db.Categories) {" + newLine()).asStatement().build(),
				new MarkupBlock(
					factory().markup("            <div>" + newLine() + "                <h1>").build(),
					new ExpressionBlock(
						factory().codeTransition().build(),
						factory().code("c.Name")
							.asImplicitExpression(JavaCodeParser.DefaultKeywords)
							.accepts(AcceptedCharacters.NonWhiteSpace)
							.build()
					),
					factory().markup("</h1>\r\n                <ul>" + newLine()).build(),
					new StatementBlock(
						factory().code("                    ").asStatement().build(),
						factory().codeTransition().build(),
						factory().code("foreach(var p in c.Products) {" + newLine()).asStatement().build(),
						new MarkupBlock(
							factory().markup("                        <li><a").build(),
							new MarkupBlock(
								new AttributeBlockCodeGenerator("href", new LocationTagged<>(" href=\"", 193, 5, 30), new LocationTagged<>("\"", 256, 5, 93)),
								factory().markup(" href=\"").with(SpanCodeGenerator.Null).build(),
								new MarkupBlock(
									new DynamicAttributeBlockCodeGenerator(new LocationTagged<>("", 200, 5, 37), 200, 5, 37),
									new ExpressionBlock(
										factory().codeTransition().build(),
										factory().code("Html.ActionUrl(\"Products\", \"Detail\", new { id = p.Id })")
											.asImplicitExpression(JavaCodeParser.DefaultKeywords)
											.accepts(AcceptedCharacters.NonWhiteSpace)
											.build()
									)
								),
								factory().markup("\"").with(SpanCodeGenerator.Null).build()
							),
							factory().markup(">").build(),
							new ExpressionBlock(
								factory().codeTransition().build(),
								factory().code("p.Name")
									.asImplicitExpression(JavaCodeParser.DefaultKeywords)
									.accepts(AcceptedCharacters.NonWhiteSpace).build()
							),
							factory().markup("</a></li>" + newLine()).accepts(AcceptedCharacters.None).build()
						),
						factory().code("                    }" + newLine()).asStatement().accepts(AcceptedCharacters.None).build()
					),
					factory().markup("                </ul>" + newLine() + "            </div>" + newLine())
						.accepts(AcceptedCharacters.None).build()
				),
				factory().code("        }").asStatement().accepts(AcceptedCharacters.None).build()
			)
		);
	}

	private void runRazorCommentBetweenClausesTest(@Nonnull final String preComment, @Nonnull final String postComment) {
		runRazorCommentBetweenClausesTest(preComment, postComment, AcceptedCharacters.Any);
	}

	private void runRazorCommentBetweenClausesTest(@Nonnull final String preComment, @Nonnull final String postComment, @Nonnull AcceptedCharacters acceptedCharacters) {
		runRazorCommentBetweenClausesTest(preComment, postComment, EnumSet.of(acceptedCharacters));
	}

	private void runRazorCommentBetweenClausesTest(@Nonnull final String preComment, @Nonnull final String postComment, @Nonnull final EnumSet<AcceptedCharacters> acceptedCharacters) {
		parseBlockTest(
			preComment + "@* Foo *@ @* Bar *@" + postComment,
			new StatementBlock(
				factory().code(preComment).asStatement().build(),
				new CommentBlock(
					factory().codeTransition(JavaSymbolType.RazorCommentTransition).build(),
					factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None).build(),
					factory().comment(" Foo ", JavaSymbolType.RazorComment).build(),
					factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None).build(),
					factory().codeTransition(JavaSymbolType.RazorCommentTransition).build()
				),
				factory().code(" ").asStatement().build(),
				new CommentBlock(
					factory().codeTransition(JavaSymbolType.RazorCommentTransition).build(),
					factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None).build(),
					factory().comment(" Bar ", JavaSymbolType.RazorComment).build(),
					factory().metaCode("*", JavaSymbolType.RazorCommentStar).accepts(AcceptedCharacters.None).build(),
					factory().codeTransition(JavaSymbolType.RazorCommentTransition).build()
				),
				factory().code(postComment).asStatement().accepts(acceptedCharacters).build()
			)
		);
	}

	private void runSimpleWrappedMarkupTest(@Nonnull final String prefix, @Nonnull final String markup, @Nonnull final String suffix) {
		runSimpleWrappedMarkupTest(prefix, markup, suffix, AcceptedCharacters.Any);
	}

	private void runSimpleWrappedMarkupTest(@Nonnull final String prefix, @Nonnull final String markup, @Nonnull final String suffix, @Nonnull final EnumSet<AcceptedCharacters> acceptedCharacters) {
		parseBlockTest(prefix + markup + suffix,
			new StatementBlock(
				factory().code(prefix).asStatement().build(),
				new MarkupBlock(
					factory().markup(markup).accepts(AcceptedCharacters.None).build()
				),
				factory().code(suffix).asStatement().accepts(acceptedCharacters).build()
			)
		);
	}

	private void packageImportTest(@Nonnull final String content, @Nonnull final String expectedPackage, @Nonnull final AcceptedCharacters... acceptedCharacters) {
		packageImportTest(content, expectedPackage, "", null, acceptedCharacters);
	}

	private void packageImportTest(@Nonnull final String content, @Nonnull final String expectedPackage, @Nullable SourceLocation location, @Nonnull final AcceptedCharacters... acceptedCharacters) {
		packageImportTest(content, expectedPackage, "", location, acceptedCharacters);
	}

	private void packageImportTest(@Nonnull final String content, @Nonnull final String expectedPackage, @Nullable String errorMessage, @Nullable SourceLocation location, @Nonnull final AcceptedCharacters... acceptedCharacters) {
		final EnumSet<AcceptedCharacters> ac;
		if (acceptedCharacters.length > 0) {
			ac = EnumSet.noneOf(AcceptedCharacters.class);
			Collections.addAll(ac, acceptedCharacters);
		}
		else {
			ac = EnumSet.of(AcceptedCharacters.None);
		}
		packageImportTest(content, expectedPackage, ac, Strings.nullToEmpty(errorMessage), location);
	}

	private void packageImportTest(@Nonnull final String content, @Nonnull final String expectedPackage, @Nonnull final EnumSet<AcceptedCharacters> acceptedCharacters, @Nonnull String errorMessage, @Nullable SourceLocation location) {
		RazorError[] errors = new RazorError[0];
		if (!Strings.isNullOrEmpty(errorMessage) && location != null) {
			errors = new RazorError[] { new RazorError(errorMessage, location) };
		}
		parseBlockTest(content,
			new DirectiveBlock(
				factory().code(content)
					.asPackageImport(expectedPackage, JavaCodeParser.UsingKeywordLength)
					.accepts(acceptedCharacters).build()
			),
			errors
		);
	}

}
