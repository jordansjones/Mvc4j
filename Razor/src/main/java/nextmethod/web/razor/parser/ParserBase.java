package nextmethod.web.razor.parser;

import nextmethod.base.Delegates;
import nextmethod.base.KeyValue;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public abstract class ParserBase {

	private ParserContext context;

	public ParserContext getContext() {
		return context;
	}

	public void setContext(@Nonnull final ParserContext context) {
		assert this.context == null : "Context has already been set for this parser!";
		this.context = context;
		this.context.assertOnOwnerTask();
	}

	public boolean isMarkerParser() {
		return false;
	}

	protected abstract ParserBase getOtherParser();

	public abstract void buildSpan(@Nonnull final SpanBuilder span, @Nonnull final SourceLocation start, @Nonnull final String content);

	public Delegates.IAction3<SpanBuilder, SourceLocation, String> createBuildSpanDelegate() {
		return (span, start, content) -> {
			assert span != null;
			assert start != null;
			assert content != null;
			buildSpan(span, start, content);
		};
	}


	public abstract void parseBlock();

	public Delegates.IAction createParseBlockDelegate() {
		return this::parseBlock;
	}

	/**
	 * Markup Parsers need the parseDocument method since
	 * the markup parser is the first parser to hit the document and the
	 * logic may be different than the parseBlock method.
	 */
	public void parseDocument() {
		assert isMarkerParser();
		throw new UnsupportedOperationException(RazorResources().parserIsNotAMarkupParser());
	}

	public Delegates.IAction createParseDocumentDelegate() {
		return this::parseDocument;
	}

	/**
	 * Markup Parsers need the parseSection method since
	 * the markup parser is the first parser to hit the document and the
	 * logic may be different than the parseBlock method.
	 */
	public void parseSection(@Nonnull final KeyValue<String, String> nestingSequence, final boolean caseSensitive) {
		assert isMarkerParser();
		throw new UnsupportedOperationException(RazorResources().parserIsNotAMarkupParser());
	}
}
