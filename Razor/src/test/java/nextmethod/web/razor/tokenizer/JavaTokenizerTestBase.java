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
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;

public class JavaTokenizerTestBase extends TokenizerTestBase<JavaSymbol, JavaSymbolType> {

    private static final JavaSymbol ignoreRemaining = new JavaSymbol(0, 0, 0, Strings.Empty, JavaSymbolType.Unknown);


    @Override
    protected JavaSymbol getIgnoreRemaining() {
        return ignoreRemaining;
    }

    @Override
    protected Tokenizer<JavaSymbol, JavaSymbolType> createTokenizer(final ITextDocument source) {
        return new JavaTokenizer(source);
    }

    protected void testSingleToken(final String text, final JavaSymbolType expectedSymbolType) {
        testTokenizer(text, new JavaSymbol(0, 0, 0, text, expectedSymbolType));
    }
}
