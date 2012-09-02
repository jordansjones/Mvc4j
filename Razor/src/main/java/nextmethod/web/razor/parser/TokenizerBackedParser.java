package nextmethod.web.razor.parser;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import nextmethod.annotations.TODO;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.Tuple;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.Tokenizer;
import nextmethod.web.razor.tokenizer.TokenizerView;
import nextmethod.web.razor.tokenizer.symbols.KnownSymbolType;
import nextmethod.web.razor.tokenizer.symbols.SymbolBase;
import nextmethod.web.razor.utils.DisposableAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class TokenizerBackedParser<
	TTokenizer extends Tokenizer<TSymbol, TSymbolType>,
	TSymbol extends SymbolBase<TSymbolType>,
	TSymbolType
	> extends ParserBase {

	private TokenizerView<TTokenizer, TSymbol, TSymbolType> tokenizer;
	private SpanBuilder span;
	private Delegates.IAction1<SpanBuilder> spanConfig;
	private TSymbol previousSymbol;

	protected TokenizerBackedParser() {
		this.span = new SpanBuilder();
	}


	protected SpanBuilder getSpan() {
		return span;
	}

	protected void setSpan(@Nonnull final SpanBuilder span) {
		this.span = span;
	}

	protected TokenizerView<TTokenizer, TSymbol, TSymbolType> getTokenizer() {
		return tokenizer != null ? tokenizer : initTokenizer();
	}

	private TokenizerView<TTokenizer, TSymbol, TSymbolType> initTokenizer() {
		return this.tokenizer = new TokenizerView<TTokenizer, TSymbol, TSymbolType>(getLanguage().createTokenizer(getContext().getSource()));
	}

	protected Delegates.IAction1<SpanBuilder> getSpanConfig() {
		return spanConfig;
	}

	protected void setSpanConfig(@Nullable final Delegates.IAction1<SpanBuilder> spanConfig) {
		this.spanConfig = spanConfig;
	}

	protected TSymbol getPreviousSymbol() {
		return previousSymbol;
	}

	protected TSymbol getCurrentSymbol() {
		return tokenizer.getCurrent();
	}

	protected SourceLocation getCurrentLocation() {
		return (isEndOfFile() || getCurrentSymbol() == null) ? getContext().getSource().getLocation() : getCurrentSymbol().getStart();
	}

	protected boolean isEndOfFile() {
		return tokenizer.isEndOfFile();
	}

	protected abstract LanguageCharacteristics<TTokenizer, TSymbol, TSymbolType> getLanguage();

	protected void handleEmbeddedTransition() {

	}

	protected boolean isAtEmbeddedTransition(final boolean allowTemplatesAndComments, final boolean allowTransitions) {
		return false;
	}

	public void buildSpan(@Nonnull final SpanBuilder span, @Nonnull final SourceLocation start, @Nonnull final String content) {
		for (TSymbol tSymbol : getLanguage().tokenizeString(start, content)) {
			span.accept(tSymbol);
		}
	}

	protected void initialize(@Nonnull final SpanBuilder span) {
		if (spanConfig != null) {
			spanConfig.invoke(span);
		}
	}

	protected boolean nextToken() {
		previousSymbol = getCurrentSymbol();
		return tokenizer.next();
	}

	// Helpers

	void assertSymbol(@Nonnull final TSymbolType expectedType) {
		assert !isEndOfFile() && Objects.equals(getCurrentSymbol().getType(), expectedType);
	}

	protected void putBack(@Nullable final TSymbol symbol) {
		if (symbol != null) {
			getTokenizer().putBack(symbol);
		}
	}

	/**
	 * Put the specified symbols back in the input stream. The provide list MUST be in the ORDER THE SYMBOLS WERE READ.
	 * The list WILL be reversed and the putBack(TSymbol) will be called on each item.
	 * @param symbols TSymbols to be put back into the input stream
	 */
	protected void putBack(@Nullable final Iterable<TSymbol> symbols) {
		if (symbols != null) {
			List<TSymbol> tSymbols = Lists.newArrayList(symbols);
			Collections.reverse(tSymbols);
			for (TSymbol symbol : tSymbols) {
				putBack(symbol);
			}
		}
	}

	protected void putCurrentBack() {
		if (!isEndOfFile() && getCurrentSymbol() != null) {
			putBack(getCurrentSymbol());
		}
	}

	@TODO
	protected boolean balance(@Nonnull final EnumSet<BalancingModes> mode) {
		final TSymbolType left = getCurrentSymbol().getType();
		final TSymbolType right = getLanguage().flipBracket(left);
		final SourceLocation start = getCurrentLocation();
		acceptAndMoveNext();
		if (isEndOfFile() && !mode.contains(BalancingModes.NoErrorOnFailure)) {
//			getContext().onError(start,)
		}
		return balance(mode, left, right, start);
	}

	protected boolean balance(@Nonnull final EnumSet<BalancingModes> mode, @Nonnull final TSymbolType left, @Nonnull final TSymbolType right, @Nonnull final SourceLocation start) {
		int startPosition = getCurrentLocation().getAbsoluteIndex();
		int nesting = 1;
		if (!isEndOfFile()) {
			final List<TSymbol> syms = Lists.newArrayList();
			do {
				if (isAtEmbeddedTransition(
					mode.contains(BalancingModes.AllowCommentsAndTemplates),
					mode.contains(BalancingModes.AllowEmbeddedTransitions)
				)) {
					accept(syms);
					syms.clear();
					handleEmbeddedTransition();

					// Reset backtracking since we've already outputted some spans.
					startPosition = getCurrentLocation().getAbsoluteIndex();
				}
				if (at(left)) {
					nesting++;
				}
				else if (at(right)) {
					nesting--;
				}
				if (nesting > 0) {
					syms.add(getCurrentSymbol());
				}
			}
			while (nesting > 0 && nextToken());

			if (nesting > 0) {
				if (!mode.contains(BalancingModes.NoErrorOnFailure)) {
//					getContext().onError()
				}
				if (mode.contains(BalancingModes.BacktrackOnFailure)) {
					getContext().getSource().setPosition(startPosition);
				}
				else {
					accept(syms);
				}
			}
			else {
				// Accept all the symbols we saw
				accept(syms);
			}
		}
		return nesting == 0;
	}

	protected boolean nextIs(@Nonnull final TSymbolType type) {
		return nextIs(new Delegates.IFunc1<TSymbol, Boolean>() {

			@Override
			public Boolean invoke(@Nullable final TSymbol input1) {
				return input1 != null && type == input1.getType();
			}
		});
	}

	protected boolean nextIs(@Nonnull final TSymbolType... types) {
		final List<TSymbolType> tSymbolTypes = Lists.newArrayList(types);
		return nextIs(new Delegates.IFunc1<TSymbol, Boolean>() {
			@Override
			public Boolean invoke(@Nullable final TSymbol input1) {
				return input1 != null && Iterables.any(tSymbolTypes, new Predicate<TSymbolType>() {
					@Override
					public boolean apply(@Nullable final TSymbolType input) {
						return input != null && input == input1.getType();
					}
				});
			}
		});
	}

	protected boolean nextIs(@Nonnull final Delegates.IFunc1<TSymbol, Boolean> condition) {
		final TSymbol cur = getCurrentSymbol();
		nextToken();
		final Boolean result = condition.invoke(getCurrentSymbol());
		putCurrentBack();
		putBack(cur);
		ensureCurrent();
		return result != null && result;
	}

	protected boolean was(@Nonnull final TSymbolType type) {
		return getPreviousSymbol() != null && getPreviousSymbol().getType() == type;
	}

	protected boolean at(@Nonnull final TSymbolType type) {
		return !isEndOfFile() && getCurrentSymbol() != null && getCurrentSymbol().getType() == type;
	}

	protected boolean acceptAndMoveNext() {
		accept(getCurrentSymbol());
		return nextToken();
	}

	@Nullable
	protected TSymbol acceptSingleWhiteSpaceCharacter() {
		if (getLanguage().isWhiteSpace(getCurrentSymbol())) {
			final Tuple<TSymbol,TSymbol> pair = getLanguage().splitSymbol(getCurrentSymbol(), 1, getLanguage().getKnownSymbolType(KnownSymbolType.WhiteSpace));
			accept(pair.getItem1());
			getSpan().getEditHandler().setAcceptedCharacters(EnumSet.of(AcceptedCharacters.None));
			nextToken();
			return pair.getItem2();
		}
		return null;
	}

	protected void accept(@Nonnull final Iterable<TSymbol> symbols) {
		for (TSymbol symbol : symbols) {
			accept(symbol);
		}
	}

	protected void accept(@Nullable final TSymbol symbol) {
		if (symbol != null) {
			for (RazorError error : symbol.getErrors()) {
				getContext().getErrors().add(error);
			}
			getSpan().accept(symbol);
		}
	}

	@SafeVarargs
	protected final boolean acceptAll(@Nonnull final TSymbolType... types) {
		for (TSymbolType type : types) {
			if (getCurrentSymbol() == null || getCurrentSymbol().getType() != type) {
				return false;
			}
			acceptAndMoveNext();
		}
		return true;
	}

	protected void addMarkerSymbolIfNecessary() {
		addMarkerSymbolIfNecessary(getCurrentLocation());
	}

	protected void addMarkerSymbolIfNecessary(@Nonnull final SourceLocation location) {
		if (getSpan().getSymbols().size() == 0 && !AcceptedCharacters.Any.equals(getContext().getLastAcceptedCharacters())) {
			accept(getLanguage().createMarkerSymbol(location));
		}
	}

	protected void output(@Nonnull final SpanKind kind) {
		configure(kind, null);
		output();
	}

	protected void output(@Nonnull final SpanKind kind, @Nonnull final EnumSet<AcceptedCharacters> accepts) {
		configure(kind, accepts);
		output();
	}

	protected void output(@Nonnull final EnumSet<AcceptedCharacters> accepts) {
		configure(null, accepts);
		output();
	}

	protected void output() {
		if (getSpan().getSymbols().size() > 0) {
			getContext().addSpan(getSpan().build());
			initialize(getSpan());
		}
	}

	protected IDisposable pushSpanConfig() {
		return pushSpanConfig((Delegates.IAction2<SpanBuilder, Delegates.IAction1<SpanBuilder>>) null);
	}

	protected IDisposable pushSpanConfig(@Nullable final Delegates.IAction1<SpanBuilder> newConfig) {
		return pushSpanConfig(newConfig == null ? (Delegates.IAction2<SpanBuilder, Delegates.IAction1<SpanBuilder>>)null : new Delegates.IAction2<SpanBuilder, Delegates.IAction1<SpanBuilder>>() {
			@Override
			public void invoke(@Nullable final SpanBuilder input1, @Nullable final Delegates.IAction1<SpanBuilder> input2) {
				newConfig.invoke(input1);
			}
		});
	}

	protected IDisposable pushSpanConfig(@Nullable final Delegates.IAction2<SpanBuilder, Delegates.IAction1<SpanBuilder>> newConfig) {
		final Delegates.IAction1<SpanBuilder> old = getSpanConfig();
		configureSpan(newConfig);
		return new DisposableAction(new Delegates.IAction() {
			@Override
			public void invoke() {
				setSpanConfig(old);
			}
		});
	}

	protected void configureSpan(@Nonnull final Delegates.IAction1<SpanBuilder> config) {
		setSpanConfig(config);
		initialize(getSpan());
	}

	protected void configureSpan(@Nullable final Delegates.IAction2<SpanBuilder, Delegates.IAction1<SpanBuilder>> config) {
		final Delegates.IAction1<SpanBuilder> prev = getSpanConfig();
		if (config == null) {
			setSpanConfig(null);
		}
		else {
			setSpanConfig(new Delegates.IAction1<SpanBuilder>() {
				@Override
				public void invoke(@Nullable final SpanBuilder input) {
					config.invoke(input, prev);
				}
			});
		}
		initialize(getSpan());
	}

	protected void expected(@Nonnull final KnownSymbolType type) {
		expected(getLanguage().getKnownSymbolType(type));
	}

	@SafeVarargs
	protected final void expected(@Nonnull final TSymbolType... types) {
		assert !isEndOfFile() && getCurrentSymbol() != null && Arrays.asList(types).contains(getCurrentSymbol().getType());
		acceptAndMoveNext();
	}

	protected boolean optional(@Nonnull final KnownSymbolType type) {
		return optional(getLanguage().getKnownSymbolType(type));
	}

	protected boolean optional(@Nonnull final TSymbolType type) {
		if (at(type)) {
			acceptAndMoveNext();
			return true;
		}
		return false;
	}

	protected boolean required(@Nonnull final TSymbolType expected, final boolean errorIfNotFound, @Nonnull final String errorBase) {
		final boolean found = at(expected);
		if (!found && errorIfNotFound) {
			String error;
			if (getLanguage().isNewLine(getCurrentSymbol())) {
				error = "";// TODO
			}
			else if (getLanguage().isWhiteSpace(getCurrentSymbol())) {
				error = "";// TODO
			}
			else if (isEndOfFile()) {
				error = ""; // TODO
			}
			else {
				error = "";// TODO
			}

			getContext().onError(
				getCurrentLocation(),
				errorBase,
				error
			);
		}

		return found;
	}

	protected boolean ensureCurrent() {
		if (getCurrentSymbol() == null) {
			return nextToken();
		}
		return true;
	}

	protected void acceptWhile(@Nonnull final TSymbolType type) {

	}

	private void configure(@Nullable final SpanKind kind, @Nullable EnumSet<AcceptedCharacters> accepts) {
		// TODO
	}

}
