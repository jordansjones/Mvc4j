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
import nextmethod.web.razor.generator.MarkupCodeGenerator;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import org.junit.Test;

public class JavaNestedStatementsTest extends JavaHtmlCodeParserTestBase {

    @Test
    public void nestedSimpleStatement() {
        parseBlockTest(
                          "@while(true) { foo(); }",
                          new StatementBlock(
                                                factory().codeTransition(),
                                                factory().code("while(true) { foo(); }")
                                                         .asStatement()
                                                         .accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void nestedKeywordStatement() {
        parseBlockTest(
                          "@while(true) { for(int i = 0; i < 10; i++) { foo(); } }",
                          new StatementBlock(
                                                factory().codeTransition(),
                                                factory().code("while(true) { for(int i = 0; i < 10; i++) { foo(); } }")
                                                         .asStatement()
                                                         .accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void nestedCodeBlock() {
        parseBlockTest(
                          "@while(true) { { { { foo(); } } } }",
                          new StatementBlock(
                                                factory().codeTransition(),
                                                factory().code("while(true) { { { { foo(); } } } }")
                                                         .asStatement()
                                                         .accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void nestedImplicitExpression() {
        parseBlockTest(
                          "@while(true) { @foo }",
                          new StatementBlock(
                                                factory().codeTransition(),
                                                factory().code("while(true) { ").asStatement(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().code("foo")
                                                                                .asImplicitExpression(
                                                                                                         JavaCodeParser.DefaultKeywords,
                                                                                                         true
                                                                                                     )
                                                                                .accepts(AcceptedCharacters.NonWhiteSpace)
                                                ),
                                                factory().code(" }")
                                                         .asStatement()
                                                         .accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void nestedExplicitExpression() {
        parseBlockTest(
                          "@while(true) { @(foo) }",
                          new StatementBlock(
                                                factory().codeTransition(),
                                                factory().code("while(true) { ").asStatement(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().metaCode("(")
                                                                                .accepts(AcceptedCharacters.None),
                                                                       factory().code("foo")
                                                                                .asExpression(),
                                                                       factory().metaCode(")")
                                                                                .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().code(" }")
                                                         .asStatement()
                                                         .accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void nestedMarkupBlock() {
        parseBlockTest(
                          "@while(true) { <p>Hello</p> }",
                          new StatementBlock(
                                                factory().codeTransition(),
                                                factory().code("while(true) {").asStatement(),
                                                new MarkupBlock(
                                                                   factory().markup(" <p>Hello</p> ")
                                                                            .with(new MarkupCodeGenerator())
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().code("}")
                                                         .asStatement()
                                                         .accepts(AcceptedCharacters.None)
                          )
                      );
    }

}
