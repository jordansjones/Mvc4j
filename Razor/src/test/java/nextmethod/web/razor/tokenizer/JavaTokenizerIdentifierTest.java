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

import nextmethod.web.razor.tokenizer.symbols.JavaKeyword;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import org.junit.Test;

public class JavaTokenizerIdentifierTest extends JavaTokenizerTestBase {

    @Test
    public void simpleIdentifierIsRecognized() {
        testTokenizer("foo", new JavaSymbol(0, 0, 0, "foo", JavaSymbolType.Identifier));
    }

    @Test
    public void identifierStartingWithUnderscoreIsRecognized() {
        testTokenizer("_foo", new JavaSymbol(0, 0, 0, "_foo", JavaSymbolType.Identifier));
    }

    @Test
    public void identifierCanContainDigits() {
        testTokenizer("foo4", new JavaSymbol(0, 0, 0, "foo4", JavaSymbolType.Identifier));
    }

    @Test
    public void identifierCanStartWithTitlecaseLetter() {
        testTokenizer("ῼfoo", new JavaSymbol(0, 0, 0, "ῼfoo", JavaSymbolType.Identifier));
    }

    @Test
    public void identifierCanStartWithLetterModifier() {
        testTokenizer("ᵊfoo", new JavaSymbol(0, 0, 0, "ᵊfoo", JavaSymbolType.Identifier));
    }

    @Test
    public void identifierCanStartWithOtherLetter() {
        testTokenizer("ƻfoo", new JavaSymbol(0, 0, 0, "ƻfoo", JavaSymbolType.Identifier));
    }

    @Test
    public void identifierCanStartWithNumberLetter() {
        testTokenizer("Ⅽool", new JavaSymbol(0, 0, 0, "Ⅽool", JavaSymbolType.Identifier));
    }

    @Test
    public void identifierCanContainNonSpacingMark() {
        testTokenizer("foo\u0300", new JavaSymbol(0, 0, 0, "foo\u0300", JavaSymbolType.Identifier));
    }

    @Test
    public void identifierCanContainSpacingCombiningMark() {
        testTokenizer("fooः", new JavaSymbol(0, 0, 0, "fooः", JavaSymbolType.Identifier));
    }

    @Test
    public void identifierCanContainNonEnglishDigit() {
        testTokenizer("foo١", new JavaSymbol(0, 0, 0, "foo١", JavaSymbolType.Identifier));
    }

    @Test
    public void identifierCanContainConnectorPunctuation() {
        testTokenizer("foo‿bar", new JavaSymbol(0, 0, 0, "foo‿bar", JavaSymbolType.Identifier));
    }

    @Test
    public void identifierCanContainFormatCharacter() {
        testTokenizer("foo؃bar", new JavaSymbol(0, 0, 0, "foo؃bar", JavaSymbolType.Identifier));
    }

    @Test
    public void keywordsAreRecognizedAsKeywordTokens() {
        for (JavaKeyword keyword : JavaKeyword.values()) {
            testKeyword(keyword.keyword(), keyword);
        }
    }

    private void testKeyword(@Nonnull final String keyword, final JavaKeyword keywordType) {
        final JavaSymbol javaSymbol = new JavaSymbol(0, 0, 0, keyword, JavaSymbolType.Keyword);
        javaSymbol.setKeyword(keywordType);
        testTokenizer(keyword, javaSymbol);
    }
}
