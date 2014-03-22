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

import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import org.junit.Test;

public class JavaTokenizerCommentTest extends JavaTokenizerTestBase {

    @Test
    public void nextIgnoresStarAtEOFInRazorComment() {
        testTokenizer(
                         "@* Foo * Bar * Baz *",
                         new JavaSymbol(0, 0, 0, "@", JavaSymbolType.RazorCommentTransition),
                         new JavaSymbol(1, 0, 1, "*", JavaSymbolType.RazorCommentStar),
                         new JavaSymbol(2, 0, 2, " Foo * Bar * Baz *", JavaSymbolType.RazorComment)
                     );
    }

    @Test
    public void nextIgnoresStarWithoutTrailingAt() {
        testTokenizer(
                         "@* Foo * Bar * Baz *@",
                         new JavaSymbol(0, 0, 0, "@", JavaSymbolType.RazorCommentTransition),
                         new JavaSymbol(1, 0, 1, "*", JavaSymbolType.RazorCommentStar),
                         new JavaSymbol(2, 0, 2, " Foo * Bar * Baz ", JavaSymbolType.RazorComment),
                         new JavaSymbol(19, 0, 19, "*", JavaSymbolType.RazorCommentStar),
                         new JavaSymbol(20, 0, 20, "@", JavaSymbolType.RazorCommentTransition)
                     );
    }

    @Test
    public void nextReturnsRazorCommentTokenForEntireRazorComment() {
        testTokenizer(
                         "@* Foo Bar Baz *@",
                         new JavaSymbol(0, 0, 0, "@", JavaSymbolType.RazorCommentTransition),
                         new JavaSymbol(1, 0, 1, "*", JavaSymbolType.RazorCommentStar),
                         new JavaSymbol(2, 0, 2, " Foo Bar Baz ", JavaSymbolType.RazorComment),
                         new JavaSymbol(15, 0, 15, "*", JavaSymbolType.RazorCommentStar),
                         new JavaSymbol(16, 0, 16, "@", JavaSymbolType.RazorCommentTransition)
                     );
    }

    @Test
    public void nextReturnsCommentTokenForEntireSingleLineComment() {
        testTokenizer("// Foo Bar Baz", new JavaSymbol(0, 0, 0, "// Foo Bar Baz", JavaSymbolType.Comment));
    }

    @Test
    public void singleLineCommentIsTerminatedByNewline() {
        testTokenizer(
                         "// Foo Bar Baz\na", new JavaSymbol(0, 0, 0, "// Foo Bar Baz", JavaSymbolType.Comment),
                         getIgnoreRemaining()
                     );
    }

    @Test
    public void multiLineCommentInSingleLineCommentHasNoEffect() {
        testTokenizer(
                         "// Foo/*Bar*/ Baz\na", new JavaSymbol(0, 0, 0, "// Foo/*Bar*/ Baz", JavaSymbolType.Comment),
                         getIgnoreRemaining()
                     );
    }

    @Test
    public void nextReturnsCommentTokenForEntireMultiLineComment() {
        testTokenizer("/* Foo\nBar\nBaz */", new JavaSymbol(0, 0, 0, "/* Foo\nBar\nBaz */", JavaSymbolType.Comment));
    }

    @Test
    public void multiLineCommentIsTerminatedByEndSequence() {
        testTokenizer(
                         "/* Foo\nBar\nBaz */a", new JavaSymbol(0, 0, 0, "/* Foo\nBar\nBaz */", JavaSymbolType.Comment),
                         getIgnoreRemaining()
                     );
    }

    @Test
    public void unterminatedMultiLineCommentCapturesToEOF() {
        testTokenizer(
                         "/* Foo\nBar\nBaz", new JavaSymbol(0, 0, 0, "/* Foo\nBar\nBaz", JavaSymbolType.Comment),
                         getIgnoreRemaining()
                     );
    }

    @Test
    public void nestedMultiLineCommentsTerminatedAtFirstEndSequence() {
        testTokenizer(
                         "/* Foo/*\nBar\nBaz*/ */",
                         new JavaSymbol(0, 0, 0, "/* Foo/*\nBar\nBaz*/", JavaSymbolType.Comment), getIgnoreRemaining()
                     );
    }

    @Test
    public void nestedMultiLineCommentsTerminatedAtFullEndSequence() {
        testTokenizer(
                         "/* Foo\nBar\nBaz* */",
                         new JavaSymbol(0, 0, 0, "/* Foo\nBar\nBaz* */", JavaSymbolType.Comment), getIgnoreRemaining()
                     );
    }

}
