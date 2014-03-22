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
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import org.junit.Test;

public class JavaTokenizerTest extends JavaTokenizerTestBase {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void constructorThrowsArgNullIfNullSourceProvided() {
        new JavaTokenizer(null);
    }

    @Test
    public void NextReturnsNullWhenEOFReached() {
        testTokenizer(Strings.Empty);
    }

    @Test
    public void nextReturnsNewlineTokenForSingleCR() {
        testTokenizer(
                         "\r\ra",
                         new JavaSymbol(0, 0, 0, "\r", JavaSymbolType.NewLine),
                         new JavaSymbol(1, 1, 0, "\r", JavaSymbolType.NewLine),
                         getIgnoreRemaining()
                     );
    }

    @Test
    public void nextReturnsNewlineTokenForSingleLF() {
        testTokenizer(
                         "\n\na",
                         new JavaSymbol(0, 0, 0, "\n", JavaSymbolType.NewLine),
                         new JavaSymbol(1, 1, 0, "\n", JavaSymbolType.NewLine),
                         getIgnoreRemaining()
                     );
    }

    @Test
    public void nextReturnsNewlineTokenForSingleNEL() {
        // NEL: Unicode "Next Line" U+0085
        testTokenizer(
                         "\u0085\u0085a",
                         new JavaSymbol(0, 0, 0, "\u0085", JavaSymbolType.NewLine),
                         new JavaSymbol(1, 1, 0, "\u0085", JavaSymbolType.NewLine),
                         getIgnoreRemaining()
                     );
    }

    @Test
    public void nextReturnsNewlineTokenForSingleLineSeparator() {
        // Unicode "Line Separator" U+2028
        testTokenizer(
                         "\u2028\u2028a",
                         new JavaSymbol(0, 0, 0, "\u2028", JavaSymbolType.NewLine),
                         new JavaSymbol(1, 1, 0, "\u2028", JavaSymbolType.NewLine),
                         getIgnoreRemaining()
                     );
    }

    @Test
    public void nextReturnsNewlineTokenForSingleParagraphSeparator() {
        // Unicode "Paragraph Separator" U+2029
        testTokenizer(
                         "\u2029\u2029a",
                         new JavaSymbol(0, 0, 0, "\u2029", JavaSymbolType.NewLine),
                         new JavaSymbol(1, 1, 0, "\u2029", JavaSymbolType.NewLine),
                         getIgnoreRemaining()
                     );
    }

    @Test
    public void nextReturnsSingleNewlineTokenForCRLF() {
        testTokenizer(
                         "\r\n\r\na",
                         new JavaSymbol(0, 0, 0, "\r\n", JavaSymbolType.NewLine),
                         new JavaSymbol(2, 1, 0, "\r\n", JavaSymbolType.NewLine),
                         getIgnoreRemaining()
                     );
    }

    @Test
    public void nextReturnsTokenForWhitespaceCharacters() {
        testTokenizer(
                         " \f\t\u000B \n ",
                         new JavaSymbol(0, 0, 0, " \f\t\u000B ", JavaSymbolType.WhiteSpace),
                         new JavaSymbol(5, 0, 5, "\n", JavaSymbolType.NewLine),
                         new JavaSymbol(6, 1, 0, " ", JavaSymbolType.WhiteSpace)
                     );
    }

    @Test
    public void transitionIsRecognized() {
        testSingleToken("@", JavaSymbolType.Transition);
    }

    @Test
    public void transitionIsRecognizedAsSingleCharacter() {
        testTokenizer(
                         "@(",
                         new JavaSymbol(0, 0, 0, "@", JavaSymbolType.Transition),
                         new JavaSymbol(1, 0, 1, "(", JavaSymbolType.LeftParenthesis)
                     );
    }
}
