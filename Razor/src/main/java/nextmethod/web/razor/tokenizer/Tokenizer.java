package nextmethod.web.razor.tokenizer;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import nextmethod.web.razor.State;
import nextmethod.web.razor.StateMachine;
import nextmethod.web.razor.parser.ParserHelpers;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.LookaheadToken;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.TextDocumentReader;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;
import nextmethod.web.razor.tokenizer.symbols.SymbolBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;
import static nextmethod.web.razor.text.TextExtensions.beginLookahead;

public abstract class Tokenizer<TSymbol extends SymbolBase<TSymbolType> & ISymbol, TSymbolType> extends StateMachine<TSymbol> implements ITokenizer<TSymbol> {

	protected final TextDocumentReader source;
	protected final StringBuilder buffer;
	protected final List<RazorError> currentErrors;
	protected SourceLocation currentStart;

	protected Tokenizer(final ITextDocument source) {
		this.source = new TextDocumentReader(checkNotNull(source));
		this.buffer = new StringBuilder();
		this.currentErrors = Lists.newArrayList();

		startSymbol();
	}

	protected abstract TSymbol createSymbol(@Nonnull final SourceLocation start, @Nonnull final String content, @Nonnull final TSymbolType type, @Nonnull final Iterable<RazorError> errors);
	public abstract TSymbolType getRazorCommentStarType();
	public abstract TSymbolType getRazorCommentType();
	public abstract TSymbolType getRazorCommentTransitionType();

	protected boolean isEndOfFile() {
		return source.peek() == -1;
	}

	protected boolean haveContent() {
		return buffer.length() > 0;
	}

	protected char getCurrentChar() {
		final int peek = source.peek();
		return peek == -1 ? '\0' : (char) peek;
	}

	protected SourceLocation getCurrentLocation() {
		return source.getLocation();
	}

	protected SourceLocation getCurrentStart() {
		return currentStart;
	}

	public TextDocumentReader getSource() {
		return source;
	}

	protected StringBuilder getBuffer() {
		return buffer;
	}

	protected List<RazorError> getCurrentErrors() {
		return currentErrors;
	}


	protected void resetBuffer() {
		buffer.delete(0, buffer.length());
	}

	@Override
	public TSymbol nextSymbol() {
		// Post-Condition: Buffer should be empty at the start of next()
		assert buffer.length() == 0;
		startSymbol();

		if (isEndOfFile())
			return null;

		final TSymbol sym = turn();

		assert buffer.length() == 0;

		return sym;
	}

	public void reset() {
		this.currentState = this.getStartState();
	}

	protected TSymbol single(@Nonnull final TSymbolType type) {
		takeCurrent();
		return endSymbol(type);
	}

	protected boolean takeString(@Nonnull final String input, final boolean caseSensitive) {
		int position = 0;
		final Function<Character, Character> charFilter = createCharFilter(caseSensitive);
		final int len = input.length();
		while (!isEndOfFile() && position < len && charFilter.apply(getCurrentChar()) == charFilter.apply(input.charAt(position++))) {
			takeCurrent();
		}
		return position == input.length();
	}

	protected void startSymbol() {
		resetBuffer();
		currentStart = getCurrentLocation();
		currentErrors.clear();
	}

	protected TSymbol endSymbol(@Nonnull final TSymbolType type) {
		return endSymbol(getCurrentStart(), type);
	}

	protected TSymbol endSymbol(@Nonnull final SourceLocation start, @Nonnull final TSymbolType type) {
		TSymbol sym = null;
		if (haveContent()) {
			sym = createSymbol(start, buffer.toString(), type, Lists.newArrayList(currentErrors));
		}
		startSymbol();
		return sym;
	}

	protected void resumeSymbol(@Nonnull final TSymbol previous) {
		// Verify the symbol can be resumed
		if (previous.getStart().getAbsoluteIndex() + previous.getContent().length() != getCurrentStart().getAbsoluteIndex()) {
			throw new UnsupportedOperationException(RazorResources().getString("tokenizer.cannotResumeSymbolUnlessIsPrevious"));
		}

		// Reset the start point
		currentStart = previous.getStart();

		// Capture the current buffer content
		final String newContent = buffer.toString();

		// Clear the buffer, then put the old content back and add the content to the end
		resetBuffer();
		buffer.append(previous.getContent());
		buffer.append(newContent);
	}

	protected boolean takeUntil(@Nonnull final Predicate<Character> predicate) {
		// Take all the characters up to the end of character
		while(!isEndOfFile() && !predicate.apply(getCurrentChar())) {
			takeCurrent();
		}
		// Why did loop end?
		return !isEndOfFile();
	}

	protected Predicate<Character> charOrWhiteSpace(final char character) {
		return new Predicate<Character>() {
			@Override
			public boolean apply(@Nullable Character input) {
				return input != null && (input == character || ParserHelpers.isWhitespace(input) || ParserHelpers.isNewLine(input));
			}
		};
	}

	protected void takeCurrent() {
		if (isEndOfFile())
			return;

		buffer.append(getCurrentChar());
		moveNext();
	}

	protected void moveNext() {
		source.read();
	}

	protected boolean takeAll(@Nonnull final String expected, boolean caseSensitive) {
		return lookahead(expected, true, caseSensitive);
	}

	protected boolean at(@Nonnull final String expected, final boolean caseSensitive) {
		return lookahead(expected, false, caseSensitive);
	}

	protected char peek() {
		final LookaheadToken lookaheadToken = beginLookahead(source);
		try {
			moveNext();
			return getCurrentChar();
		}
		finally {
			lookaheadToken.dispose();
		}
	}

	protected final State afterRazorCommenTransitionState = new State() {
		@Override
		public StateResult invoke() {
			return afterRazorCommentTransition();
		}
	};

	protected StateResult afterRazorCommentTransition() {
		if (getCurrentChar() != '*') {
			// We've been moved since last time we were asked for a symbol... reset the state
			return transition(getStartState());
		}
		assertCurrent('*');
		takeCurrent();
		return transition(endSymbol(getRazorCommentStarType()), new State() {
			@Override
			public StateResult invoke() {
				return razorCommentBody();
			}
		});
	}

	protected StateResult razorCommentBody() {
		takeUntil(Predicates.<Character>equalTo('*'));
		final char currentChar = getCurrentChar();
		if (currentChar == '*') {
			final SourceLocation start = getCurrentLocation();
			moveNext();
			if (!isEndOfFile() && getCurrentChar() == '@') {
				final State next = new State() {
					@Override
					public StateResult invoke() {
						buffer.append(currentChar);
						return transition(endSymbol(start, getRazorCommentStarType()), new State() {
							@Override
							public StateResult invoke() {
								if (getCurrentChar() != '@') {
									// We've been moved since last time we were asked for a symbol... reset the state
									return transition(getStartState());
								}
								takeCurrent();
								return transition(endSymbol(getRazorCommentTransitionType()), getStartState());
							}
						});
					}
				};
				if (haveContent())
					return transition(endSymbol(getRazorCommentType()), next);
				else
					return transition(next);
			}
			else {
				buffer.append(currentChar);
				return stay();
			}
		}
		return transition(endSymbol(getRazorCommentType()), getStartState());
	}

	private boolean lookahead(@Nonnull final String expected, final boolean takeIfMatch, final boolean caseSensitive) {
		final Function<Character, Character> charFilter = createCharFilter(!caseSensitive);

		final int len = expected.length();
		if (len == 0 || charFilter.apply(getCurrentChar()) != charFilter.apply(expected.charAt(0)))
			return false;

		// Capture the current buffer content in case we have to backtrack
		String oldBuffer = null;
		if (takeIfMatch)
			oldBuffer = buffer.toString();

		try (final LookaheadToken lookaheadToken = beginLookahead(source)) {
			for (int i = 0; i < len; i++) {
				if (charFilter.apply(getCurrentChar()) != charFilter.apply(expected.charAt(i))) {
					if (takeIfMatch) {
						// Clear the buffer and put the old buffer text back
						resetBuffer();
						buffer.append(oldBuffer);
					}
					// return without accepting lookahead (thus rejecting it)
					return false;
				}
				if (takeIfMatch)
					takeCurrent();
				else
					moveNext();
			}
			if (takeIfMatch)
				lookaheadToken.accept();
		}
		catch (Exception ignored) {}

		return true;
	}

	void assertCurrent(final char current) {
		assert getCurrentChar() == current;
	}

	private static Function<Character, Character> createCharFilter(final boolean caseSensitive) {
		return new Function<Character, Character>() {
			@Override
			public Character apply(@Nullable Character input) {
				if (input == null) return null;

				return caseSensitive ? Character.toLowerCase(input) : input;
			}
		};
	}
}
