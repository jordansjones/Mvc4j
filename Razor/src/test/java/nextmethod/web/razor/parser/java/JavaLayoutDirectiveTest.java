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

import nextmethod.base.Strings;
import nextmethod.web.razor.editor.EditorHints;
import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.generator.SetLayoutCodeGenerator;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.DirectiveBlock;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import org.junit.Test;


public class JavaLayoutDirectiveTest extends JavaHtmlCodeParserTestBase {

    @Test
    public void layoutKeywordIsCaseSensitive() {
        for (String keyword : new String[]{"Layout", "LAYOUT", "layOut", "LayOut"}) {
            runLayoutKeywordIsCaseSensitive(keyword);
        }
    }

    @Test
    public void layoutDirectiveAcceptsAllTextToEndOfLine() {
        parseBlockTest(
                          "@layout Foo Bar Baz",
                          new DirectiveBlock(
                                                factory().codeTransition(),
                                                factory().metaCode("layout ").accepts(AcceptedCharacters.None),
                                                factory().metaCode("Foo Bar Baz")
                                                         .with(new SetLayoutCodeGenerator("Foo Bar Baz"))
                                                         .withEditorHints(
                                                                             EditorHints.VirtualPath,
                                                                             EditorHints.LayoutPage
                                                                         )
                          )
                      );
    }

    @Test
    public void layoutDirectiveAcceptsAnyIfNoWhitespaceFollowingLayoutKeyword() {
        parseBlockTest(
                          "@layout",
                          new DirectiveBlock(
                                                factory().codeTransition(),
                                                factory().metaCode("layout")
                          )
                      );
    }

    @Test
    public void layoutDirectiveOutputsMarkerSpanIfAnyWhitespaceAfterLayoutKeyword() {
        parseBlockTest(
                          "@layout ",
                          new DirectiveBlock(
                                                factory().codeTransition(),
                                                factory().metaCode("layout ").accepts(AcceptedCharacters.None),
                                                factory().emptyJava()
                                                         .asMetaCode()
                                                         .with(new SetLayoutCodeGenerator(Strings.Empty))
                                                         .withEditorHints(
                                                                             EditorHints.VirtualPath,
                                                                             EditorHints.LayoutPage
                                                                         )
                          )
                      );
    }

    @Test
    public void layoutDirectiveAcceptsTrailingNewlineButDoesNotIncludeItInLayoutPath() {
        parseBlockTest(
                          "@layout Foo" + Environment.NewLine,
                          new DirectiveBlock(
                                                factory().codeTransition(),
                                                factory().metaCode("layout ").accepts(AcceptedCharacters.None),
                                                factory().metaCode("Foo\r\n")
                                                         .with(new SetLayoutCodeGenerator("Foo"))
                                                         .accepts(AcceptedCharacters.None)
                                                         .withEditorHints(
                                                                             EditorHints.VirtualPath,
                                                                             EditorHints.LayoutPage
                                                                         )
                          )
                      );
    }

    @Test
    public void layoutDirectiveCorrectlyRestoresContextAfterCompleting() {
        parseDocumentTest(
                             "@layout Foo" + Environment.NewLine + "@foo",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new DirectiveBlock(
                                                                      factory().codeTransition(),
                                                                      factory().metaCode("layout ")
                                                                               .accepts(AcceptedCharacters.None),
                                                                      factory().metaCode("Foo\r\n")
                                                                               .with(new SetLayoutCodeGenerator("Foo"))
                                                                               .accepts(AcceptedCharacters.None)
                                                                               .withEditorHints(
                                                                                                   EditorHints.VirtualPath,
                                                                                                   EditorHints.LayoutPage
                                                                                               )
                                                ),
                                                factory().emptyHtml(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().code("foo")
                                                                                .asImplicitExpression(
                                                                                                         JavaCodeParser.DefaultKeywords,
                                                                                                         false
                                                                                                     )
                                                                                .accepts(AcceptedCharacters.NonWhiteSpace)
                                                ),
                                                factory().emptyHtml()
                             )
                         );
    }

    private void runLayoutKeywordIsCaseSensitive(final String word) {
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
