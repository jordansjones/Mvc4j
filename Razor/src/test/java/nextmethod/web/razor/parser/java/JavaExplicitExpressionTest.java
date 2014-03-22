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
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class JavaExplicitExpressionTest extends JavaHtmlCodeParserTestBase {

    @Test
    public void parseBlockShouldOutputZeroLengthCodeSpanIfExplicitExpressionIsEmpty() {
        parseBlockTest(
                          "@()",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().emptyJava().asExpression(),
                                                 factory().metaCode(")").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockShouldOutputZeroLengthCodeSpanIfEOFOccursAfterStartOfExplicitExpression() {
        parseBlockTest(
                          "@(",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().emptyJava().asExpression()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      RazorResources().blockNameExplicitExpression(),
                                                                                                      ")",
                                                                                                      "("
                                                                                                  ),
                                            new SourceLocation(1, 0, 1)
                          )
                      );
    }

    @Test
    public void parseBlockShouldAcceptEscapedQuoteInNonVerbatimStrings() {
        parseBlockTest(
                          "@(\"\\\"\")",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().code("\"\\\"\"").asExpression(),
                                                 factory().metaCode(")").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockShouldAcceptEscapedQuoteInVerbatimStrings() {
        parseBlockTest(
                          "@(@\"\"\"\")",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().code("@\"\"\"\"").asExpression(),
                                                 factory().metaCode(")").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockShouldAcceptMultipleRepeatedEscapedQuoteInVerbatimStrings() {
        parseBlockTest(
                          "@(@\"\"\"\"\"\")",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().code("@\"\"\"\"\"\"").asExpression(),
                                                 factory().metaCode(")").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockShouldAcceptMultiLineVerbatimStrings() {
        parseBlockTest(
                          "@(@\"" + Environment.NewLine
                          + "Foo" + Environment.NewLine
                          + "Bar" + Environment.NewLine
                          + "Baz" + Environment.NewLine
                          + "\")",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().code("@\"\r\nFoo\r\nBar\r\nBaz\r\n\"").asExpression(),
                                                 factory().metaCode(")").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockShouldAcceptMultipleEscapedQuotesInNonVerbatimStrings() {
        parseBlockTest(
                          "@(\"\\\"hello, world\\\"\")",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().code("\"\\\"hello, world\\\"\"").asExpression(),
                                                 factory().metaCode(")").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockShouldAcceptMultipleEscapedQuotesInVerbatimStrings() {
        parseBlockTest(
                          "@(@\"\"\"hello, world\"\"\")",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().code("@\"\"\"hello, world\"\"\"").asExpression(),
                                                 factory().metaCode(")").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockShouldAcceptConsecutiveEscapedQuotesInNonVerbatimStrings() {
        parseBlockTest(
                          "@(\"\\\"\\\"\")",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().code("\"\\\"\\\"\"").asExpression(),
                                                 factory().metaCode(")").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockShouldAcceptConsecutiveEscapedQuotesInVerbatimStrings() {
        parseBlockTest(
                          "@(@\"\"\"\"\"\")",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().code("@\"\"\"\"\"\"").asExpression(),
                                                 factory().metaCode(")").accepts(AcceptedCharacters.None)
                          )
                      );
    }
}
