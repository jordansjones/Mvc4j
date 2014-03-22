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

import nextmethod.web.razor.editor.AutoCompleteEditHandler;
import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.JavaLanguageCharacteristics;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.FunctionsBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaErrorTest extends JavaHtmlCodeParserTestBase {

    @Test
    public void parseBlockHandlesQuotesAfterTransition() {
        parseBlockTest(
                          "@\"",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().emptyJava()
                                                          .asImplicitExpression(getKeywordSet())
                                                          .accepts(AcceptedCharacters.NonWhiteSpace)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorUnexpectedCharacterAtStartOfCodeBlock("\""),
                                            1, 0, 1
                          )
                      );
    }

    @Test
    public void parseBlockCapturesWhitespaceToEndOfLineInInvalidUsingStatementAndTreatsAsFileCode() {
        parseBlockTest(
                          "using          " + Environment.NewLine + Environment.NewLine,
                          new StatementBlock(
                                                factory().code("using          " + Environment.NewLine).asStatement()
                          )
                      );
    }

    @Test
    public void parseBlockMethodOutputsOpenCurlyAsCodeSpanIfEofFoundAfterOpenCurlyBrace() {
        parseBlockTest(
                          "{",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().emptyJava()
                                                         .asStatement()
                                                         .with(
                                                                  new AutoCompleteEditHandler(
                                                                                                 JavaLanguageCharacteristics.Instance
                                                                                                     .createTokenizeStringDelegate()
                                                                  ) {{
                                                                      this.setAutoCompleteString("}");
                                                                  }}
                                                              )
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      RazorResources().blockNameCode(),
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockMethodOutputsZeroLengthCodeSpanIfStatementBlockEmpty() {
        parseBlockTest(
                          "{}",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().emptyJava().asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockMethodProducesErrorIfNewlineFollowsTransition() {
        parseBlockTest(
                          "@" + Environment.NewLine,
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().emptyJava()
                                                          .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                          .accepts(AcceptedCharacters.NonWhiteSpace)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorUnexpectedWhiteSpaceAtStartOfCodeBlock(),
                                            1, 0, 1
                          )
                      );
    }

    @Test
    public void parseBlockMethodProducesErrorIfWhitespaceBetweenTransitionAndBlockStartInEmbeddedExpression() {
        parseBlockTest(
                          "{" + Environment.NewLine
                          + "    @   {}" + Environment.NewLine
                          + "}",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().code("\r\n    ").asStatement(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().emptyJava()
                                                                                .asImplicitExpression(
                                                                                                         JavaCodeParser.DefaultKeywords,
                                                                                                         true
                                                                                                     )
                                                                                .accepts(AcceptedCharacters.NonWhiteSpace)
                                                ),
                                                factory().code("   {}\r\n").asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorUnexpectedWhiteSpaceAtStartOfCodeBlock(),
                                            8, 1, 5
                          )
                      );
    }

    @Test
    public void parseBlockMethodProducesErrorIfEOFAfterTransitionInEmbeddedExpression() {
        parseBlockTest(
                          "{" + Environment.NewLine
                          + "    @",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().code("\r\n    ").asStatement(),
                                                new ExpressionBlock(
                                                                       factory().codeTransition(),
                                                                       factory().emptyJava()
                                                                                .asImplicitExpression(
                                                                                                         JavaCodeParser.DefaultKeywords,
                                                                                                         true
                                                                                                     )
                                                                                .accepts(AcceptedCharacters.NonWhiteSpace)
                                                ),
                                                factory().emptyJava().asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorUnexpectedEndOfFileAtStartOfCodeBlock(),
                                            8, 1, 5
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      RazorResources().blockNameCode(),
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockMethodParsesNothingIfFirstCharacterIsNotIdentifierStartOrParenOrBrace() {
        parseBlockTest(
                          "@!!!",
                          new ExpressionBlock(
                                                 factory().codeTransition(),
                                                 factory().emptyJava()
                                                          .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                          .accepts(AcceptedCharacters.NonWhiteSpace)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorUnexpectedCharacterAtStartOfCodeBlock("!"),
                                            1, 0, 1
                          )
                      );
    }

    @Test
    public void parseBlockShouldReportErrorAndTerminateAtEOFIfIfParenInExplicitExpressionUnclosed() {
        parseBlockTest(
                          "(foo bar" + Environment.NewLine
                          + "baz",
                          new ExpressionBlock(
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().code("foo bar\r\nbaz").asExpression()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      RazorResources().blockNameExplicitExpression(),
                                                                                                      ")",
                                                                                                      "("
                                                                                                  ),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockShouldReportErrorAndTerminateAtMarkupIfIfParenInExplicitExpressionUnclosed() {
        parseBlockTest(
                          "(foo bar" + Environment.NewLine
                          + "<html>" + Environment.NewLine
                          + "baz" + Environment.NewLine
                          + "</html",
                          new ExpressionBlock(
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().code("foo bar\r\n").asExpression()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      RazorResources().blockNameExplicitExpression(),
                                                                                                      ")",
                                                                                                      "("
                                                                                                  ),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockCorrectlyHandlesInCorrectTransitionsIfImplicitExpressionParensUnclosed() {
        parseBlockTest(
                          "Href(" + Environment.NewLine
                          + "<h1>@Html.Foo(Bar);</h1>" + Environment.NewLine,
                          new ExpressionBlock(
                                                 factory().code("Href(\r\n")
                                                          .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedCloseBracketBeforeEof(
                                                                                                        "(",
                                                                                                        ")"
                                                                                                    ),
                                            4, 0, 4
                          )
                      );
    }

    @Test
    public void parseBlockShouldReportErrorAndTerminateAtEOFIfParenInImplicitExpressionUnclosed() {
        parseBlockTest(
                          "Foo(Bar(Baz)" + Environment.NewLine
                          + "Biz" + Environment.NewLine
                          + "Boz",
                          new ExpressionBlock(
                                                 factory().code("Foo(Bar(Baz)\r\nBiz\r\nBoz")
                                                          .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedCloseBracketBeforeEof(
                                                                                                        "(",
                                                                                                        ")"
                                                                                                    ),
                                            3, 0, 3
                          )
                      );
    }

    @Test
    public void parseBlockShouldReportErrorAndTerminateAtMarkupIfParenInImplicitExpressionUnclosed() {
        parseBlockTest(
                          "Foo(Bar(Baz)" + Environment.NewLine
                          + "Biz" + Environment.NewLine
                          + "<html>" + Environment.NewLine
                          + "Boz" + Environment.NewLine
                          + "</html>",
                          new ExpressionBlock(
                                                 factory().code("Foo(Bar(Baz)\r\nBiz\r\n")
                                                          .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedCloseBracketBeforeEof(
                                                                                                        "(",
                                                                                                        ")"
                                                                                                    ),
                                            3, 0, 3
                          )
                      );
    }

    @Test
    public void parseBlockShouldReportErrorAndTerminateAtEOFIfBracketInImplicitExpressionUnclosed() {
        parseBlockTest(
                          "Foo[Bar[Baz]" + Environment.NewLine
                          + "Biz" + Environment.NewLine
                          + "Boz",
                          new ExpressionBlock(
                                                 factory().code("Foo[Bar[Baz]\r\nBiz\r\nBoz")
                                                          .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedCloseBracketBeforeEof(
                                                                                                        "[",
                                                                                                        "]"
                                                                                                    ),
                                            3, 0, 3
                          )
                      );
    }

    @Test
    public void parseBlockShouldReportErrorAndTerminateAtMarkupIfBracketInImplicitExpressionUnclosed() {
        parseBlockTest(
                          "Foo[Bar[Baz]" + Environment.NewLine
                          + "Biz" + Environment.NewLine
                          + "<b>" + Environment.NewLine
                          + "Boz" + Environment.NewLine
                          + "</b>",
                          new ExpressionBlock(
                                                 factory().code("Foo[Bar[Baz]\r\nBiz\r\n")
                                                          .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedCloseBracketBeforeEof(
                                                                                                        "[",
                                                                                                        "]"
                                                                                                    ),
                                            3, 0, 3
                          )
                      );
    }

    @Test
    public void parseBlockReportsErrorIfExplicitCodeBlockUnterminatedAtEOF() {
        parseBlockTest(
                          "{ var foo = bar; if(foo != null) { bar(); } ",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().code(" var foo = bar; if(foo != null) { bar(); } ")
                                                         .asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      RazorResources().blockNameCode(),
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockReportsErrorIfClassBlockUnterminatedAtEOF() {
        parseBlockTest(
                          "functions { var foo = bar; if(foo != null) { bar(); } ",
                          new FunctionsBlock(
                                                factory().metaCode("functions {").accepts(AcceptedCharacters.None),
                                                factory().code(" var foo = bar; if(foo != null) { bar(); } ")
                                                         .asFunctionsBody()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      "functions",
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockReportsErrorIfIfBlockUnterminatedAtEOF() {
        runUnterminatedSimpleKeywordBlock("if");
    }

    @Test
    public void parseBlockReportsErrorIfElseBlockUnterminatedAtEOF() {
        parseBlockTest(
                          "if(foo) { baz(); } else { var foo = bar; if(foo != null) { bar(); } ",
                          new StatementBlock(
                                                factory().code("if(foo) { baz(); } else { var foo = bar; if(foo != null) { bar(); } ")
                                                         .asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      "else",
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            19, 0, 19
                          )
                      );
    }

    @Test
    public void parseBlockReportsErrorIfElseIfBlockUnterminatedAtEOF() {
        parseBlockTest(
                          "if(foo) { baz(); } else if { var foo = bar; if(foo != null) { bar(); } ",
                          new StatementBlock(
                                                factory().code("if(foo) { baz(); } else if { var foo = bar; if(foo != null) { bar(); } ")
                                                         .asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      "else if",
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            19, 0, 19
                          )
                      );
    }

    @Test
    public void parseBlockReportsErrorIfDoBlockUnterminatedAtEOF() {
        parseBlockTest(
                          "do { var foo = bar; if(foo != null) { bar(); } ",
                          new StatementBlock(
                                                factory().code("do { var foo = bar; if(foo != null) { bar(); } ")
                                                         .asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      "do",
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockReportsErrorIfTryBlockUnterminatedAtEOF() {
        parseBlockTest(
                          "try { var foo = bar; if(foo != null) { bar(); } ",
                          new StatementBlock(
                                                factory().code("try { var foo = bar; if(foo != null) { bar(); } ")
                                                         .asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      "try",
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockReportsErrorIfCatchBlockUnterminatedAtEOF() {
        parseBlockTest(
                          "try { baz(); } catch(Foo) { var foo = bar; if(foo != null) { bar(); } ",
                          new StatementBlock(
                                                factory().code("try { baz(); } catch(Foo) { var foo = bar; if(foo != null) { bar(); } ")
                                                         .asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      "catch",
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            15, 0, 15
                          )
                      );
    }

    @Test
    public void parseBlockReportsErrorIfFinallyBlockUnterminatedAtEOF() {
        parseBlockTest(
                          "try { baz(); } finally { var foo = bar; if(foo != null) { bar(); } ",
                          new StatementBlock(
                                                factory().code("try { baz(); } finally { var foo = bar; if(foo != null) { bar(); } ")
                                                         .asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      "finally",
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            15, 0, 15
                          )
                      );
    }

    @Test
    public void parseBlockReportsErrorIfForBlockUnterminatedAtEOF() {
        runUnterminatedSimpleKeywordBlock("for");
    }

    @Test
    public void parseBlockReportsErrorIfForeachBlockUnterminatedAtEOF() {
        runUnterminatedSimpleKeywordBlock("foreach");
    }

    @Test
    public void parseBlockReportsErrorIfWhileBlockUnterminatedAtEOF() {
        runUnterminatedSimpleKeywordBlock("while");
    }

    @Test
    public void parseBlockReportsErrorIfSwitchBlockUnterminatedAtEOF() {
        runUnterminatedSimpleKeywordBlock("switch");
    }

    @Test
    public void parseBlockReportsErrorIfLockBlockUnterminatedAtEOF() {
        runUnterminatedSimpleKeywordBlock("lock");
    }

    @Test
    public void parseBlockReportsErrorIfUsingBlockUnterminatedAtEOF() {
        runUnterminatedSimpleKeywordBlock("using");
    }

    @Test
    public void parseBlockRequiresControlFlowStatementsToHaveBraces() {
        final String expectedMessage = RazorResources().parseErrorSingleLineControlFlowStatementsNotAllowed("{", "<");
        parseBlockTest(
                          "if(foo) <p>Bar</p> else if(bar) <p>Baz</p> else <p>Boz</p>",
                          new StatementBlock(
                                                factory().code("if(foo) ").asStatement(),
                                                new MarkupBlock(
                                                                   factory().markup("<p>Bar</p> ")
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().code("else if(bar) ").asStatement(),
                                                new MarkupBlock(
                                                                   factory().markup("<p>Baz</p> ")
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().code("else ").asStatement(),
                                                new MarkupBlock(
                                                                   factory().markup("<p>Boz</p>")
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().emptyJava().asStatement()
                          ),
                          new RazorError(expectedMessage, 8, 0, 8),
                          new RazorError(expectedMessage, 32, 0, 32),
                          new RazorError(expectedMessage, 48, 0, 48)
                      );
    }

    @Test
    public void parseBlockIncludesUnexpectedCharacterInSingleStatementControlFlowStatementError() {
        parseBlockTest(
                          "if(foo)) { var bar = foo; }",
                          new StatementBlock(
                                                factory().code("if(foo)) { var bar = foo; }").asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorSingleLineControlFlowStatementsNotAllowed(
                                                                                                                    "{",
                                                                                                                    ")"
                                                                                                                ),
                                            7, 0, 7
                          )
                      );
    }

    @Test
    public void parseBlockOutputsErrorIfAtSignFollowedByLessThanSignAtStatementStart() {
        parseBlockTest(
                          "if(foo) { @<p>Bar</p> }",
                          new StatementBlock(
                                                factory().code("if(foo) {").asStatement(),
                                                new MarkupBlock(
                                                                   factory().markup(" "),
                                                                   factory().markupTransition(),
                                                                   factory().markup("<p>Bar</p> ")
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().code("}").asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorAtInCodeMustBeFollowedByColonParenOrIdentifierStart(),
                                            10, 0, 10
                          )
                      );
    }

    @Test
    public void parseBlockTerminatesIfBlockAtEOLWhenRecoveringFromMissingCloseParen() {
        parseBlockTest(
                          "if(foo bar" + Environment.NewLine
                          + "baz",
                          new StatementBlock(
                                                factory().code("if(foo bar\r\n").asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedCloseBracketBeforeEof(
                                                                                                        "(",
                                                                                                        ")"
                                                                                                    ),
                                            2, 0, 2
                          )
                      );
    }

    @Test
    public void parseBlockTerminatesForeachBlockAtEOLWhenRecoveringFromMissingCloseParen() {
        parseBlockTest(
                          "foreach(foo bar" + Environment.NewLine
                          + "baz",
                          new StatementBlock(
                                                factory().code("foreach(foo bar\r\n").asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedCloseBracketBeforeEof(
                                                                                                        "(",
                                                                                                        ")"
                                                                                                    ),
                                            7, 0, 7
                          )
                      );
    }

    @Test
    public void parseBlockTerminatesWhileClauseInDoStatementAtEOLWhenRecoveringFromMissingCloseParen() {
        parseBlockTest(
                          "do { } while(foo bar" + Environment.NewLine
                          + "baz",
                          new StatementBlock(
                                                factory().code("do { } while(foo bar\r\n").asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedCloseBracketBeforeEof(
                                                                                                        "(",
                                                                                                        ")"
                                                                                                    ),
                                            12, 0, 12
                          )
                      );
    }

    @Test
    public void parseBlockTerminatesUsingBlockAtEOLWhenRecoveringFromMissingCloseParen() {
        parseBlockTest(
                          "using(foo bar" + Environment.NewLine
                          + "baz",
                          new StatementBlock(
                                                factory().code("using(foo bar\r\n").asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedCloseBracketBeforeEof(
                                                                                                        "(",
                                                                                                        ")"
                                                                                                    ),
                                            5, 0, 5
                          )
                      );
    }

    @Test
    public void parseBlockResumesIfStatementAfterOpenParen() {
        parseBlockTest(
                          "if(" + Environment.NewLine
                          + "else { <p>Foo</p> }",
                          new StatementBlock(
                                                factory().code("if(\r\nelse {").asStatement(),
                                                new MarkupBlock(
                                                                   factory().markup(" <p>Foo</p> ")
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().code("}").asStatement().accepts(AcceptedCharacters.None)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedCloseBracketBeforeEof(
                                                                                                        "(",
                                                                                                        ")"
                                                                                                    ),
                                            2, 0, 2
                          )
                      );
    }

    @Test
    public void parseBlockTerminatesNormalCSharpStringsAtEOLIfEndQuoteMissing() {
        singleSpanBlockTest(
                               "if(foo) {" + Environment.NewLine
                               + "    var p = \"foo bar baz" + Environment.NewLine
                               + ";" + Environment.NewLine
                               + "}",
                               BlockType.Statement,
                               SpanKind.Code,
                               new RazorError(
                                                 RazorResources().parseErrorUnterminatedStringLiteral(),
                                                 23, 1, 12
                               )
                           );
    }

    @Test
    public void parseBlockTerminatesNormalStringAtEndOfFile() {
        singleSpanBlockTest(
                               "if(foo) { var foo = \"blah blah blah blah blah",
                               BlockType.Statement,
                               SpanKind.Code,
                               new RazorError(
                                                 RazorResources().parseErrorUnterminatedStringLiteral(),
                                                 20, 0, 20
                               ),
                               new RazorError(
                                                 RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                           "if",
                                                                                                           "}",
                                                                                                           "{"
                                                                                                       ),
                                                 SourceLocation.Zero
                               )
                           );
    }

    @Test
    public void parseBlockTerminatesVerbatimStringAtEndOfFile() {
        singleSpanBlockTest(
                               "if(foo) { var foo = @\"blah " + Environment.NewLine
                               + "blah; " + Environment.NewLine
                               + "<p>Foo</p>" + Environment.NewLine
                               + "blah " + Environment.NewLine
                               + "blah",
                               BlockType.Statement,
                               SpanKind.Code,
                               new RazorError(
                                                 RazorResources().parseErrorUnterminatedStringLiteral(),
                                                 20, 0, 20
                               ),
                               new RazorError(
                                                 RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                           "if",
                                                                                                           "}",
                                                                                                           "{"
                                                                                                       ),
                                                 SourceLocation.Zero
                               )
                           );
    }

    @Test
    public void parseBlockCorrectlyParsesMarkupIncorrectyAssumedToBeWithinAStatement() {
        parseBlockTest(
                          "if(foo) {" + Environment.NewLine
                          + "    var foo = \"foo bar baz" + Environment.NewLine
                          + "    <p>Foo is @foo</p>" + Environment.NewLine
                          + "}",
                          new StatementBlock(
                                                factory().code("if(foo) {\r\n    var foo = \"foo bar baz\r\n    ")
                                                         .asStatement(),
                                                new MarkupBlock(
                                                                   factory().markup("<p>Foo is "),
                                                                   new ExpressionBlock(
                                                                                          factory().codeTransition(),
                                                                                          factory().code("foo")
                                                                                                   .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                                   .accepts(AcceptedCharacters.NonWhiteSpace)
                                                                   ),
                                                                   factory().markup("</p>\r\n")
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().code("}").asStatement()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorUnterminatedStringLiteral(),
                                            25, 1, 14
                          )
                      );
    }

    @Test
    public void parseBlockCorrectlyParsesAtSignInDelimitedBlock() {
        parseBlockTest(
                          "(Request[\"description\"] ?? @photo.Description)",
                          new ExpressionBlock(
                                                 factory().metaCode("(").accepts(AcceptedCharacters.None),
                                                 factory().code("Request[\"description\"] ?? @photo.Description")
                                                          .asExpression(),
                                                 factory().metaCode(")").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockCorrectlyRecoversFromMissingCloseParenInExpressionWithinCode() {
        parseBlockTest(
                          "{String.Format(<html></html>}",
                          new StatementBlock(
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().code("String.Format(").asStatement(),
                                                new MarkupBlock(
                                                                   factory().markup("<html></html>")
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().emptyJava().asStatement(),
                                                factory().metaCode("}").accepts(AcceptedCharacters.None)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedCloseBracketBeforeEof(
                                                                                                        "(",
                                                                                                        ")"
                                                                                                    ),
                                            14, 0, 14
                          )
                      );
    }


    private void runUnterminatedSimpleKeywordBlock(final String keyword) {
        singleSpanBlockTest(
                               keyword + " (foo) { var foo = bar; if(foo != null) { bar(); } ",
                               BlockType.Statement,
                               SpanKind.Code,
                               new RazorError(
                                                 RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                           keyword,
                                                                                                           "}",
                                                                                                           "{"
                                                                                                       ),
                                                 SourceLocation.Zero
                               )
                           );
    }

}
