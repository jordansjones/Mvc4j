package nextmethod.web.razor.parser;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.KeyValue;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
class HtmlMarkupParserSection extends HtmlMarkupParserDelegate {

	private boolean caseSensitive;

	public HtmlMarkupParserSection(@Nonnull final HtmlMarkupParser delegate) {
		super(delegate);
	}

	@Override
	public void parseSection(@Nonnull final KeyValue<String, String> nestingSequence, final boolean caseSensitive) {
		if (getContext() == null) {
			throw new UnsupportedOperationException(RazorResources().getString("parser.context.not.set"));
		}

		try (IDisposable ignored = pushSpanConfig(defaultMarkupSpanDelegate)) {
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
				output(SpanKind.Markup);
			}
		}
	}

	private void nonNestingSection(@Nonnull final String[] nestingSequenceComponents) {
		do {
			skipToAndParseCode(new Delegates.IFunc1<HtmlSymbol, Boolean>() {
				@Override
				public Boolean invoke(@Nullable final HtmlSymbol sym) {
					return sym != null && (sym.isType(HtmlSymbolType.OpenAngle) || atEnd(nestingSequenceComponents));
				}
			});
			getDocumentParser().scanTagInDocumentContext();
			if (!isEndOfFile() && atEnd(nestingSequenceComponents)) {
				break;
			}
		}
		while (!isEndOfFile());

		putCurrentBack();
	}

	private void nestingSection(@Nonnull final KeyValue<String, String> nestingSequence) {
		int nesting = 1;
		while (nesting > 0 && !isEndOfFile()) {
			skipToAndParseCode(new Delegates.IFunc1<HtmlSymbol, Boolean>() {
				@Override
				public Boolean invoke(@Nullable final HtmlSymbol sym) {
					return sym != null && sym.isTypeOr(HtmlSymbolType.Text, HtmlSymbolType.OpenAngle);
				}
			});
			if (at(HtmlSymbolType.Text)) {
				nesting += processTextToken(nestingSequence, nesting);
				if (getCurrentSymbol() != null) {
					acceptAndMoveNext();
				}
				else if (nesting > 0) {
					nextToken();
				}
			}
			else {
				getDocumentParser().scanTagInDocumentContext();
			}
		}
	}

	private boolean atEnd(@Nonnull final String[] nestingSequenceComponents) {
		ensureCurrent();
		if (isEqualTo(getCurrentSymbol().getContent(), nestingSequenceComponents[0])) {
			final int bookmark = getCurrentSymbol().getStart().getAbsoluteIndex();
			try {
				for (String component : nestingSequenceComponents) {
					if (!isEqualTo(getCurrentSymbol().getContent(), component)) {
						return false;
					}
					nextToken();
					//noinspection ConstantConditions
					while (!isEndOfFile() && isSpacingToken(true).invoke(getCurrentSymbol())) {
						nextToken();
					}
				}
				return true;
			}
			finally {
				getContext().getSource().setPosition(bookmark);
				nextToken();
			}
		}
		return false;
	}

	private int processTextToken(@Nonnull final KeyValue<String, String> nestingSequence, final int currentNesting) {
		for (int i = 0, symContentLength = getCurrentSymbol().getContent().length(); i < symContentLength; i++) {
			int nestingDelta = handleNestingSequence(nestingSequence.getKey(), i, currentNesting, 1);
			if (nestingDelta == 0) {
				nestingDelta = handleNestingSequence(nestingSequence.getValue(), i, currentNesting, -1);
			}

			if (nestingDelta != 0) {
				return nestingDelta;
			}
		}
		return 0;
	}

	private int handleNestingSequence(@Nullable final String sequence, final int position, final int currentNesting, final int retIfMatched) {
		if (sequence == null) return 0;

		final String symContent = getCurrentSymbol().getContent();
		if (symContent.charAt(position) == sequence.charAt(0) && (position + sequence.length() <= symContent.length())) {
			final String possibleStart = symContent.substring(position, (position + sequence.length()));
			if (isEqualTo(possibleStart, sequence)) {
				// Capture the current symbol and "put it back" (really we just want to clear CurrentSymbol)
				final int bookmark = getContext().getSource().getPosition();
				HtmlSymbol sym = getCurrentSymbol();
				putCurrentBack();

				// Carve up the symbol
				KeyValue<HtmlSymbol, HtmlSymbol> pair = getLanguage().splitSymbol(sym, position, HtmlSymbolType.Text);
				final HtmlSymbol preSequence = pair.getKey();
				if (Debug.isAssertEnabled()) assert pair.getValue() != null;
				//noinspection ConstantConditions
				pair = getLanguage().splitSymbol(pair.getValue(), sequence.length(), HtmlSymbolType.Text);
				final HtmlSymbol sequenceToken = pair.getKey();
				final HtmlSymbol postSequence = pair.getValue();

				// Accept the first chunk (up to the nesting sequence we just saw)
				if (preSequence != null && !Strings.isNullOrEmpty(preSequence.getContent())) {
					accept(preSequence);
				}

				// Accept the sequence if it isn't the last one
				if (currentNesting + retIfMatched != 0) {
					accept(sequenceToken);


					int newPosition = bookmark;
					// Position at the start of the postSequence symbol
					if (postSequence != null) {
						newPosition = postSequence.getStart().getAbsoluteIndex();
					}
					getContext().getSource().setPosition(newPosition);
				}

				// Return the value we were asked to return if matched, since we found a nesting sequence
				return retIfMatched;
			}
		}
		return 0;
	}

	private boolean isEqualTo(@Nonnull String first, @Nonnull String second) {
		first = Strings.nullToEmpty(first);
		second = Strings.nullToEmpty(second);
		return this.caseSensitive
			? first.equals(second)
			: first.equalsIgnoreCase(second);
	}
}
