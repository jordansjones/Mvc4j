/*
 * Copyright 2014 Jordan S. Jones <jordansjones@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextmethod.web.razor.parser;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import nextmethod.base.Delegates;
import nextmethod.base.KeyValue;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.SourceLocationTracker;
import nextmethod.web.razor.tokenizer.Tokenizer;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;
import nextmethod.web.razor.tokenizer.symbols.KnownSymbolType;
import nextmethod.web.razor.tokenizer.symbols.SymbolBase;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class LanguageCharacteristics<
                                                 TTokenizer extends Tokenizer<TSymbol, TSymbolType>,
                                                 TSymbol extends SymbolBase<TSymbolType>,
                                                 TSymbolType extends Enum<TSymbolType>
                                                 > {

    public abstract String getSample(@Nonnull final TSymbolType type);

    public abstract TTokenizer createTokenizer(@Nonnull final ITextDocument source);

    public abstract TSymbolType flipBracket(@Nonnull final TSymbolType bracket);

    public abstract TSymbol createMarkerSymbol(@Nonnull final SourceLocation location);

    public abstract TSymbolType getKnownSymbolType(@Nonnull final KnownSymbolType type);

    protected abstract TSymbol createSymbol(@Nonnull final SourceLocation location, @Nonnull final String content,
                                            @Nonnull final TSymbolType type, @Nonnull final Iterable<RazorError> errors
                                           );

    @SuppressWarnings("unchecked")
    public <XSymbol extends ISymbol> Delegates.IFunc1<String, Iterable<XSymbol>> createTokenizeStringDelegate() {
        return tokenString -> (Iterable<XSymbol>) (tokenString == null
                                                   ? Lists.<XSymbol>newArrayList()
                                                   : tokenizeString(tokenString));
    }

    public Iterable<TSymbol> tokenizeString(@Nonnull final String content) {
        return tokenizeString(SourceLocation.Zero, content);
    }

    public Iterable<TSymbol> tokenizeString(@Nonnull final SourceLocation start, @Nonnull final String input) {
        return new TokenizeStringIterator<TTokenizer, TSymbol, TSymbolType>(
                                                                               start, input,
                                                                               input1 -> createTokenizer(checkNotNull(input1))
        );
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

    public KeyValue<TSymbol, TSymbol> splitSymbol(@Nonnull final TSymbol symbol, final int splitAt,
                                                  @Nonnull final TSymbolType leftType
                                                 ) {
        final SourceLocation symbolStart = symbol.getStart();
        final String symbolContent = symbol.getContent();
        final TSymbol left = createSymbol(
                                             symbolStart, symbolContent.substring(0, splitAt), leftType,
                                             Lists.<RazorError>newArrayList()
                                         );
        TSymbol right = null;
        if (splitAt < symbolContent.length()) {
            right = createSymbol(
                                    SourceLocationTracker.calculateNewLocation(symbolStart, left.getContent()),
                                    symbolContent.substring(splitAt), symbol.getType(), symbol.getErrors()
                                );
        }
        return KeyValue.of(left, right);
    }

    public boolean knowsSymbolType(@Nonnull final KnownSymbolType type) {
        return type == KnownSymbolType.Unknown ||
               !Objects.equals(getKnownSymbolType(type), getKnownSymbolType(KnownSymbolType.Unknown));
    }
}
