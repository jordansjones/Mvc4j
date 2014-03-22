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

package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.DirectiveBlock;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaReservedWordsTest extends JavaHtmlCodeParserTestBase {

    @Test
    public void reservedWords() {
        for (String word : new String[]{"namespace", "class", "package"}) {
            runReservedWordsTest(word);
        }
    }

    @Test
    public void reservedWordsAreCaseSensitive() {
        for (String word : new String[]{
                                           "Namespace", "Class", "NAMESPACE", "CLASS", "nameSpace", "NameSpace",
                                           "Package", "PACKage", "PACKAGE"
        }) {
            runReservedWordsAreCaseSensitiveTest(word);
        }
    }

    private void runReservedWordsTest(final String word) {
        parseBlockTest(
                          word,
                          new DirectiveBlock(
                                                factory().metaCode(word).accepts(AcceptedCharacters.None)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorReservedWord(word),
                                            SourceLocation.Zero
                          )
                      );
    }

    private void runReservedWordsAreCaseSensitiveTest(final String word) {
        parseBlockTest(
                          word,
                          new ExpressionBlock(
                                                 factory().code(word)
                                                          .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                          .accepts(AcceptedCharacters.NonWhiteSpace)
                          )
                      );
    }
}
