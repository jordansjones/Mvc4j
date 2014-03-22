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
import nextmethod.web.razor.generator.HelperCodeGenerator;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.HelperBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.LocationTagged;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class JavaHelperTest extends JavaHtmlMarkupParserTestBase {

    @Test
    public void parseHelperCorrectlyParsesHelperWithNoSpaceInBody() {
        parseDocumentTest(
                             "@helper Foo(){@Bar()}",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   new HelperCodeGenerator(
                                                                                              new LocationTagged<>(
                                                                                                                      "Foo(){",
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                              ), true
                                                                   ),
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper ")
                                                                            .accepts(AcceptedCharacters.None),
                                                                   factory().code("Foo(){")
                                                                            .hidden()
                                                                            .accepts(AcceptedCharacters.None),
                                                                   new StatementBlock(
                                                                                         factory().emptyJava()
                                                                                                  .asStatement(),
                                                                                         new ExpressionBlock(
                                                                                                                factory()
                                                                                                                    .codeTransition(),
                                                                                                                factory()
                                                                                                                    .code("Bar()")
                                                                                                                    .asImplicitExpression(
                                                                                                                                             JavaCodeParser.DefaultKeywords,
                                                                                                                                             true
                                                                                                                                         )
                                                                                                                    .accepts(AcceptedCharacters.NonWhiteSpace)
                                                                                         ),
                                                                                         factory().emptyJava()
                                                                                                  .asStatement()
                                                                   ),
                                                                   factory().code("}")
                                                                            .hidden()
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().emptyHtml()
                             )
                         );
    }

    @Test
    public void parseHelperCorrectlyParsesIncompleteHelperPreceedingCodeBlock() {
        parseDocumentTest(
                             "@helper" + Environment.NewLine
                             + "@{}",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper")
                                                ),
                                                factory().markup("\r\n"),
                                                new StatementBlock(
                                                                      factory().codeTransition(),
                                                                      factory().metaCode("{")
                                                                               .accepts(AcceptedCharacters.None),
                                                                      factory().emptyJava().asStatement(),
                                                                      factory().metaCode("}")
                                                                               .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().emptyHtml()
                             ),
                             new RazorError(
                                               RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(
                                                                                                                  RazorResources()
                                                                                                                      .errorComponentNewline()
                                                                                                              ),
                                               7, 0, 7
                             )
                         );
    }

    @Test
    public void parseHelperRequiresSpaceBeforeSignature() {
        parseDocumentTest(
                             "@helper{",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper")
                                                ),
                                                factory().markup("{")
                             ),
                             new RazorError(
                                               RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(
                                                                                                                  RazorResources()
                                                                                                                      .errorComponentCharacter("{")
                                                                                                              ),
                                               7, 0, 7
                             )
                         );
    }

    @Test
    public void parseHelperOutputsErrorButContinuesIfLParenFoundAfterHelperKeyword() {
        parseDocumentTest(
                             "@helper () {",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   new HelperCodeGenerator(
                                                                                              new LocationTagged<>(
                                                                                                                      "() {",
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                              ), true
                                                                   ),
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper ")
                                                                            .accepts(AcceptedCharacters.None),
                                                                   factory().code("() {")
                                                                            .hidden()
                                                                            .accepts(AcceptedCharacters.None),
                                                                   new StatementBlock(
                                                                                         factory().emptyJava()
                                                                                                  .asStatement()
                                                                                                  .autoCompleteWith("}")
                                                                   )
                                                )
                             ),
                             new RazorError(
                                               RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(
                                                                                                                  RazorResources()
                                                                                                                      .errorComponentCharacter("(")
                                                                                                              ),
                                               8, 0, 8
                             ),
                             new RazorError(
                                               RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                         "helper", "}",
                                                                                                         "{"
                                                                                                     ),
                                               1, 0, 1
                             )
                         );
    }

    @Test
    public void parseHelperStatementOutputsMarkerHelperHeaderSpanOnceKeywordComplete() {
        parseDocumentTest(
                             "@helper ",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   new HelperCodeGenerator(
                                                                                              new LocationTagged<>(
                                                                                                                      Strings.Empty,
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                              ), false
                                                                   ),
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper ")
                                                                            .accepts(AcceptedCharacters.None),
                                                                   factory().emptyJava().hidden()
                                                )
                             ),
                             new RazorError(
                                               RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(
                                                                                                                  RazorResources()
                                                                                                                      .errorComponentEndOfFile()
                                                                                                              ),
                                               8, 0, 8
                             )
                         );
    }

    @Test
    public void parseHelperStatementMarksHelperSpanAsCanGrowIfMissingTrailingSpace() {
        parseDocumentTest(
                             "@helper",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper")
                                                                            .accepts(AcceptedCharacters.Any)
                                                )
                             ),
                             new RazorError(
                                               RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(
                                                                                                                  RazorResources()
                                                                                                                      .errorComponentEndOfFile()
                                                                                                              ),
                                               7, 0, 7
                             )
                         );
    }

    @Test
    public void parseHelperStatementCapturesWhitespaceToEndOfLineIfHelperStatementMissingName() {
        parseDocumentTest(
                             "@helper                       " + Environment.NewLine
                             + "    ",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   new HelperCodeGenerator(
                                                                                              new LocationTagged<>(
                                                                                                                      "                      ",
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                              ), false
                                                                   ),
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper ")
                                                                            .accepts(AcceptedCharacters.None),
                                                                   factory().code("                      \r\n").hidden()
                                                ),
                                                factory().markup("    ")
                             ),
                             new RazorError(
                                               RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(
                                                                                                                  RazorResources()
                                                                                                                      .errorComponentNewline()
                                                                                                              ),
                                               30, 0, 30
                             )
                         );
    }

    @Test
    public void parseHelperStatementCapturesWhitespaceToEndOfLineIfHelperStatementMissingOpenParen() {
        parseDocumentTest(
                             "@helper Foo    " + Environment.NewLine + "    ",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   new HelperCodeGenerator(
                                                                                              new LocationTagged<>(
                                                                                                                      "Foo    ",
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                              ), false
                                                                   ),
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper ")
                                                                            .accepts(AcceptedCharacters.None),
                                                                   factory().code("Foo    \r\n").hidden()
                                                ),
                                                factory().markup("    ")
                             ),
                             new RazorError(
                                               RazorResources().parseErrorMissingCharAfterHelperName("("),
                                               15, 0, 15
                             )
                         );
    }

    @Test
    public void parseHelperStatementCapturesAllContentToEndOfFileIfHelperStatementMissingCloseParenInParameterList() {
        parseDocumentTest(
                             "@helper Foo(Foo Bar" + Environment.NewLine + "Biz" + Environment.NewLine + "Boz",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   new HelperCodeGenerator(
                                                                                              new LocationTagged<>(
                                                                                                                      "Foo(Foo Bar\r\nBiz\r\nBoz",
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                              ), false
                                                                   ),
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper ")
                                                                            .accepts(AcceptedCharacters.None),
                                                                   factory().code("Foo(Foo Bar\r\nBiz\r\nBoz").hidden()
                                                )
                             ),
                             new RazorError(
                                               RazorResources().parseErrorUnterminatedHelperParameterList(),
                                               11, 0, 11
                             )
                         );
    }

    @Test
    public void parseHelperStatementCapturesWhitespaceToEndOfLineIfHelperStatementMissingOpenBraceAfterParameterList() {
        parseDocumentTest(
                             "@helper Foo(String foo)    " + Environment.NewLine,
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   new HelperCodeGenerator(
                                                                                              new LocationTagged<>(
                                                                                                                      "Foo(String foo)    ",
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                              ), false
                                                                   ),
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper ")
                                                                            .accepts(AcceptedCharacters.None),
                                                                   factory().code("Foo(String foo)    \r\n").hidden()
                                                )
                             ),
                             new RazorError(
                                               RazorResources().parseErrorMissingCharAfterHelperParameters("{"),
                                               29, 1, 0
                             )
                         );
    }

    @Test
    public void parseHelperStatementContinuesParsingHelperUntilEOF() {
        parseDocumentTest(
                             "@helper Foo(String foo) {    " + Environment.NewLine + "    <p>Foo</p>",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   new HelperCodeGenerator(
                                                                                              new LocationTagged<>(
                                                                                                                      "Foo(String foo) {",
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                              ), true
                                                                   ),
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper ")
                                                                            .accepts(AcceptedCharacters.None),
                                                                   factory().code("Foo(String foo) {")
                                                                            .hidden()
                                                                            .accepts(AcceptedCharacters.None),
                                                                   new StatementBlock(
                                                                                         factory().code("    \r\n")
                                                                                                  .asStatement()
                                                                                                  .autoCompleteWith("}"),
                                                                                         new MarkupBlock(
                                                                                                            factory().markup("    <p>Foo</p>")
                                                                                                                     .accepts(AcceptedCharacters.None)
                                                                                         ),
                                                                                         factory().emptyJava()
                                                                                                  .asStatement()
                                                                   )
                                                )
                             ),
                             new RazorError(
                                               RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                         "helper", "}",
                                                                                                         "{"
                                                                                                     ),
                                               1, 0, 1
                             )
                         );
    }

    @Test
    public void parseHelperStatementCorrectlyParsesHelperWithEmbeddedCode() {
        parseDocumentTest(
                             "@helper Foo(String foo) {    " + Environment.NewLine + "    <p>@foo</p>" +
                             Environment.NewLine + "}",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   new HelperCodeGenerator(
                                                                                              new LocationTagged<>(
                                                                                                                      "Foo(String foo) {",
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                              ), true
                                                                   ),
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper ")
                                                                            .accepts(AcceptedCharacters.None),
                                                                   factory().code("Foo(String foo) {")
                                                                            .hidden()
                                                                            .accepts(AcceptedCharacters.None),
                                                                   new StatementBlock(
                                                                                         factory().code("    \r\n")
                                                                                                  .asStatement(),
                                                                                         new MarkupBlock(
                                                                                                            factory().markup("    <p>"),
                                                                                                            new ExpressionBlock(
                                                                                                                                   factory()
                                                                                                                                       .codeTransition(),
                                                                                                                                   factory()
                                                                                                                                       .code("foo")
                                                                                                                                       .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                                                                       .accepts(AcceptedCharacters.NonWhiteSpace)
                                                                                                            ),
                                                                                                            factory().markup("</p>\r\n")
                                                                                                                     .accepts(AcceptedCharacters.None)
                                                                                         ),
                                                                                         factory().emptyJava()
                                                                                                  .asStatement()
                                                                   ),
                                                                   factory().code("}")
                                                                            .hidden()
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().emptyHtml()
                             )
                         );
    }

    @Test
    public void parseHelperStatementCorrectlyParsesHelperWithNewlinesBetweenCloseParenAndOpenBrace() {
        parseDocumentTest(
                             "@helper Foo(String foo)" + Environment.NewLine + Environment.NewLine +
                             Environment.NewLine + Environment.NewLine + "{    " + Environment.NewLine +
                             "    <p>@foo</p>" + Environment.NewLine + "}",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   new HelperCodeGenerator(
                                                                                              new LocationTagged<>(
                                                                                                                      "Foo(String foo)\r\n\r\n\r\n\r\n{",
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                              ), true
                                                                   ),
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper ")
                                                                            .accepts(AcceptedCharacters.None),
                                                                   factory().code("Foo(String foo)\r\n\r\n\r\n\r\n{")
                                                                            .hidden()
                                                                            .accepts(AcceptedCharacters.None),
                                                                   new StatementBlock(
                                                                                         factory().code("    \r\n")
                                                                                                  .asStatement(),
                                                                                         new MarkupBlock(
                                                                                                            factory().markup("    <p>"),
                                                                                                            new ExpressionBlock(
                                                                                                                                   factory()
                                                                                                                                       .codeTransition(),
                                                                                                                                   factory()
                                                                                                                                       .code("foo")
                                                                                                                                       .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                                                                       .accepts(AcceptedCharacters.NonWhiteSpace)
                                                                                                            ),
                                                                                                            factory().markup("</p>\r\n")
                                                                                                                     .accepts(AcceptedCharacters.None)
                                                                                         ),
                                                                                         factory().emptyJava()
                                                                                                  .asStatement()
                                                                   ),
                                                                   factory().code("}")
                                                                            .hidden()
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().emptyHtml()
                             )
                         );
    }

    @Test
    public void parseHelperStatementGivesWhitespaceAfterOpenBraceToMarkupInDesignMode() {
        parseDocumentTest(
                             "@helper Foo(String foo) {    " + Environment.NewLine + "    ",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   new HelperCodeGenerator(
                                                                                              new LocationTagged<>(
                                                                                                                      "Foo(String foo) {",
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                              ), true
                                                                   ),
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper ")
                                                                            .accepts(AcceptedCharacters.None),
                                                                   factory().code("Foo(String foo) {")
                                                                            .hidden()
                                                                            .accepts(AcceptedCharacters.None),
                                                                   new StatementBlock(
                                                                                         factory().code("    \r\n    ")
                                                                                                  .asStatement()
                                                                                                  .autoCompleteWith("}")
                                                                   )
                                                )
                             ),
                             true, // designTimeParser
                             new RazorError(
                                               RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                         "helper", "}",
                                                                                                         "{"
                                                                                                     ),
                                               1, 0, 1
                             )
                         );
    }

    @Test
    public void parseHelperAcceptsNestedHelpersButOutputsError() {
        parseDocumentTest(
                             "@helper Foo(String foo) {" + Environment.NewLine
                             + "    @helper Bar(String baz) {" + Environment.NewLine
                             + "    }" + Environment.NewLine
                             + "}",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new HelperBlock(
                                                                   new HelperCodeGenerator(
                                                                                              new LocationTagged<>(
                                                                                                                      "Foo(String foo) {",
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                              ), true
                                                                   ),
                                                                   factory().codeTransition(),
                                                                   factory().metaCode("helper ")
                                                                            .accepts(AcceptedCharacters.None),
                                                                   factory().code("Foo(String foo) {")
                                                                            .hidden()
                                                                            .accepts(AcceptedCharacters.None),
                                                                   new StatementBlock(
                                                                                         factory().code("\r\n    ")
                                                                                                  .asStatement(),
                                                                                         new HelperBlock(
                                                                                                            new HelperCodeGenerator(
                                                                                                                                       new LocationTagged<>(
                                                                                                                                                               "Bar(String baz) {",
                                                                                                                                                               39,
                                                                                                                                                               1,
                                                                                                                                                               12
                                                                                                                                       ),
                                                                                                                                       true
                                                                                                            ),
                                                                                                            factory().codeTransition(),
                                                                                                            factory().metaCode("helper ")
                                                                                                                     .accepts(AcceptedCharacters.None),
                                                                                                            factory().code("Bar(String baz) {")
                                                                                                                     .hidden()
                                                                                                                     .accepts(AcceptedCharacters.None),
                                                                                                            new StatementBlock(
                                                                                                                                  factory()
                                                                                                                                      .code("\r\n    ")
                                                                                                                                      .asStatement()
                                                                                                            ),
                                                                                                            factory().code("}")
                                                                                                                     .hidden()
                                                                                                                     .accepts(AcceptedCharacters.None)
                                                                                         ),
                                                                                         factory().code("\r\n")
                                                                                                  .asStatement()
                                                                   ),
                                                                   factory().code("}")
                                                                            .hidden()
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().emptyHtml()
                             ),
                             true, // designTimeParser
                             new RazorError(
                                               RazorResources().parseErrorHelpersCannotBeNested(),
                                               38, 1, 11
                             )
                         );
    }
}
