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
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import org.junit.Test;

public class HtmlTokenizerTest extends HtmlTokenizerTestBase {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void constructorThrowsNPEIfNullSourceProvided() {
        new HtmlTokenizer(null);
    }

    @Test
    public void nextReturnsNullWhenEOFReached() {
        testTokenizer(Strings.Empty);
    }

    @Test
    public void textIsRecognized() {
        testTokenizer(
                         "foo-9309&smlkmb;::-3029022,.sdkq92384",
                         new HtmlSymbol(0, 0, 0, "foo-9309&smlkmb;::-3029022,.sdkq92384", HtmlSymbolType.Text)
                     );
    }

    @Test
    public void whitespaceIsRecognized() {
        testTokenizer(" \t\f ", new HtmlSymbol(0, 0, 0, " \t\f ", HtmlSymbolType.WhiteSpace));
    }

    @Test
    public void newlineIsRecognized() {
        testTokenizer(
                         "\n\r\r\n",
                         new HtmlSymbol(0, 0, 0, "\n", HtmlSymbolType.NewLine),
                         new HtmlSymbol(1, 1, 0, "\r", HtmlSymbolType.NewLine),
                         new HtmlSymbol(2, 2, 0, "\r\n", HtmlSymbolType.NewLine)
                     );
    }

    @Test
    public void transitionIsNotRecognizedMidTextIfSurroundedByAlphanumericCharacters() {
        testSingleToken("foo@bar", HtmlSymbolType.Text);
    }

    @Test
    public void openAngleIsRecognized() {
        testSingleToken("<", HtmlSymbolType.OpenAngle);
    }

    @Test
    public void bangIsRecognized() {
        testSingleToken("!", HtmlSymbolType.Bang);
    }

    @Test
    public void solidusIsRecognized() {
        testSingleToken("/", HtmlSymbolType.Solidus);
    }

    @Test
    public void questionMarkIsRecognized() {
        testSingleToken("?", HtmlSymbolType.QuestionMark);
    }

    @Test
    public void leftBracketIsRecognized() {
        testSingleToken("[", HtmlSymbolType.LeftBracket);
    }

    @Test
    public void closeAngleIsRecognized() {
        testSingleToken(">", HtmlSymbolType.CloseAngle);
    }

    @Test
    public void rightBracketIsRecognized() {
        testSingleToken("]", HtmlSymbolType.RightBracket);
    }

    @Test
    public void equalsIsRecognized() {
        testSingleToken("=", HtmlSymbolType.Equals);
    }

    @Test
    public void doubleQuoteIsRecognized() {
        testSingleToken("\"", HtmlSymbolType.DoubleQuote);
    }

    @Test
    public void singleQuoteIsRecognized() {
        testSingleToken("'", HtmlSymbolType.SingleQuote);
    }

    @Test
    public void transitionIsRecognized() {
        testSingleToken("@", HtmlSymbolType.Transition);
    }

    @Test
    public void doubleHyphenIsRecognized() {
        testSingleToken("--", HtmlSymbolType.DoubleHyphen);
    }

    @Test
    public void singleHyphenIsNotRecognized() {
        testSingleToken("-", HtmlSymbolType.Text);
    }

    @Test
    public void singleHyphenMidTextIsNotRecognizedAsSeparateToken() {
        testSingleToken("foo-bar", HtmlSymbolType.Text);
    }

    @Test
    public void nextIgnoresStarAtEOFInRazorComment() {
        testTokenizer(
                         "@* Foo * Bar * Baz *",
                         new HtmlSymbol(0, 0, 0, "@", HtmlSymbolType.RazorCommentTransition),
                         new HtmlSymbol(1, 0, 1, "*", HtmlSymbolType.RazorCommentStar),
                         new HtmlSymbol(2, 0, 2, " Foo * Bar * Baz *", HtmlSymbolType.RazorComment)
                     );
    }

    @Test
    public void nextIgnoresStarWithoutTrailingAt() {
        testTokenizer(
                         "@* Foo * Bar * Baz *@",
                         new HtmlSymbol(0, 0, 0, "@", HtmlSymbolType.RazorCommentTransition),
                         new HtmlSymbol(1, 0, 1, "*", HtmlSymbolType.RazorCommentStar),
                         new HtmlSymbol(2, 0, 2, " Foo * Bar * Baz ", HtmlSymbolType.RazorComment),
                         new HtmlSymbol(19, 0, 19, "*", HtmlSymbolType.RazorCommentStar),
                         new HtmlSymbol(20, 0, 20, "@", HtmlSymbolType.RazorCommentTransition)
                     );
    }

    @Test
    public void nextReturnsRazorCommentTokenForEntireRazorComment() {
        testTokenizer(
                         "@* Foo Bar Baz *@",
                         new HtmlSymbol(0, 0, 0, "@", HtmlSymbolType.RazorCommentTransition),
                         new HtmlSymbol(1, 0, 1, "*", HtmlSymbolType.RazorCommentStar),
                         new HtmlSymbol(2, 0, 2, " Foo Bar Baz ", HtmlSymbolType.RazorComment),
                         new HtmlSymbol(15, 0, 15, "*", HtmlSymbolType.RazorCommentStar),
                         new HtmlSymbol(16, 0, 16, "@", HtmlSymbolType.RazorCommentTransition)
                     );
    }


}
