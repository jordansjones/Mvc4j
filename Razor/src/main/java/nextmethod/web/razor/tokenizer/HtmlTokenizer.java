package nextmethod.web.razor.tokenizer;

import com.google.common.collect.AbstractIterator;
import nextmethod.annotations.Internal;
import nextmethod.base.Delegates;
import nextmethod.collections.IterableIterator;
import nextmethod.web.razor.State;
import nextmethod.web.razor.parser.ParserHelpers;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SeekableTextReader;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;

import static nextmethod.web.razor.parser.ParserHelpers.isNewLine;
import static nextmethod.web.razor.parser.ParserHelpers.isWhitespace;

public class HtmlTokenizer extends Tokenizer<HtmlSymbol, HtmlSymbolType> {

	private final char transitionChar = '@';

	public HtmlTokenizer(@Nonnull final ITextDocument source) {
		super(source);
		setCurrentState(dataState);
	}

	@Override
	protected HtmlSymbol createSymbol(@Nonnull SourceLocation start, @Nonnull String content, @Nonnull HtmlSymbolType htmlSymbolType, @Nonnull Iterable<RazorError> errors) {
		return new HtmlSymbol(start, content, htmlSymbolType, errors);
	}

	@Override
	public HtmlSymbolType getRazorCommentStarType() {
		return HtmlSymbolType.RazorCommentStar;
	}

	@Override
	public HtmlSymbolType getRazorCommentType() {
		return HtmlSymbolType.RazorComment;
	}

	@Override
	public HtmlSymbolType getRazorCommentTransitionType() {
		return HtmlSymbolType.RazorCommentTransition;
	}

	@Override
	protected State getStartState() {
		return dataState;
	}

	@Internal
	public static Delegates.IFunc1<String, Iterable<ISymbol>> createTokenizeDelegate() {
		return new Delegates.IFunc1<String, Iterable<ISymbol>>() {

			@Override
			public Iterable<ISymbol> invoke(@Nullable final String input1) {
				if (input1 == null) {
					return Collections.<ISymbol>emptyList();
				}
				return HtmlTokenizer.tokenize(input1);
			}
		};
	}

	@Internal
	public static Iterable<ISymbol> tokenize(@Nonnull final String content) {
		return new IterableIterator<ISymbol>() {

			private final SeekableTextReader reader = new SeekableTextReader(content);
			private final HtmlTokenizer tok = new HtmlTokenizer(reader);
			private	HtmlSymbol sym;

			@Override
			protected ISymbol computeNext() {
				sym = tok.nextSymbol();
				return sym != null
					? sym
					: endOfData();
			}
		};
	}

	private final State dataState = new State() {
		@Override
		public StateResult invoke() {
			return data();
		}
	};

	private final State textState = new State() {
		@Override
		public StateResult invoke() {
			return text();
		}
	};

	private StateResult data() {
		if (isWhitespace(getCurrentChar()))
			return stay(whitespace());

		if (isNewLine(getCurrentChar()))
			return stay(newline());

		if (getCurrentChar() == '@') {
			takeCurrent();
			if (getCurrentChar() == '*')
				return transition(endSymbol(HtmlSymbolType.RazorCommentTransition), afterRazorCommenTransitionState);
			if (getCurrentChar() == '@')
				return transition(endSymbol(HtmlSymbolType.Transition), new State() {
					@Override
					public StateResult invoke() {
						takeCurrent();
						return transition(endSymbol(HtmlSymbolType.Transition), dataState);
					}
				});

			return stay(endSymbol(HtmlSymbolType.Transition));
		}

		if (atSymbol())
			return stay(symbol());

		return transition(textState);
	}

	private StateResult text() {
		char prev = '\0';
		while (!isEndOfFile() && !ParserHelpers.isWhitespaceOrNewLine(getCurrentChar()) && !atSymbol()) {
			prev = getCurrentChar();
			takeCurrent();
		}
		if (getCurrentChar() == '@') {
			char next = peek();
			if (ParserHelpers.isLetterOrDecimalDigit(prev) && ParserHelpers.isLetterOrDecimalDigit(next)) {
				takeCurrent(); // Take the "@"
				return stay(); // Stay in the text state
			}
		}
		return transition(endSymbol(HtmlSymbolType.Text), dataState);
	}

	private HtmlSymbol symbol() {
		assert atSymbol();

		final char currentChar = getCurrentChar();
		takeCurrent();
		switch (currentChar) {
			case '<':
				return endSymbol(HtmlSymbolType.OpenAngle);
			case '!':
				return endSymbol(HtmlSymbolType.Bang);
			case '/':
				return endSymbol(HtmlSymbolType.Solidus);
			case '?':
				return endSymbol(HtmlSymbolType.QuestionMark);
			case '[':
				return endSymbol(HtmlSymbolType.LeftBracket);
			case '>':
				return endSymbol(HtmlSymbolType.CloseAngle);
			case ']':
				return endSymbol(HtmlSymbolType.RightBracket);
			case '=':
				return endSymbol(HtmlSymbolType.Equals);
			case '"':
				return endSymbol(HtmlSymbolType.DoubleQuote);
			case '\'':
				return endSymbol(HtmlSymbolType.SingleQuote);
			case '-':
				assert getCurrentChar() == '-';
				takeCurrent();
				return endSymbol(HtmlSymbolType.DoubleHyphen);
			default:
				assert false : "Unexpected symbol!";
				return endSymbol(HtmlSymbolType.Unknown);
		}
	}

	private HtmlSymbol whitespace() {
		while (isWhitespace(getCurrentChar())) {
			takeCurrent();
		}
		return endSymbol(HtmlSymbolType.WhiteSpace);
	}

	private HtmlSymbol newline() {
		final char currentChar = getCurrentChar();
		assert isNewLine(currentChar);
		final boolean checkTwoCharNewline = currentChar == '\r';
		takeCurrent();
		if (checkTwoCharNewline && getCurrentChar() == '\n')
			takeCurrent();

		return endSymbol(HtmlSymbolType.NewLine);
	}

	private boolean atSymbol() {
		return getCurrentChar() == '<' ||
			getCurrentChar() == '<' ||
			getCurrentChar() == '!' ||
			getCurrentChar() == '/' ||
			getCurrentChar() == '?' ||
			getCurrentChar() == '[' ||
			getCurrentChar() == '>' ||
			getCurrentChar() == ']' ||
			getCurrentChar() == '=' ||
			getCurrentChar() == '"' ||
			getCurrentChar() == '\'' ||
			getCurrentChar() == '@' ||
			(getCurrentChar() == '-' && peek() == '-');
	}
}
