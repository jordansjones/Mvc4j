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

import nextmethod.base.Strings;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;

public abstract class HtmlTokenizerTestBase extends TokenizerTestBase<HtmlSymbol, HtmlSymbolType> {

    private static HtmlSymbol ignoreRemaining = new HtmlSymbol(0, 0, 0, Strings.Empty, HtmlSymbolType.Unknown);

    @Override
    protected Tokenizer<HtmlSymbol, HtmlSymbolType> createTokenizer(final ITextDocument source) {
        return new HtmlTokenizer(source);
    }

    @Override
    protected HtmlSymbol getIgnoreRemaining() {
        return ignoreRemaining;
    }

    protected void testSingleToken(final String text, final HtmlSymbolType expectedType) {
        testTokenizer(text, new HtmlSymbol(0, 0, 0, text, expectedType));
    }
}
