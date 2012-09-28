package nextmethod.web.razor.parser;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import nextmethod.base.IDisposable;
import nextmethod.base.KeyValue;
import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
// TODO
public class HtmlMarkupParserSection extends HtmlMarkupParserDelegate {

	private boolean caseSensitive;

	public HtmlMarkupParserSection(@Nonnull final HtmlMarkupParser delegate) {
		super(delegate);
	}

	@Override
	public void parseSection(@Nonnull final KeyValue<String, String> nestingSequence, final boolean caseSensitive) {
		if (getContext() == null) {
			throw new UnsupportedOperationException(RazorResources().getString("parser.context.not.set"));
		}

		try (IDisposable ignored = pushSpanConfig()) {
			try (IDisposable ignored2 = getContext().startBlock(BlockType.Markup)) {
				nextToken();
				this.caseSensitive = caseSensitive;
				if (nestingSequence.getKey() == null) {
					final Iterable<String> split = Splitter.on(CharMatcher.WHITESPACE).split(nestingSequence.getValue());
					nonNestingSection(
						Iterables.toArray(split, String.class)
					);
				}
				else {
					nestingSection(nestingSequence);
				}
				addMarkerSymbolIfNecessary();
				output(SpanKind.MetaCode);
			}
		}
	}

	private void nonNestingSection(@Nullable final String[] nestingSequenceComponents) {
		throw new NotImplementedException();
	}

	private void nestingSection(@Nonnull final KeyValue<String, String> nestingSequence) {
		throw new NotImplementedException();
	}

	private boolean atEnd(@Nonnull final String[] nestingSequenceComponents) {
		throw new NotImplementedException();
	}

	private int processTextToken(@Nonnull final KeyValue<String, String> nestingSequence, final int currentNesting) {
		throw new NotImplementedException();
	}

	private int handleNestingSequence(@Nonnull final String sequence, final int position, final int currentNesting, final int retIfMatched) {
		throw new NotImplementedException();
	}
}
