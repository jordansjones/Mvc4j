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

import javax.annotation.Nonnull;

import nextmethod.base.Delegates;
import nextmethod.collections.IterableIterator;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SeekableTextReader;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.Tokenizer;
import nextmethod.web.razor.tokenizer.symbols.SymbolBase;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
class TokenizeStringIterator<
                                TTokenizer extends Tokenizer<TSymbol, TSymbolType>,
                                TSymbol extends SymbolBase<TSymbolType>,
                                TSymbolType extends Enum<TSymbolType>
                                > extends IterableIterator<TSymbol> {

    private final SourceLocation start;
    private final String input;
    private final Delegates.IFunc1<ITextDocument, TTokenizer> createTokenizerDelegate;

    TokenizeStringIterator(@Nonnull final SourceLocation start, @Nonnull final String input,
                           @Nonnull final Delegates.IFunc1<ITextDocument, TTokenizer> createTokenizerDelegate
                          ) {
        this.start = checkNotNull(start);
        this.input = checkNotNull(input);
        this.createTokenizerDelegate = checkNotNull(createTokenizerDelegate);
    }

    private SeekableTextReader textReader;
    private TTokenizer tokenizer;

    @Override
    protected TSymbol computeNext() {
        final TTokenizer tok = ensureTokenizer();
        TSymbol sym;
        if ((sym = tok.nextSymbol()) != null) {
            sym.offsetStart(start);
            return sym;
        }

        textReader.close();

        return endOfData();
    }

    private TTokenizer ensureTokenizer() {
        if (textReader == null) {
            this.textReader = new SeekableTextReader(this.input);
            this.tokenizer = createTokenizerDelegate.invoke(this.textReader);
        }
        return this.tokenizer;
    }


}
