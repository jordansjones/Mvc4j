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

import nextmethod.web.razor.editor.SingleLineMarkupEditHandler;
import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.JavaLanguageCharacteristics;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.parser.syntaxtree.TemplateBlock;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class JavaTemplateTest extends JavaHtmlCodeParserTestBase {

    private static final String testTemplateCode = " @<p>Foo #@item</p>";

    private TemplateBlock testTemplate() {
        return new TemplateBlock(
                                    new MarkupBlock(
                                                       factory().markupTransition(),
                                                       factory().markup("<p>Foo #"),
                                                       new ExpressionBlock(
                                                                              factory().codeTransition(),
                                                                              factory().code("item")
                                                                                       .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                       .accepts(AcceptedCharacters.NonWhiteSpace)
                                                       ),
                                                       factory().markup("</p>").accepts(AcceptedCharacters.None)
                                    )
        );
    }

    private static final String testNestedTemplateCode = " @<p>Foo #@Html.Repeat(10, @<p>@item</p>)</p>";

    private TemplateBlock testNestedTemplate() {
        return new TemplateBlock(
                                    new MarkupBlock(
                                                       factory().markupTransition(),
                                                       factory().markup("<p>Foo #"),
                                                       new ExpressionBlock(
                                                                              factory().codeTransition(),
                                                                              factory().code("Html.Repeat(10, ")
                                                                                       .asImplicitExpression(JavaCodeParser.DefaultKeywords),
                                                                              new TemplateBlock(
                                                                                                   new MarkupBlock(
                                                                                                                      factory()
                                                                                                                          .markupTransition(),
                                                                                                                      factory()
                                                                                                                          .markup("<p>"),
                                                                                                                      new ExpressionBlock(
                                                                                                                                             factory()
                                                                                                                                                 .codeTransition(),
                                                                                                                                             factory()
                                                                                                                                                 .code("item")
                                                                                                                                                 .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                                                                                 .accepts(AcceptedCharacters.NonWhiteSpace)
                                                                                                                      ),
                                                                                                                      factory()
                                                                                                                          .markup("</p>")
                                                                                                                          .accepts(AcceptedCharacters.None)
                                                                                                   )
                                                                              ),
                                                                              factory().code(")")
                                                                                       .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                       .accepts(AcceptedCharacters.NonWhiteSpace)
                                                       ),
                                                       factory().markup("</p>").accepts(AcceptedCharacters.None)
                                    )
        );
    }

    @Test
    public void parseBlockHandlesSingleLineTemplate() {
        parseBlockTest(
                          "{ var foo = @: bar" + Environment.NewLine + "; }",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().code(" var foo = ").asStatement(),
                                                new TemplateBlock(
                                                                     new MarkupBlock(
                                                                                        factory().markupTransition(),
                                                                                        factory().metaMarkup(
                                                                                                                ":",
                                                                                                                HtmlSymbolType.Colon
                                                                                                            ),
                                                                                        factory().markup(
                                                                                                            " bar" +
                                                                                                            Environment.NewLine
                                                                                                        )
                                                                                                 .with(
                                                                                                          new SingleLineMarkupEditHandler(
                                                                                                                                             JavaLanguageCharacteristics.Instance
                                                                                                                                                 .createTokenizeStringDelegate()
                                                                                                          )
                                                                                                      )
                                                                                                 .accepts(AcceptedCharacters.None)
                                                                     )
                                                ),
                                                factory().code("; ").asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockHandlesSingleLineImmediatelyFollowingStatementChar() {
        parseBlockTest(
                          "{i@: bar" + Environment.NewLine + "}",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().code("i").asStatement(),
                                                new TemplateBlock(
                                                                     new MarkupBlock(
                                                                                        factory().markupTransition(),
                                                                                        factory().metaMarkup(
                                                                                                                ":",
                                                                                                                HtmlSymbolType.Colon
                                                                                                            ),
                                                                                        factory().markup(
                                                                                                            " bar" +
                                                                                                            Environment.NewLine
                                                                                                        )
                                                                                                 .with(
                                                                                                          new SingleLineMarkupEditHandler(
                                                                                                                                             JavaLanguageCharacteristics.Instance
                                                                                                                                                 .createTokenizeStringDelegate()
                                                                                                          )
                                                                                                      )
                                                                                                 .accepts(AcceptedCharacters.None)
                                                                     )
                                                ),
                                                factory().emptyJava().asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockHandlesSimpleTemplateInExplicitExpresionParens() {
        parseBlockTest(
                          "(Html.Repeat(10," + testTemplateCode + "))",
                          new ExpressionBlock(
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().code("Html.Repeat(10, ").asExpression(),
                                                 testTemplate(),
                                                 factory().code(")").asExpression(),
                                                 factory().metaCode(")").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockHandlesSimpleTemplateInImplicitExpressionParens() {
        parseBlockTest(
                          "Html.Repeat(10," + testTemplateCode + ")",
                          new ExpressionBlock(
                                                 factory().code("Html.Repeat(10, ")
                                                          .asImplicitExpression(JavaCodeParser.DefaultKeywords),
                                                 testTemplate(),
                                                 factory().code(")")
                                                          .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                          .accepts(AcceptedCharacters.NonWhiteSpace)
                          )
                      );
    }

    @Test
    public void parseBlockProducesErrorButCorrectlyParsesNestedTemplateInImplicitExpressionParens() {
        parseBlockTest(
                          "Html.Repeat(10," + testNestedTemplateCode + ")",
                          new ExpressionBlock(
                                                 factory().code("Html.Repeat(10, ")
                                                          .asImplicitExpression(JavaCodeParser.DefaultKeywords),
                                                 testNestedTemplate(),
                                                 factory().code(")")
                                                          .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                          .accepts(AcceptedCharacters.NonWhiteSpace)
                          ),
                          getNestedTemplateError(42)
                      );
    }

    @Test
    public void parseBlockHandlesSimpleTemplateInStatementWithinCodeBlock() {
        parseBlockTest(
                          "foreach(foo in Bar) { Html.ExecuteTemplate(foo," + testTemplateCode + "); }",
                          new StatementBlock(
                                                factory().code("foreach(foo in Bar) { Html.ExecuteTemplate(foo, ")
                                                         .asStatement(),
                                                testTemplate(),
                                                factory().code("); }")
                                                         .asStatement()
                                                         .accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockHandlesTwoTemplatesInStatementWithinCodeBlock() {
        parseBlockTest(
                          "foreach(foo in Bar) { Html.ExecuteTemplate(foo," + testTemplateCode + "," +
                          testTemplateCode + "); }",
                          new StatementBlock(
                                                factory().code("foreach(foo in Bar) { Html.ExecuteTemplate(foo, ")
                                                         .asStatement(),
                                                testTemplate(),
                                                factory().code(", ").asStatement(),
                                                testTemplate(),
                                                factory().code("); }")
                                                         .asStatement()
                                                         .accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockProducesErrorButCorrectlyParsesNestedTemplateInStatementWithinCodeBlock() {
        parseBlockTest(
                          "foreach(foo in Bar) { Html.ExecuteTemplate(foo," + testNestedTemplateCode + "); }",
                          new StatementBlock(
                                                factory().code("foreach(foo in Bar) { Html.ExecuteTemplate(foo, ")
                                                         .asStatement(),
                                                testNestedTemplate(),
                                                factory().code("); }")
                                                         .asStatement()
                                                         .accepts(AcceptedCharacters.None)
                          ),
                          getNestedTemplateError(74)
                      );
    }

    @Test
    public void parseBlockHandlesSimpleTemplateInStatementWithinStatementBlock() {
        parseBlockTest(
                          "{ var foo = bar; Html.ExecuteTemplate(foo," + testTemplateCode + "); }",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().code(" var foo = bar; Html.ExecuteTemplate(foo, ")
                                                         .asStatement(),
                                                testTemplate(),
                                                factory().code("); ").asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockHandlessTwoTemplatesInStatementWithinStatementBlock() {
        parseBlockTest(
                          "{ var foo = bar; Html.ExecuteTemplate(foo," + testTemplateCode + "," + testTemplateCode +
                          "); }",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().code(" var foo = bar; Html.ExecuteTemplate(foo, ")
                                                         .asStatement(),
                                                testTemplate(),
                                                factory().code(", ").asStatement(),
                                                testTemplate(),
                                                factory().code("); ").asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockProducesErrorButCorrectlyParsesNestedTemplateInStatementWithinStatementBlock() {
        parseBlockTest(
                          "{ var foo = bar; Html.ExecuteTemplate(foo," + testNestedTemplateCode + "); }",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().code(" var foo = bar; Html.ExecuteTemplate(foo, ")
                                                         .asStatement(),
                                                testNestedTemplate(),
                                                factory().code("); ").asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          ),
                          getNestedTemplateError(69)
                      );
    }

    private static RazorError getNestedTemplateError(int charIndex) {
        return new RazorError(
                                 RazorResources().parseErrorInlineMarkupBlocksCannotBeNested(),
                                 new SourceLocation(charIndex, 0, charIndex)
        );
    }
}
