package nextmethod.web.razor.parser;

import com.google.common.collect.Lists;
import nextmethod.Tuple;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.SourceLocationTracker;
import nextmethod.web.razor.tokenizer.Tokenizer;
import nextmethod.web.razor.tokenizer.symbols.KnownSymbolType;
import nextmethod.web.razor.tokenizer.symbols.SymbolBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

// TODO
public abstract class LanguageCharacteristics<
	TTokenizer extends Tokenizer<TSymbol, TSymbolType>,
	TSymbol extends SymbolBase<TSymbolType>,
	TSymbolType
	> {

	public abstract String getSample(@Nonnull final TSymbolType type);
	public abstract TTokenizer createTokenizer(@Nonnull final ITextDocument source);
	public abstract TSymbolType flipBracket(@Nonnull final TSymbolType bracket);
	public abstract TSymbol createMarkerSymbol(@Nonnull final SourceLocation location);
	public abstract TSymbolType getKnownSymbolType(@Nonnull final KnownSymbolType type);
	protected abstract TSymbol createSymbol(@Nonnull final SourceLocation location, @Nonnull final String content, @Nonnull final TSymbolType type, @Nonnull final Iterable<RazorError> errors);

	public Iterable<TSymbol> tokenizeString(@Nonnull final String content) {
		return tokenizeString(SourceLocation.Zero, content);
	}

	public Iterable<TSymbol> tokenizeString(@Nonnull final SourceLocation start, @Nonnull final String input) {
		// TODO
		return null;
	}

	public boolean isWhiteSpace(@Nonnull final TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.WhiteSpace);
	}

	public boolean isNewLine(@Nonnull final TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.NewLine);
	}

	public boolean isIdentifier(@Nonnull final TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.Identifier);
	}

	public boolean isKeyword(@Nonnull final TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.Keyword);
	}

	public boolean isTransition(@Nonnull final TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.Transition);
	}

	public boolean isCommentStart(@Nonnull final TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.CommentStart);
	}

	public boolean isCommentStar(@Nonnull final TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.CommentStar);
	}

	public boolean isCommentBody(@Nonnull final TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.CommentBody);
	}

	public boolean isUnknown(@Nonnull final TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.Unknown);
	}

	public boolean isKnownSymbolType(@Nullable final TSymbol symbol, @Nonnull final KnownSymbolType type) {
		return symbol != null && Objects.equals(symbol.getType(), getKnownSymbolType(type));
	}

	public Tuple<TSymbol, TSymbol> splitSymbol(@Nonnull final TSymbol symbol, final int splitAt, @Nonnull final TSymbolType leftType) {
		final SourceLocation symbolStart = symbol.getStart();
		final String symbolContent = symbol.getContent();
		final TSymbol left = createSymbol(symbolStart, symbolContent.substring(0, splitAt), leftType, Lists.<RazorError>newArrayList());
		TSymbol right = null;
		if (splitAt < symbolContent.length()) {
			right = createSymbol(SourceLocationTracker.calculateNewLocation(symbolStart, left.getContent()), symbolContent.substring(splitAt), symbol.getType(), symbol.getErrors());
		}
		return Tuple.of(left, right);
	}

	public boolean knowsSymbolType(@Nonnull final KnownSymbolType type) {
		return type == KnownSymbolType.Unknown || !Objects.equals(getKnownSymbolType(type), getKnownSymbolType(KnownSymbolType.Unknown));
	}
}
