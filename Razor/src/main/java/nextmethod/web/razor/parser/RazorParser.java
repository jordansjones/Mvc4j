package nextmethod.web.razor.parser;

import com.google.common.collect.Lists;
import nextmethod.base.Delegates;
import nextmethod.threading.CancellationToken;
import nextmethod.threading.OperationCanceledException;
import nextmethod.threading.SynchronizationContext;
import nextmethod.threading.Task;
import nextmethod.web.razor.ParserResults;
import nextmethod.web.razor.parser.internal.ConditionalAttributeCollapser;
import nextmethod.web.razor.parser.internal.ISyntaxTreeRewriter;
import nextmethod.web.razor.parser.internal.WhiteSpaceRewriter;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SeekableTextReader;
import nextmethod.web.razor.text.TextReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class RazorParser {

	private final ParserBase codeParser;
	private final ParserBase markupParser;
	private final List<ISyntaxTreeRewriter> optimizers;

	private boolean designTimeMode;

	public RazorParser(@Nonnull final ParserBase codeParser, @Nonnull final ParserBase markupParser) {
		this.codeParser = checkNotNull(codeParser);
		this.markupParser = checkNotNull(markupParser);

		this.optimizers = Lists.<ISyntaxTreeRewriter>newArrayList(
			// Move whitespace from start of expression block to markup
			new WhiteSpaceRewriter(markupParser.createBuildSpanDelegate()),
			// Collapse conditional attributes where the entire value is literal
			new ConditionalAttributeCollapser(markupParser.createBuildSpanDelegate())
		);
	}

	public ParserBase getCodeParser() {
		return codeParser;
	}

	public ParserBase getMarkupParser() {
		return markupParser;
	}

	public List<ISyntaxTreeRewriter> getOptimizers() {
		return optimizers;
	}

	public boolean isDesignTimeMode() {
		return designTimeMode;
	}

	public RazorParser setDesignTimeMode(final boolean designTimeMode) {
		this.designTimeMode = designTimeMode;
		return this;
	}

	public void parse(@Nonnull final TextReader input, @Nonnull final ParserVisitor visitor) {
		final ParserResults results = parseCore(new SeekableTextReader(checkNotNull(input)));
		// Replay the results on the visitor
		checkNotNull(visitor).visit(results);
	}

	public ParserResults parse(@Nonnull final TextReader input) {
		return parseCore(new SeekableTextReader(checkNotNull(input)));
	}

	public ParserResults parse(@Nonnull final ITextDocument input) {
		return parseCore(checkNotNull(input));
	}

	public Task createParseTask(@Nonnull final TextReader input, @Nonnull final Delegates.IAction1<Span> spanCallback, @Nonnull final Delegates.IAction1<RazorError> errorCallback) {
		return createParseTask(checkNotNull(input), new CallbackVisitor(checkNotNull(spanCallback, checkNotNull(errorCallback))));
	}

	public Task createParseTask(@Nonnull final TextReader input, @Nonnull final Delegates.IAction1<Span> spanCallback, @Nonnull final Delegates.IAction1<RazorError> errorCallback, @Nonnull final SynchronizationContext context) {
		return createParseTask(
			checkNotNull(input),
			new CallbackVisitor(checkNotNull(spanCallback), checkNotNull(errorCallback))
				.setSynchronizationContext(checkNotNull(context))
		);
	}

	public Task createParseTask(@Nonnull final TextReader input, @Nonnull final Delegates.IAction1<Span> spanCallback, @Nonnull final Delegates.IAction1<RazorError> errorCallback, @Nullable final CancellationToken cancelToken) {
		return createParseTask(
			checkNotNull(input),
			new CallbackVisitor(checkNotNull(spanCallback), checkNotNull(errorCallback))
				.setCancelToken(cancelToken)
		);
	}

	public Task createParseTask(@Nonnull final TextReader input, @Nonnull final Delegates.IAction1<Span> spanCallback, @Nonnull final Delegates.IAction1<RazorError> errorCallback, @Nonnull final SynchronizationContext context, @Nullable final CancellationToken cancelToken) {
		return createParseTask(
			checkNotNull(input),
			new CallbackVisitor(checkNotNull(spanCallback), checkNotNull(errorCallback))
				.setSynchronizationContext(checkNotNull(context))
				.setCancelToken(cancelToken)
		);
	}

	public Task createParseTask(@Nonnull final TextReader input, @Nonnull final ParserVisitor consumer) {
		return new Task(() -> {
			try {
				parse(input, consumer);
			}
			catch (OperationCanceledException ignored) {}
		});
	}

	private ParserResults parseCore(final ITextDocument input) {
		final ParserContext context = new ParserContext(checkNotNull(input), codeParser, markupParser, markupParser);
		context
			.setDesignTimeMode(designTimeMode);

		markupParser.setContext(context);
		codeParser.setContext(context);

		// Execute the parse
		markupParser.parseDocument();

		// Get the result
		final ParserResults results = context.completeParse();

		// Rewrite whitespace if supported
		Block current = results.getDocument();
		for (ISyntaxTreeRewriter optimizer : optimizers) {
			current = optimizer.rewrite(current);
		}

		// Link the leaf nodes into a chain
		Span prev = null;
		for (Span node : current.flatten()) {
			node.setPrevious(prev);
			if (prev != null) {
				prev.setNext(node);
			}
			prev = node;
		}

		// Return the new result
		return new ParserResults(current, results.getParserErrors());
	}
}
