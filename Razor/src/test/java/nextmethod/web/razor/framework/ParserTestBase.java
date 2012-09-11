package nextmethod.web.razor.framework;

import com.google.common.collect.Lists;
import nextmethod.base.Delegates;
import nextmethod.web.razor.ParserResults;
import nextmethod.web.razor.parser.ParserBase;
import nextmethod.web.razor.parser.ParserContext;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.IgnoreOutputBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SeekableTextReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

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

	public static void evaluateParseTree(final Block actualBlock, final Block expectedRoot) {

	}

	public static void evaluateRazorErrors(final List<RazorError> actualErrors, final List<RazorError> expectedErrors) {

	}

	private void writeNode(int indent, final SyntaxTreeNode node) {

	}

	private static void writeTraceLine(final String format, final Object... args) {
		Logger.getGlobal().finest(String.format(format, args));
	}
}
