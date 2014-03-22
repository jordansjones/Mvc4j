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
import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.CommentBlock;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaRazorCommentsTest extends JavaHtmlMarkupParserTestBase {

    @Test
    public void unterminatedRazorComment() {
        parseDocumentTest(
                             "@*",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new CommentBlock(
                                                                    factory().markupTransition(HtmlSymbolType.RazorCommentTransition)
                                                                             .accepts(AcceptedCharacters.None),
                                                                    factory().metaMarkup(
                                                                                            "*",
                                                                                            HtmlSymbolType.RazorCommentStar
                                                                                        )
                                                                             .accepts(AcceptedCharacters.None),
                                                                    factory().span(
                                                                                      SpanKind.Comment,
                                                                                      new HtmlSymbol(
                                                                                                        factory().getLocationTracker()
                                                                                                                 .getCurrentLocation(),
                                                                                                        Strings.Empty,
                                                                                                        HtmlSymbolType.Unknown
                                                                                      )
                                                                                  ).accepts(AcceptedCharacters.Any)
                                                )
                             ),
                             new RazorError(
                                               RazorResources().parseErrorRazorCommentNotTerminated(),
                                               0, 0, 0
                             )
                         );
    }

    @Test
    public void emptyRazorComment() {
        parseDocumentTest(
                             "@**@",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new CommentBlock(
                                                                    factory().markupTransition(HtmlSymbolType.RazorCommentTransition)
                                                                             .accepts(AcceptedCharacters.None),
                                                                    factory().metaMarkup(
                                                                                            "*",
                                                                                            HtmlSymbolType.RazorCommentStar
                                                                                        )
                                                                             .accepts(AcceptedCharacters.None),
                                                                    factory().span(
                                                                                      SpanKind.Comment,
                                                                                      new HtmlSymbol(
                                                                                                        factory().getLocationTracker()
                                                                                                                 .getCurrentLocation(),
                                                                                                        Strings.Empty,
                                                                                                        HtmlSymbolType.Unknown
                                                                                      )
                                                                                  ).accepts(AcceptedCharacters.Any),
                                                                    factory().metaMarkup(
                                                                                            "*",
                                                                                            HtmlSymbolType.RazorCommentStar
                                                                                        )
                                                                             .accepts(AcceptedCharacters.None),
                                                                    factory().markupTransition(HtmlSymbolType.RazorCommentTransition)
                                                                             .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().emptyHtml()
                             )
                         );
    }

    @Test
    public void razorCommentInImplicitExpressionMethodCall() {
        parseDocumentTest(
                             "@foo(" + Environment.NewLine + "@**@" + Environment.NewLine,
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().code("foo(" + Environment.NewLine)
                                                                                .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                       ,
                                                                       new CommentBlock(
                                                                                           factory().codeTransition(JavaSymbolType.RazorCommentTransition)
                                                                                                    .accepts(AcceptedCharacters.None),
                                                                                           factory().metaCode(
                                                                                                                 "*",
                                                                                                                 JavaSymbolType.RazorCommentStar
                                                                                                             )
                                                                                                    .accepts(AcceptedCharacters.None),
                                                                                           factory().span(
                                                                                                             SpanKind.Comment,
                                                                                                             new JavaSymbol(
                                                                                                                               factory()
                                                                                                                                   .getLocationTracker()
                                                                                                                                   .getCurrentLocation(),
                                                                                                                               Strings.Empty,
                                                                                                                               JavaSymbolType.Unknown
                                                                                                             )
                                                                                                         )
                                                                                                    .accepts(AcceptedCharacters.Any),
                                                                                           factory().metaCode(
                                                                                                                 "*",
                                                                                                                 JavaSymbolType.RazorCommentStar
                                                                                                             )
                                                                                                    .accepts(AcceptedCharacters.None),
                                                                                           factory().codeTransition(JavaSymbolType.RazorCommentTransition)
                                                                                                    .accepts(AcceptedCharacters.None)
                                                                       ),
                                                                       factory().code(Environment.NewLine)
                                                                                .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                )
                             ),
                             new RazorError(
                                               RazorResources().parseErrorExpectedCloseBracketBeforeEof("(", ")"),
                                               4, 0, 4
                             )
                         );
    }

    @Test
    public void unterminatedRazorCommentInImplicitExpressionMethodCall() {
        parseDocumentTest(
                             "@foo(@*",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().code("foo(")
                                                                                .asImplicitExpression(JavaCodeParser.DefaultKeywords),
                                                                       new CommentBlock(
                                                                                           factory().codeTransition(JavaSymbolType.RazorCommentTransition)
                                                                                                    .accepts(AcceptedCharacters.None),
                                                                                           factory().metaCode(
                                                                                                                 "*",
                                                                                                                 JavaSymbolType.RazorCommentStar
                                                                                                             )
                                                                                                    .accepts(AcceptedCharacters.None),
                                                                                           factory().span(
                                                                                                             SpanKind.Comment,
                                                                                                             new JavaSymbol(
                                                                                                                               factory()
                                                                                                                                   .getLocationTracker()
                                                                                                                                   .getCurrentLocation(),
                                                                                                                               Strings.Empty,
                                                                                                                               JavaSymbolType.Unknown
                                                                                                             )
                                                                                                         )
                                                                                                    .accepts(AcceptedCharacters.Any)
                                                                       )
                                                )
                             ),
                             new RazorError(RazorResources().parseErrorRazorCommentNotTerminated(), 5, 0, 5),
                             new RazorError(RazorResources().parseErrorExpectedCloseBracketBeforeEof("(", ")"), 4, 0, 4)
                         );
    }

    @Test
    public void razorCommentInVerbatimBlock() {
        parseDocumentTest(
                             "@{" + Environment.NewLine + "    <text" + Environment.NewLine + "    @**@" +
                             Environment.NewLine + "}",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new StatementBlock(
                                                                      factory().codeTransition(),
                                                                      factory().metaCode("{")
                                                                               .accepts(AcceptedCharacters.None),
                                                                      factory().code(Environment.NewLine).asStatement(),
                                                                      new MarkupBlock(
                                                                                         factory().markup("    "),
                                                                                         factory().markupTransition("<text")
                                                                                                  .accepts(AcceptedCharacters.Any),
                                                                                         factory().markup(
                                                                                                             Environment.NewLine +
                                                                                                             "    "
                                                                                                         ),
                                                                                         new CommentBlock(
                                                                                                             factory().markupTransition(HtmlSymbolType.RazorCommentTransition)
                                                                                                                      .accepts(AcceptedCharacters.None),
                                                                                                             factory().metaMarkup(
                                                                                                                                     "*",
                                                                                                                                     HtmlSymbolType.RazorCommentStar
                                                                                                                                 )
                                                                                                                      .accepts(AcceptedCharacters.None),
                                                                                                             factory().span(
                                                                                                                               SpanKind.Comment,
                                                                                                                               new HtmlSymbol(
                                                                                                                                                 factory()
                                                                                                                                                     .getLocationTracker()
                                                                                                                                                     .getCurrentLocation(),
                                                                                                                                                 Strings.Empty,
                                                                                                                                                 HtmlSymbolType.Unknown
                                                                                                                               )
                                                                                                                           )
                                                                                                                      .accepts(AcceptedCharacters.Any),
                                                                                                             factory().metaMarkup(
                                                                                                                                     "*",
                                                                                                                                     HtmlSymbolType.RazorCommentStar
                                                                                                                                 )
                                                                                                                      .accepts(AcceptedCharacters.None),
                                                                                                             factory().markupTransition(HtmlSymbolType.RazorCommentTransition)
                                                                                                                      .accepts(AcceptedCharacters.None)
                                                                                         ),
                                                                                         factory().markup(
                                                                                                             Environment.NewLine +
                                                                                                             "}"
                                                                                                         )
                                                                      )
                                                )
                             ),
                             new RazorError(RazorResources().parseErrorTextTagCannotContainAttributes(), 8, 1, 4),
                             new RazorError(RazorResources().parseErrorMissingEndTag("text"), 8, 1, 4),
                             new RazorError(
                                               RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                         RazorResources()
                                                                                                             .blockNameCode(),
                                                                                                         "}", "{"
                                                                                                     ), 1, 0, 1
                             )
                         );
    }

    @Test
    public void unterminatedRazorCommentInVerbatimBlock() {
        parseDocumentTest(
                             "@{@*",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new StatementBlock(
                                                                      factory().codeTransition(),
                                                                      factory().metaCode("{")
                                                                               .accepts(AcceptedCharacters.None),
                                                                      factory().emptyJava().asStatement(),
                                                                      new CommentBlock(
                                                                                          factory().codeTransition(JavaSymbolType.RazorCommentTransition)
                                                                                                   .accepts(AcceptedCharacters.None),
                                                                                          factory().metaCode(
                                                                                                                "*",
                                                                                                                JavaSymbolType.RazorCommentStar
                                                                                                            )
                                                                                                   .accepts(AcceptedCharacters.None),
                                                                                          factory().span(
                                                                                                            SpanKind.Comment,
                                                                                                            new JavaSymbol(
                                                                                                                              factory()
                                                                                                                                  .getLocationTracker()
                                                                                                                                  .getCurrentLocation(),
                                                                                                                              Strings.Empty,
                                                                                                                              JavaSymbolType.Unknown
                                                                                                            )
                                                                                                        )
                                                                                                   .accepts(AcceptedCharacters.Any)
                                                                      )
                                                )
                             ),
                             new RazorError(RazorResources().parseErrorRazorCommentNotTerminated(), 2, 0, 2),
                             new RazorError(
                                               RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                         RazorResources()
                                                                                                             .blockNameCode(),
                                                                                                         "}", "{"
                                                                                                     ), 1, 0, 1
                             )
                         );
    }

}
