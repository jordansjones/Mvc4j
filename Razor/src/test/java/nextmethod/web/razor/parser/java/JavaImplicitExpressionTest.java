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
import nextmethod.web.razor.parser.ParserBase;
import nextmethod.web.razor.parser.SyntaxConstants;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaImplicitExpressionTest extends JavaHtmlCodeParserTestBase {

    private static final String TestExtraKeyword = "model";

    @Override
    public ParserBase createCodeParser() {
        return new JavaCodeParser();
    }

    @Test
    public void nestedImplicitExpression() {
        parseBlockTest(
                          "if (true) { @foo }",
                          new StatementBlock(
                                                factory().code("if (true) { ").asStatement(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().code("foo")
                                                                                .asImplicitExpression(
                                                                                                         JavaCodeParser.DefaultKeywords,
                                                                                                         true
                                                                                                     )
                                                                                .accepts(AcceptedCharacters.NonWhiteSpace)
                                                ),
                                                factory().code(" }").asStatement()
                          )
                      );
    }

    @Test
    public void parseBlockAcceptsNonEnglishCharactersThatAreValidIdentifiers() {
        implicitExpressionTest("हळूँजद॔.", "हळूँजद॔");
    }

    @Test
    public void parseBlockOutputsZeroLengthCodeSpanIfInvalidCharacterFollowsTransition() {
        parseBlockTest(
                          "@/",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().emptyJava()
                                                          .asImplicitExpression(getKeywordSet())
                                                          .accepts(AcceptedCharacters.NonWhiteSpace)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorUnexpectedCharacterAtStartOfCodeBlock("/"),
                                            new SourceLocation(1, 0, 1)
                          )
                      );
    }

    @Test
    public void parseBlockOutputsZeroLengthCodeSpanIfEOFOccursAfterTransition() {
        parseBlockTest(
                          "@",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().emptyJava()
                                                          .asImplicitExpression(getKeywordSet())
                                                          .accepts(AcceptedCharacters.NonWhiteSpace)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorUnexpectedEndOfFileAtStartOfCodeBlock(),
                                            new SourceLocation(1, 0, 1)
                          )
                      );
    }

    @Test
    public void parseBlockSupportsSlashesWithinComplexImplicitExpressions() {
        implicitExpressionTest("DataGridColumn.Template(\"Years of Service\", e => (int)Math.Round((DateTime.Now - dt).TotalDays / 365))");
    }

    @Test
    public void parseBlockMethodParsesSingleIdentifierAsImplicitExpression() {
        implicitExpressionTest("foo");
    }

    @Test
    public void parseBlockMethodDoesNotAcceptSemicolonIfExpressionTerminatedByWhitespace() {
        implicitExpressionTest("foo ;", "foo");
    }

    @Test
    public void parseBlockMethodIgnoresSemicolonAtEndOfSimpleImplicitExpression() {
        runTrailingSemicolonTest("foo");
    }

    @Test
    public void parseBlockMethodParsesDottedIdentifiersAsImplicitExpression() {
        implicitExpressionTest("foo.bar.baz");
    }

    @Test
    public void parseBlockMethodIgnoresSemicolonAtEndOfDottedIdentifiers() {
        runTrailingSemicolonTest("foo.bar.baz");
    }

    @Test
    public void parseBlockMethodDoesNotIncludeDotAtEOFInImplicitExpression() {
        implicitExpressionTest("foo.bar.", "foo.bar");
    }

    @Test
    public void parseBlockMethodDoesNotIncludeDotFollowedByInvalidIdentifierCharacterInImplicitExpression() {
        implicitExpressionTest("foo.bar.0", "foo.bar");
        implicitExpressionTest("foo.bar.</p>", "foo.bar");
    }

    @Test
    public void parseBlockMethodDoesNotIncludeSemicolonAfterDot() {
        implicitExpressionTest("foo.bar.;", "foo.bar");
    }

    @Test
    public void parseBlockMethodTerminatesAfterIdentifierUnlessFollowedByDotOrParenInImplicitExpression() {
        implicitExpressionTest("foo.bar</p>", "foo.bar");
    }

    @Test
    public void parseBlockProperlyParsesParenthesesAndBalancesThemInImplicitExpression() {
        implicitExpressionTest("foo().bar(\"bi\\\"z\", 4)(\"chained method; call\").baz(@\"bo\"\"z\", '\\'', () => { return 4; }, (4+5+new { foo = bar[4] }))");
    }

    @Test
    public void parseBlockProperlyParsesBracketsAndBalancesThemInImplicitExpression() {
        implicitExpressionTest("foo.bar[4 * (8 + 7)][\"fo\\\"o\"].baz");
    }

    @Test
    public void parseBlockTerminatesImplicitExpressionAtHtmlEndTag() {
        implicitExpressionTest("foo().bar.baz</p>zoop", "foo().bar.baz");
    }

    @Test
    public void parseBlockTerminatesImplicitExpressionAtHtmlStartTag() {
        implicitExpressionTest("foo().bar.baz<p>zoop", "foo().bar.baz");
    }

    @Test
    public void parseBlockTerminatesImplicitExpressionBeforeDotIfDotNotFollowedByIdentifierStartCharacter() {
        implicitExpressionTest("foo().bar.baz.42", "foo().bar.baz");
    }

    @Test
    public void parseBlockStopsBalancingParenthesesAtEOF() {
        implicitExpressionTest(
                                  "foo(()",
                                  "foo(()",
                                  AcceptedCharacters.Any,
                                  new RazorError(
                                                    RazorResources().parseErrorExpectedCloseBracketBeforeEof("(", ")"),
                                                    new SourceLocation(4, 0, 4)
                                  )
                              );
    }

    @Test
    public void parseBlockTerminatesImplicitExpressionIfCloseParenFollowedByAnyWhiteSpace() {
        implicitExpressionTest("foo.bar() (baz)", "foo.bar()");
    }

    @Test
    public void parseBlockTerminatesImplicitExpressionIfIdentifierFollowedByAnyWhiteSpace() {
        implicitExpressionTest("foo .bar() (baz)", "foo");
    }

    @Test
    public void parseBlockTerminatesImplicitExpressionAtLastValidPointIfDotFollowedByWhitespace() {
        implicitExpressionTest("foo. bar() (baz)", "foo");
    }

    @Test
    public void parseBlockOutputExpressionIfModuleTokenNotFollowedByBrace() {
        implicitExpressionTest("module.foo()");
    }


    private void runTrailingSemicolonTest(final String expr) {
        parseBlockTest(
                          SyntaxConstants.TransitionString + expr + ";",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().code(expr)
                                                          .asImplicitExpression(getKeywordSet())
                                                          .accepts(AcceptedCharacters.NonWhiteSpace)
                          )
                      );
    }
}
