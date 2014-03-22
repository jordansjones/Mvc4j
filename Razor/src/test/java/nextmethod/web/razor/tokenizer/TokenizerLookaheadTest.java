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

import nextmethod.web.razor.text.LookaheadToken;
import nextmethod.web.razor.text.SeekableTextReader;
import nextmethod.web.razor.text.TextExtensions;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TokenizerLookaheadTest extends HtmlTokenizerTestBase {

    @Test
    public void afterCancellingLookaheadTokenizerReturnsSameTokensAsItDidBeforeLookahead()
        throws Exception {
        HtmlTokenizer tokenizer = new HtmlTokenizer(new SeekableTextReader("<foo>"));
        try (LookaheadToken token = TextExtensions.beginLookahead(tokenizer.getSource())) {
            assertEquals(new HtmlSymbol(0, 0, 0, "<", HtmlSymbolType.OpenAngle), tokenizer.nextSymbol());
            assertEquals(new HtmlSymbol(1, 0, 1, "foo", HtmlSymbolType.Text), tokenizer.nextSymbol());
            assertEquals(new HtmlSymbol(4, 0, 4, ">", HtmlSymbolType.CloseAngle), tokenizer.nextSymbol());
        }
        assertEquals(new HtmlSymbol(0, 0, 0, "<", HtmlSymbolType.OpenAngle), tokenizer.nextSymbol());
        assertEquals(new HtmlSymbol(1, 0, 1, "foo", HtmlSymbolType.Text), tokenizer.nextSymbol());
        assertEquals(new HtmlSymbol(4, 0, 4, ">", HtmlSymbolType.CloseAngle), tokenizer.nextSymbol());
    }

    @Test
    public void afterAcceptingLookaheadTokenizerReturnsNextToken()
        throws Exception {
        HtmlTokenizer tokenizer = new HtmlTokenizer(new SeekableTextReader("<foo>"));
        try (LookaheadToken lookahead = TextExtensions.beginLookahead(tokenizer.getSource())) {
            assertEquals(new HtmlSymbol(0, 0, 0, "<", HtmlSymbolType.OpenAngle), tokenizer.nextSymbol());
            assertEquals(new HtmlSymbol(1, 0, 1, "foo", HtmlSymbolType.Text), tokenizer.nextSymbol());
            lookahead.accept();
        }
        assertEquals(new HtmlSymbol(4, 0, 4, ">", HtmlSymbolType.CloseAngle), tokenizer.nextSymbol());
    }
}
