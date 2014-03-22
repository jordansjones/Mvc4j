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

package nextmethod.web.razor.tokenizer;

import javax.annotation.Nonnull;

import nextmethod.base.Debug;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;
import nextmethod.web.razor.tokenizer.symbols.SymbolBase;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class TokenizerView<
                              TTokenizer extends Tokenizer<TSymbol, TSymbolType>,
                              TSymbol extends SymbolBase<TSymbolType> & ISymbol,
                              TSymbolType
                              > {

    private final TTokenizer tokenizer;
    private boolean endOfFile;
    private TSymbol current;

    public TokenizerView(@Nonnull final TTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public boolean next() {
        this.current = this.tokenizer.nextSymbol();
        this.endOfFile = (this.current == null);
        return !this.endOfFile;
    }

    public void putBack(@Nonnull final TSymbol symbol) {
        if (Debug.isAssertEnabled()) {
            assert getSource().getPosition() == (symbol.getStart().getAbsoluteIndex() + symbol.getContent().length());
        }

        if (getSource().getPosition() != symbol.getStart().getAbsoluteIndex() + symbol.getContent().length()) {
            // Passed the symbol
            throw new UnsupportedOperationException(
                                                       RazorResources().tokenizerViewCannotPutBack(
                                                                                                      symbol.getStart()
                                                                                                            .getAbsoluteIndex() +
                                                                                                      symbol.getContent()
                                                                                                            .length(),
                                                                                                      getSource().getPosition()
                                                                                                  )
            );
        }

        int position = getSource().getPosition();
        position -= symbol.getContent().length();
        getSource().setPosition(position);
        current = null;
        endOfFile = getSource().getPosition() >= getSource().getLength();
        tokenizer.reset();
    }

    public ITextDocument getSource() {
        return tokenizer.getSource();
    }

    public TTokenizer getTokenizer() {
        return tokenizer;
    }

    public boolean isEndOfFile() {
        return endOfFile;
    }

    public TSymbol getCurrent() {
        return current;
    }
}
