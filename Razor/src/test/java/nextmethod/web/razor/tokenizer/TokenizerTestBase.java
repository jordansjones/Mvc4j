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

import java.io.StringReader;

import nextmethod.base.SystemHelpers;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SeekableTextReader;
import nextmethod.web.razor.tokenizer.symbols.SymbolBase;

import static org.junit.Assert.assertTrue;

public abstract class TokenizerTestBase<TSymbol extends SymbolBase<TSymbolType>, TSymbolType> {

    protected abstract TSymbol getIgnoreRemaining();

    protected abstract Tokenizer<TSymbol, TSymbolType> createTokenizer(final ITextDocument source);

    @SafeVarargs
    protected final void testTokenizer(final String input, final TSymbol... symbols) {
        boolean success = true;
        final StringBuilder output = new StringBuilder();
        try (final StringReader reader = new StringReader(input)) {
            final SeekableTextReader source = new SeekableTextReader(reader);
            final Tokenizer<TSymbol, TSymbolType> tokenizer = createTokenizer(source);
            int counter = 0;
            TSymbol current = null;
            while ((current = tokenizer.nextSymbol()) != null) {
                if (counter >= symbols.length) {
                    output.append(String.format("F: Expected: << Nothing >>; Actual: %s", current))
                          .append(SystemHelpers.newLine());
                    success = false;
                }
                else if (getIgnoreRemaining().equals(symbols[counter])) {
                    output.append(String.format("P: Ignored %s", current)).append(SystemHelpers.newLine());
                }
                else {
                    if (!current.equals(symbols[counter])) {
                        output.append(String.format("F: Expected: %s; Actual: %s", symbols[counter], current))
                              .append(SystemHelpers.newLine());
                        success = false;
                    }
                    else {
                        output.append(String.format("P: Expected %s", current)).append(SystemHelpers.newLine());
                    }
                    counter++;
                }
            }
            if (counter < symbols.length && !getIgnoreRemaining().equals(symbols[counter])) {
                success = false;
                for (; counter < symbols.length; counter++) {
                    output.append(String.format("F: Expected: %s; Actual: << NONE >>", symbols[counter]))
                          .append(SystemHelpers.newLine());
                }
            }
        }
        assertTrue("\r\n" + output.toString(), success);
    }
}
