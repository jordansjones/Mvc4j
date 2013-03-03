package nextmethod.web.razor.parser.partialparsing;

import nextmethod.web.razor.JavaRazorCodeLanguage;
import nextmethod.web.razor.PartialParseResult;
import nextmethod.web.razor.StringTextBuffer;
import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.ParserTestBase;
import nextmethod.web.razor.framework.SpanFactory;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.TextChange;
import org.junit.Test;

import java.util.EnumSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;


public class JavaPartialParsingTest extends PartialParsingTestBase<JavaRazorCodeLanguage> {

	@Override
	protected JavaRazorCodeLanguage createNewLanguage() {
		return new JavaRazorCodeLanguage();
	}

	@Test
	public void implicitExpressionProvisionallyAcceptsDeleteOfIdentifierPartsIfDotRemains() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final StringTextBuffer changed = new StringTextBuffer("foo @User. baz");
		final StringTextBuffer old = new StringTextBuffer("foo @User.Name baz");
		runPartialParseTest(
			new TextChange(10, 4, old, 0, changed),
			new MarkupBlock(
				factory.markup("foo "),
				new ExpressionBlock(
					factory.codeTransition(),
					factory.code("User.")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory.markup(" baz")
			),
			EnumSet.of(PartialParseResult.Provisional)
		);
	}

	@Test
	public void implicitExpressionAcceptsDeleteOfIdentifierPartsIfSomeOfIdentifierRemains() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final StringTextBuffer changed = new StringTextBuffer("foo @Us baz");
		final StringTextBuffer old = new StringTextBuffer("foo @User baz");
		runPartialParseTest(
			new TextChange(7, 2, old, 0, changed),
			new MarkupBlock(
				factory.markup("foo "),
				new ExpressionBlock(
					factory.codeTransition(),
					factory.code("Us")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory.markup(" baz")
			)
		);
	}

	@Test
	public void implicitExpressionProvisionallyAcceptsMultipleInsertionIfItCausesIdentifierExpansionAndTrailingDot() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final StringTextBuffer changed = new StringTextBuffer("foo @User. baz");
		final StringTextBuffer old = new StringTextBuffer("foo @U baz");
		runPartialParseTest(
			new TextChange(6, 0, old, 4, changed),
			new MarkupBlock(
				factory.markup("foo "),
				new ExpressionBlock(
					factory.codeTransition(),
					factory.code("User.")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory.markup(" baz")
			),
			PartialParseResult.Provisional
		);
	}

	@Test
	public void implicitExpressionAcceptsMultipleInsertionIfItOnlyCausesIdentifierExpansion() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final StringTextBuffer changed = new StringTextBuffer("foo @barbiz baz");
		final StringTextBuffer old = new StringTextBuffer("foo @bar baz");
		runPartialParseTest(
			new TextChange(8, 0, old, 3, changed),
			new MarkupBlock(
				factory.markup("foo "),
				new ExpressionBlock(
					factory.codeTransition(),
					factory.code("barbiz")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory.markup(" baz")
			)
		);
	}

	@Test
	public void implicitExpressionAcceptsIdentifierExpansionAtEndOfNonWhitespaceCharacters() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final StringTextBuffer changed = new StringTextBuffer("@{" + Environment.NewLine + "    @food" + Environment.NewLine + "}");
		final StringTextBuffer old = new StringTextBuffer("@{" + Environment.NewLine + "    @foo" + Environment.NewLine + "}");
		runPartialParseTest(
			new TextChange(12, 0, old, 1, changed),
			new MarkupBlock(
				factory.emptyHtml(),
				new StatementBlock(
					factory.codeTransition(),
					factory.metaCode("{").accepts(AcceptedCharacters.None),
					factory.code("\r\n    ").asStatement(),
					new ExpressionBlock(
						factory.codeTransition(),
						factory.code("food")
							.asImplicitExpression(JavaCodeParser.DefaultKeywords, true)
							.accepts(AcceptedCharacters.NonWhiteSpace)
					),
					factory.code("\r\n").asStatement(),
					factory.metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory.emptyHtml()
			)
		);
	}

	@Test
	public void implicitExpressionAcceptsIdentifierAfterDotAtEndOfNonWhitespaceCharacters() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final StringTextBuffer changed = new StringTextBuffer("@{" + Environment.NewLine + "    @foo.d" + Environment.NewLine + "}");
		final StringTextBuffer old = new StringTextBuffer("@{" + Environment.NewLine + "    @foo." + Environment.NewLine + "}");
		runPartialParseTest(
			new TextChange(13, 0, old, 1, changed),
			new MarkupBlock(
				factory.emptyHtml(),
				new StatementBlock(
					factory.codeTransition(),
					factory.metaCode("{").accepts(AcceptedCharacters.None),
					factory.code("\r\n    ").asStatement(),
					new ExpressionBlock(
						factory.codeTransition(),
						factory.code("foo.d")
							.asImplicitExpression(JavaCodeParser.DefaultKeywords, true)
							.accepts(AcceptedCharacters.NonWhiteSpace)
					),
					factory.code("\r\n").asStatement(),
					factory.metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory.emptyHtml()
			)
		);
	}

	@Test
	public void implicitExpressionAcceptsDotAtEndOfNonWhitespaceCharacters() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final StringTextBuffer changed = new StringTextBuffer("@{" + Environment.NewLine + "    @foo." + Environment.NewLine + "}");
		final StringTextBuffer old = new StringTextBuffer("@{" + Environment.NewLine + "    @foo" + Environment.NewLine + "}");
		runPartialParseTest(
			new TextChange(12, 0, old, 1, changed),
			new MarkupBlock(
				factory.emptyHtml(),
				new StatementBlock(
					factory.codeTransition(),
					factory.metaCode("{").accepts(AcceptedCharacters.None),
					factory.code("\r\n    ").asStatement(),
					new ExpressionBlock(
						factory.codeTransition(),
						factory.code("foo.")
							.asImplicitExpression(JavaCodeParser.DefaultKeywords, true)
							.accepts(AcceptedCharacters.NonWhiteSpace)
					),
					factory.code("\r\n").asStatement(),
					factory.metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory.emptyHtml()
			)
		);
	}

	@Test
	public void implicitExpressionRejectsChangeWhichWouldHaveBeenAcceptedIfLastChangeWasProvisionallyAcceptedOnDifferentSpan() {
		final SpanFactory factory = SpanFactory.createJavaHtml();

		final TextChange dotTyped = new TextChange(8, 0, new StringTextBuffer("foo @foo @bar"), 1, new StringTextBuffer("foo @foo. @bar"));
		final TextChange charTyped = new TextChange(14, 0, new StringTextBuffer("foo @foo. @bar"), 1, new StringTextBuffer("foo @foo. @barb"));
		final TestParserManager manager = createParserManager();
		manager.initializeWithDocument(dotTyped.getOldBuffer());

		// Apply the dot change
		EnumSet<PartialParseResult> result = manager.checkForStructureChangesAndWait(dotTyped);
		assertEquals(EnumSet.of(PartialParseResult.Provisional, PartialParseResult.Accepted), result);

		// Apply the identifier start char change
		result = manager.checkForStructureChangesAndWait(charTyped);

		assertEquals(EnumSet.of(PartialParseResult.Rejected), result);
		assertFalse("LastResultProvisional flag should have been cleared but it was not", manager.parser.isLastResultProvisional());

		ParserTestBase.evaluateParseTree(
			manager.parser.getCurrentParseTree(),
			new MarkupBlock(
				factory.markup("foo "),
				new ExpressionBlock(
					factory.codeTransition(),
					factory.code("foo")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory.markup(". "),
				new ExpressionBlock(
					factory.codeTransition(),
					factory.code("barb")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory.emptyHtml()
			)
		);
	}

	@Test
	public void implicitExpressionAcceptsIdentifierTypedAfterDotIfLastChangeWasProvisionalAcceptanceOfDot() {
		final SpanFactory factory = SpanFactory.createJavaHtml();

		final TextChange dotTyped = new TextChange(8, 0, new StringTextBuffer("foo @foo bar"), 1, new StringTextBuffer("foo @foo. bar"));
		final TextChange charTyped = new TextChange(9, 0, new StringTextBuffer("foo @foo. bar"), 1, new StringTextBuffer("foo @foo.b bar"));
		final TestParserManager manager = createParserManager();
		manager.initializeWithDocument(dotTyped.getOldBuffer());

		// Apply the dot change
		EnumSet<PartialParseResult> result = manager.checkForStructureChangesAndWait(dotTyped);
		assertEquals(EnumSet.of(PartialParseResult.Provisional, PartialParseResult.Accepted), result);

		// Apply the identifier start char change
		result = manager.checkForStructureChangesAndWait(charTyped);

		assertEquals(EnumSet.of(PartialParseResult.Accepted), result);
		assertFalse("LastResultProvisional flag should have been cleared but it was not", manager.parser.isLastResultProvisional());

		ParserTestBase.evaluateParseTree(
			manager.parser.getCurrentParseTree(),
			new MarkupBlock(
				factory.markup("foo "),
				new ExpressionBlock(
					factory.codeTransition(),
					factory.code("foo.b")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory.markup(" bar")
			)
		);
	}

	@Test
	public void implicitExpressionProvisionallyAcceptsDotAfterIdentifierInMarkup() {
		final SpanFactory factory = SpanFactory.createJavaHtml();

		final StringTextBuffer changed = new StringTextBuffer("foo @foo. bar");
		final StringTextBuffer old = new StringTextBuffer("foo @foo bar");
		runPartialParseTest(
			new TextChange(8, 0, old, 1, changed),
			new MarkupBlock(
				factory.markup("foo "),
				new ExpressionBlock(
					factory.codeTransition(),
					factory.code("foo.")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory.markup(" bar")
			),
			PartialParseResult.Provisional
		);
	}

	@Test
	public void implicitExpressionAcceptsAdditionalIdentifierCharactersIfEndOfSpanIsIdentifier() {
		final SpanFactory factory = SpanFactory.createJavaHtml();

		final StringTextBuffer changed = new StringTextBuffer("foo @foob bar");
		final StringTextBuffer old = new StringTextBuffer("foo @foo bar");
		runPartialParseTest(
			new TextChange(8, 0, old, 1, changed),
			new MarkupBlock(
				factory.markup("foo "),
				new ExpressionBlock(
					factory.codeTransition(),
					factory.code("foob")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory.markup(" bar")
			)
		);
	}

	@Test
	public void implicitExpressionAcceptsAdditionalIdentifierStartCharactersIfEndOfSpanIsDot() {
		final SpanFactory factory = SpanFactory.createJavaHtml();

		final StringTextBuffer changed = new StringTextBuffer("@{@foo.b}");
		final StringTextBuffer old = new StringTextBuffer("@{@foo.}");
		runPartialParseTest(
			new TextChange(7, 0, old, 1, changed),
			new MarkupBlock(
				factory.emptyHtml(),
				new StatementBlock(
					factory.codeTransition(),
					factory.metaCode("{").accepts(AcceptedCharacters.None),
					factory.emptyJava().asStatement(),
					new ExpressionBlock(
						factory.codeTransition(),
						factory.code("foo.b")
							.asImplicitExpression(JavaCodeParser.DefaultKeywords, true)
							.accepts(AcceptedCharacters.NonWhiteSpace)
					),
					factory.emptyJava().asStatement(),
					factory.metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory.emptyHtml()
			)
		);
	}

	@Test
	public void implicitExpressionAcceptsDotIfTrailingDotsAreAllowed() {
		final SpanFactory factory = SpanFactory.createJavaHtml();

		final StringTextBuffer changed = new StringTextBuffer("@{@foo.}");
		final StringTextBuffer old = new StringTextBuffer("@{@foo}");
		runPartialParseTest(
			new TextChange(6, 0, old, 1, changed),
			new MarkupBlock(
				factory.emptyHtml(),
				new StatementBlock(
					factory.codeTransition(),
					factory.metaCode("{").accepts(AcceptedCharacters.None),
					factory.emptyJava().asStatement(),
					new ExpressionBlock(
						factory.codeTransition(),
						factory.code("foo.")
							.asImplicitExpression(JavaCodeParser.DefaultKeywords, true)
							.accepts(AcceptedCharacters.NonWhiteSpace)
					),
					factory.emptyJava().asStatement(),
					factory.metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory.emptyHtml()
			)
		);
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfIfKeywordTyped() {
		runTypeKeywordTest("if");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfDoKeywordTyped() {
		runTypeKeywordTest("do");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfTryKeywordTyped() {
		runTypeKeywordTest("try");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfForKeywordTyped() {
		runTypeKeywordTest("for");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfForEachKeywordTyped() {
		runTypeKeywordTest("foreach");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfWhileKeywordTyped() {
		runTypeKeywordTest("while");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfSwitchKeywordTyped() {
		runTypeKeywordTest("switch");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfLockKeywordTyped() {
		runTypeKeywordTest("lock");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfUsingKeywordTyped() {
		runTypeKeywordTest("using");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfSectionKeywordTyped() {
		runTypeKeywordTest("section");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfInheritsKeywordTyped() {
		runTypeKeywordTest("inherits");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfHelperKeywordTyped() {
		runTypeKeywordTest("helper");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfFunctionsKeywordTyped() {
		runTypeKeywordTest("functions");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfNamespaceKeywordTyped() {
		runTypeKeywordTest("namespace");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfPackageKeywordTyped() {
		runTypeKeywordTest("package");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfClassKeywordTyped() {
		runTypeKeywordTest("class");
	}

	@Test
	public void implicitExpressionCorrectlyTriggersReparseIfLayoutKeywordTyped() {
		runTypeKeywordTest("layout");
	}

}
