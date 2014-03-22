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

import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaVerbatimBlockTest extends JavaHtmlCodeParserTestBase {

    @Test
    public void verbatimBlock() {
        parseBlockTest(
                          "@{ foo(); }",
                          new StatementBlock(
                                                factory().codeTransition(),
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().code(" foo(); ").asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void innerImplicitExpressionWithOnlySingleAtOutputsZeroLengthCodeSpan() {
        parseBlockTest(
                          "{@}",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().emptyJava().asStatement(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().emptyJava()
                                                                                .asImplicitExpression(
                                                                                                         getKeywordSet(),
                                                                                                         true
                                                                                                     )
                                                                                .accepts(AcceptedCharacters.NonWhiteSpace)
                                                ),
                                                factory().emptyJava().asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          ),
                          true,
                          new RazorError(
                                            RazorResources().parseErrorUnexpectedCharacterAtStartOfCodeBlock("}"),
                                            2, 0, 2
                          )
                      );
    }

    @Test
    public void innerImplicitExpressionDoesNotAcceptDotAfterAt() {
        parseBlockTest(
                          "{@.}",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().emptyJava().asStatement(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().emptyJava()
                                                                                .asImplicitExpression(
                                                                                                         getKeywordSet(),
                                                                                                         true
                                                                                                     )
                                                                                .accepts(AcceptedCharacters.NonWhiteSpace)
                                                ),
                                                factory().code(".").asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          ),
                          true,
                          new RazorError(
                                            RazorResources().parseErrorUnexpectedCharacterAtStartOfCodeBlock("."),
                                            2, 0, 2
                          )
                      );
    }

    @Test
    public void innerImplicitExpressionWithOnlySingleAtAcceptsSingleSpaceOrNewlineAtDesignTime() {
        parseBlockTest(
                          "{" + Environment.NewLine + "    @" + Environment.NewLine + "}",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().code("\r\n    ").asStatement(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().emptyJava()
                                                                                .asImplicitExpression(
                                                                                                         getKeywordSet(),
                                                                                                         true
                                                                                                     )
                                                                                .accepts(AcceptedCharacters.NonWhiteSpace)
                                                ),
                                                factory().code("\r\n").asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          ),
                          true,
                          new RazorError(
                                            RazorResources().parseErrorUnexpectedWhiteSpaceAtStartOfCodeBlock(),
                                            8, 1, 5
                          )
                      );
    }

    @Test
    public void innerImplicitExpressionDoesNotAcceptTrailingNewlineInRunTimeMode() {
        parseBlockTest(
                          "{@foo." + Environment.NewLine + "}",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().emptyJava().asStatement(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().code("foo.")
                                                                                .asImplicitExpression(
                                                                                                         getKeywordSet(),
                                                                                                         true
                                                                                                     )
                                                                                .accepts(AcceptedCharacters.NonWhiteSpace)
                                                ),
                                                factory().code("\r\n").asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void innerImplicitExpressionAcceptsTrailingNewlineInDesignTimeMode() {
        parseBlockTest(
                          "{@foo." + Environment.NewLine + "}",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().emptyJava().asStatement(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().code("foo.")
                                                                                .asImplicitExpression(
                                                                                                         getKeywordSet(),
                                                                                                         true
                                                                                                     )
                                                                                .accepts(AcceptedCharacters.NonWhiteSpace)
                                                ),
                                                factory().code("\r\n").asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          ),
                          true
                      );
    }

}
