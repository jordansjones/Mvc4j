package nextmethod.web.razor.framework;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.web.razor.ParserResults;
import nextmethod.web.razor.parser.ParserBase;
import nextmethod.web.razor.parser.ParserContext;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockExtensions;
import nextmethod.web.razor.parser.syntaxtree.IgnoreOutputBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SeekableTextReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static nextmethod.base.SystemHelpers.newLine;
import static nextmethod.base.TypeHelpers.typeAs;
import static org.junit.Assert.*;

// TODO
public abstract class ParserTestBase {

	protected static Block IgnoreOutput = new IgnoreOutputBlock();

	protected final SpanFactory factory;

	protected ParserTestBase() {
		this.factory = createSpanFactory();
	}

	public SpanFactory getFactory() {
		return factory;
	}

	public abstract ParserBase createMarkupParser();
	public abstract ParserBase createCodeParser();

	protected abstract ParserBase selectActiveParser(@Nonnull final ParserBase codeParser, @Nonnull final ParserBase markupParser);

	public ParserContext createParserContext(@Nonnull final ITextDocument input, @Nonnull final ParserBase codeParser, @Nonnull final ParserBase markupParser) {
		return new ParserContext(input, codeParser, markupParser, selectActiveParser(codeParser, markupParser));
	}

	protected abstract SpanFactory createSpanFactory();

	protected void parseBlockTest(@Nonnull final String document) {
		parseBlockTest(document, null, false, new RazorError[0]);
	}

	protected void parseBlockTest(@Nonnull final String document, final boolean designTimeParser) {
		parseBlockTest(document, null, designTimeParser, new RazorError[0]);
	}

	protected void parseBlockTest(@Nonnull final String document, final RazorError... expectedErrors) {
		parseBlockTest(document, false, expectedErrors);
	}

	protected void parseBlockTest(@Nonnull final String document, final boolean designTimeParser, final RazorError... expectedErrors) {
		parseBlockTest(document, null, designTimeParser, expectedErrors);
	}

	protected void parseBlockTest(@Nonnull final String document, @Nonnull final Block expectedRoot) {
		parseBlockTest(document, expectedRoot, false, null);
	}

	protected void parseBlockTest(@Nonnull final String document, @Nonnull final Block expectedRoot, final boolean designTimeParser) {
		parseBlockTest(document, expectedRoot, designTimeParser, null);
	}

	protected void parseBlockTest(@Nonnull final String document, @Nonnull final Block expectedRoot, final RazorError... expectedErrors) {
		parseBlockTest(document, expectedRoot, false, expectedErrors);
	}

	protected void parseBlockTest(@Nonnull final String document, @Nonnull final Block expectedRoot, final boolean designTimeParser, final RazorError... expectedErrors) {
		runParseTest(document, new Delegates.IFunc1<ParserBase, Delegates.IAction>() {
			@Override
			public Delegates.IAction invoke(@Nullable final ParserBase parser) {
				return new Delegates.IAction() {
					@Override
					public void invoke() {
						if (parser != null) {
							parser.parseBlock();
						}
					}
				};
			}
		}, expectedRoot, (expectedErrors == null ? Collections.<RazorError>emptyList() : Lists.newArrayList(expectedErrors)), designTimeParser);
	}

	protected ParserResults runParse(final String document, final Delegates.IFunc1<ParserBase, Delegates.IAction> parserActionSelector, final Block expectedRoot, final boolean designTimeParser) {
		return runParse(document, parserActionSelector, expectedRoot, designTimeParser, null);
	}

	protected ParserResults runParse(final String document, Delegates.IFunc1<ParserBase, Delegates.IAction> parserActionSelector, final Block expectedRoot, final boolean designTimeParser, Delegates.IFunc1<ParserContext, ParserBase> parserSelector) {
		parserSelector = parserSelector != null ? parserSelector : new Delegates.IFunc1<ParserContext, ParserBase>() {
			@Override
			public ParserBase invoke(@Nullable final ParserContext c) {
				return c == null ? null : c.getActiveParser();
			}
		};

		// Create the source
		ParserResults results = null;
		try (SeekableTextReader reader = new SeekableTextReader(document)) {
			final ParserBase codeParser = createCodeParser();
			final ParserBase markupParser = createMarkupParser();
			final ParserContext context = createParserContext(reader, codeParser, markupParser);
			context.setDesignTimeMode(designTimeParser);

			codeParser.setContext(context);
			markupParser.setContext(context);

			// Run the parser
			final Delegates.IAction parserAction = parserActionSelector.invoke(parserSelector.invoke(context));
			if (parserAction != null) {
				parserAction.invoke();
			}
			results = context.completeParse();
		}
		finally {
			if (results != null && results.getDocument() != null) {
				writeTraceLine("");
				writeTraceLine("Actual Parse Tree:");
				writeNode(0, results.getDocument());
			}
		}
		return results;
	}

	protected void runParseTest(final String document, final Delegates.IFunc1<ParserBase, Delegates.IAction> parserActionSelector, final Block expectedRoot, final List<RazorError> expectedErrors, final boolean designTimeParser) {
		runParseTest(document, parserActionSelector, expectedRoot, expectedErrors, designTimeParser, null);
	}

	protected void runParseTest(final String document, final Delegates.IFunc1<ParserBase, Delegates.IAction> parserActionSelector, final Block expectedRoot, final List<RazorError> expectedErrors, final boolean designTimeParser, Delegates.IFunc1<ParserContext, ParserBase> parserSelector) {
		// Create the source
		final ParserResults results = runParse(document, parserActionSelector, expectedRoot, designTimeParser, parserSelector);

		// Evaluate the results
		if (!expectedRoot.equals(IgnoreOutput)) {
			evaluateResults(results, expectedRoot, expectedErrors);
		}
	}

	public static void evaluateResults(final ParserResults results, final Block expectedRoot) {
		evaluateResults(results, expectedRoot, null);
	}

	public static void evaluateResults(final ParserResults results, final Block expectedRoot, final List<RazorError> expectedErrors) {
		evaluateParseTree(results.getDocument(), expectedRoot);
		evaluateRazorErrors(results.getParserErrors(), expectedErrors);
	}

	public static void evaluateParseTree(final Block actualRoot, final Block expectedRoot) {
		// Evaluate the result
		final ErrorCollector collector = new ErrorCollector();

		// Link all the Nodes
		BlockExtensions.linkNodes(expectedRoot);

		if (expectedRoot == null) {
			assertNull(actualRoot);
		}
		else {
			assertNotNull(actualRoot);
			evaluateSyntaxTreeNode(collector, actualRoot, expectedRoot);
			if (collector.isSuccess()) {
				writeTraceLine("Parse Tree Validation Succeeded:\r\n%s", collector.getMessage());
			}
			else {
				fail(String.format("\r\n%s", collector.getMessage()));
			}
		}
	}

	private static void evaluateSyntaxTreeNode(final ErrorCollector collector, final SyntaxTreeNode actual, final SyntaxTreeNode expected) {
		if (actual == null) {
			addNullActualError(collector, actual, expected);
			return;
		}

		if (actual.isBlock() != expected.isBlock()) {
			addMismatchError(collector, actual, expected);
		}
		else if (expected.isBlock()) {
			evaluateBlock(collector, (Block) actual, (Block) expected);
		}
		else {
			evaluateSpan(collector, (Span) actual, (Span) expected);
		}
	}

	private static void evaluateSpan(final ErrorCollector collector, final Span actual, final Span expected) {
		if (!expected.equals(actual)) {
			addMismatchError(collector, actual, expected);
		}
		else {
			addPassedMessage(collector, expected);
		}
	}

	private static void evaluateBlock(final ErrorCollector collector, final Block actual, final Block expected) {
		if (actual.getType() != expected.getType() || !expected.getCodeGenerator().equals(actual.getCodeGenerator())) {
			addMismatchError(collector, actual, expected);
		}
		else {
			addPassedMessage(collector, expected);
			try(IDisposable d = collector.indent()) {
				final Iterator<SyntaxTreeNode> expectedNodes = expected.getChildren().iterator();
				final Iterator<SyntaxTreeNode> actualNodes = actual.getChildren().iterator();
				while (expectedNodes.hasNext()) {
					if (!actualNodes.hasNext()) {
						collector.addError("%s - FAILED :: No more elements at this node", expectedNodes.next());
					}
					else {
						evaluateSyntaxTreeNode(collector, actualNodes.next(), expectedNodes.next());
					}
				}
				while (actualNodes.hasNext()) {
					collector.addError("End of Node - FAILED :: %s", actualNodes.next());
				}
			}
		}
	}

	private static void addPassedMessage(final ErrorCollector collector, final SyntaxTreeNode expected) {
		collector.addMessage("%s - PASSED", expected);
	}

	private static void addMismatchError(final ErrorCollector collector, final SyntaxTreeNode actual, final SyntaxTreeNode expected) {
		collector.addError("%s - FAILED :: Actual: %s", expected, actual);
	}

	private static void addNullActualError(final ErrorCollector collector, final SyntaxTreeNode actual, final SyntaxTreeNode expected) {
		collector.addError("%s - FAILED :: Actual: << Null >>", expected);
	}

	public static void evaluateRazorErrors(final List<RazorError> actualErrors, final List<RazorError> expectedErrors) {
		// Evaluate the errors
		if (expectedErrors == null || expectedErrors.isEmpty()) {
			assertTrue(String.format("Expected that no errors would be raised, but the following errors were:\r\n%s", formatErrors(actualErrors)), actualErrors.isEmpty());
		}
		else {
			assertTrue(
				String.format("Expected that %d errors would be raised, but %d errors were.\r\nExpected Errors: \r\n%s\r\nActual Errors: \r\n%s",
					expectedErrors.size(),
					actualErrors.size(),
					formatErrors(expectedErrors),
					formatErrors(actualErrors)
				),
				expectedErrors.size() == actualErrors.size()
			);
			assertArrayEquals(expectedErrors.toArray(), actualErrors.toArray());
		}
		writeTraceLine("Expected Errors were raised:\r\n%s", formatErrors(expectedErrors));
	}

	public static String formatErrors(final List<RazorError> errors) {
		if (errors == null || errors.isEmpty())
			return "\t<< No Errors >>";

		final StringBuilder sb = new StringBuilder();
		for (RazorError error : errors) {
			sb.append("\t")
				.append(error)
				.append(newLine());
		}
		return sb.toString();
	}

	private void writeNode(int indent, final SyntaxTreeNode node) {
		String content = node.toString().replace("\r", "\\r")
			.replace("\n", "\\n")
			.replace("{", "{{")
			.replace("}", "}}");

		if (indent > 0)
			content = Strings.repeat(".", indent * 2) + content;

		writeTraceLine(content);
		final Block block = typeAs(node, Block.class);
		if (block != null) {
			for (SyntaxTreeNode child : block.getChildren()) {
				writeNode(indent + 1, child);
			}
		}
	}

	private static void writeTraceLine(final String format, final Object... args) {
		Logger.getGlobal().finest(String.format(format, args));
	}
}
