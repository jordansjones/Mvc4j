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

package nextmethod.web.razor.parser.html;

import nextmethod.web.razor.editor.SingleLineMarkupEditHandler;
import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.generator.AttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.DynamicAttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.LiteralAttributeCodeGenerator;
import nextmethod.web.razor.generator.SectionCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.JavaLanguageCharacteristics;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.SectionBlock;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import org.junit.Test;

import static nextmethod.web.razor.framework.LocationTaggedExtensions.locationTagged;

public class HtmlToCodeSwitchTest extends JavaHtmlMarkupParserTestBase {

    @Test
    public void parseBlockSwitchesWhenCharacterBeforeSwapIsNonAlphanumeric() {
        parseBlockTest(
                          "<p>foo#@i</p>",
                          new MarkupBlock(
                                             factory().markup("<p>foo#"),
                                             new ExpressionBlock(
                                                                    factory().codeTransition(),
                                                                    factory().code("i")
                                                                             .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                             .accepts(AcceptedCharacters.NonWhiteSpace)
                                             ),
                                             factory().markup("</p>").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockSwitchesToCodeWhenSwapCharacterEncounteredMidTag() {
        parseBlockTest(
                          "<foo @bar />",
                          new MarkupBlock(
                                             factory().markup("<foo "),
                                             new ExpressionBlock(
                                                                    factory().codeTransition(),
                                                                    factory().code("bar")
                                                                             .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                             .accepts(AcceptedCharacters.NonWhiteSpace)
                                             ),
                                             factory().markup(" />").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockSwitchesToCodeWhenSwapCharacterEncounteredInAttributeValue() {
        parseBlockTest(
                          "<foo bar=\"@baz\" />",
                          new MarkupBlock(
                                             factory().markup("<foo"),
                                             new MarkupBlock(
                                                                new AttributeBlockCodeGenerator(
                                                                                                   "bar",
                                                                                                   locationTagged(
                                                                                                                     " bar=\"",
                                                                                                                     4,
                                                                                                                     0,
                                                                                                                     4
                                                                                                                 ),
                                                                                                   locationTagged(
                                                                                                                     "\"",
                                                                                                                     14,
                                                                                                                     0,
                                                                                                                     14
                                                                                                                 )
                                                                ),
                                                                factory().markup(" bar=\"")
                                                                         .with(SpanCodeGenerator.Null),
                                                                new MarkupBlock(
                                                                                   new DynamicAttributeBlockCodeGenerator(
                                                                                                                             locationTagged(
                                                                                                                                               10,
                                                                                                                                               0
                                                                                                                                           ),
                                                                                                                             10,
                                                                                                                             0,
                                                                                                                             10
                                                                                   ),
                                                                                   new ExpressionBlock(
                                                                                                          factory().codeTransition(),
                                                                                                          factory().code("baz")
                                                                                                                   .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                                                   .accepts(AcceptedCharacters.NonWhiteSpace)
                                                                                   )
                                                                ),
                                                                factory().markup("\"").with(SpanCodeGenerator.Null)
                                             ),
                                             factory().markup(" />").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockSwitchesToCodeWhenSwapCharacterEncounteredInTagContent() {
        parseBlockTest(
                          "<foo>@bar<baz>@boz</baz></foo>",
                          new MarkupBlock(
                                             factory().markup("<foo>"),
                                             new ExpressionBlock(
                                                                    factory().codeTransition(),
                                                                    factory().code("bar")
                                                                             .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                             .accepts(AcceptedCharacters.NonWhiteSpace)
                                             ),
                                             factory().markup("<baz>"),
                                             new ExpressionBlock(
                                                                    factory().codeTransition(),
                                                                    factory().code("boz")
                                                                             .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                             .accepts(AcceptedCharacters.NonWhiteSpace)
                                             ),
                                             factory().markup("</baz></foo>").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockParsesCodeWithinSingleLineMarkup() {
        parseBlockTest(
                          "@:<li>Foo @Bar Baz" + Environment.NewLine + "bork",
                          new MarkupBlock(
                                             factory().markupTransition(),
                                             factory().metaMarkup(":", HtmlSymbolType.Colon),
                                             factory().markup("<li>Foo ")
                                                      .with(
                                                               new SingleLineMarkupEditHandler(
                                                                                                  JavaLanguageCharacteristics.Instance
                                                                                                      .createTokenizeStringDelegate()
                                                               )
                                                           ),
                                             new ExpressionBlock(
                                                                    factory().codeTransition(),
                                                                    factory().code("Bar")
                                                                             .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                             .accepts(AcceptedCharacters.NonWhiteSpace)
                                             ),
                                             factory().markup(" Baz\r\n")
                                                      .with(
                                                               new SingleLineMarkupEditHandler(
                                                                                                  JavaLanguageCharacteristics.Instance
                                                                                                      .createTokenizeStringDelegate(),
                                                                                                  AcceptedCharacters.None
                                                               )
                                                           )
                          )
                      );
    }

    @Test
    public void parseBlockSupportsCodeWithinComment() {
        parseBlockTest(
                          "<foo><!-- @foo --></foo>",
                          new MarkupBlock(
                                             factory().markup("<foo><!-- "),
                                             new ExpressionBlock(
                                                                    factory().codeTransition(),
                                                                    factory().code("foo")
                                                                             .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                             .accepts(AcceptedCharacters.NonWhiteSpace)
                                             ),
                                             factory().markup(" --></foo>").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockSupportsCodeWithinSGMLDeclaration() {
        parseBlockTest(
                          "<foo><!DOCTYPE foo @bar baz></foo>",
                          new MarkupBlock(
                                             factory().markup("<foo><!DOCTYPE foo "),
                                             new ExpressionBlock(
                                                                    factory().codeTransition(),
                                                                    factory().code("bar")
                                                                             .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                             .accepts(AcceptedCharacters.NonWhiteSpace)
                                             ),
                                             factory().markup(" baz></foo>").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockSupportsCodeWithinCDataDeclaration() {
        parseBlockTest(
                          "<foo><![CDATA[ foo @bar baz]]></foo>",
                          new MarkupBlock(
                                             factory().markup("<foo><![CDATA[ foo "),
                                             new ExpressionBlock(
                                                                    factory().codeTransition(),
                                                                    factory().code("bar")
                                                                             .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                             .accepts(AcceptedCharacters.NonWhiteSpace)
                                             ),
                                             factory().markup(" baz]]></foo>").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockSupportsCodeWithinXMLProcessingInstruction() {
        parseBlockTest(
                          "<foo><?xml foo @bar baz?></foo>",
                          new MarkupBlock(
                                             factory().markup("<foo><?xml foo "),
                                             new ExpressionBlock(
                                                                    factory().codeTransition(),
                                                                    factory().code("bar")
                                                                             .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                             .accepts(AcceptedCharacters.NonWhiteSpace)
                                             ),
                                             factory().markup(" baz?></foo>").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockDoesNotSwitchToCodeOnEmailAddressInText() {
        singleSpanBlockTest(
                               "<foo>jordansjones@gmail.com</foo>", BlockType.Markup, SpanKind.Markup,
                               AcceptedCharacters.None
                           );
    }

    @Test
    public void parseBlockDoesNotSwitchToCodeOnEmailAddressInAttribute() {
        parseBlockTest(
                          "<a href=\"mailto:jordansjones@gmail.com\">Don't Email Me</a>",
                          new MarkupBlock(
                                             factory().markup("<a"),
                                             new MarkupBlock(
                                                                new AttributeBlockCodeGenerator(
                                                                                                   "href",
                                                                                                   locationTagged(
                                                                                                                     " href=\"",
                                                                                                                     2,
                                                                                                                     0,
                                                                                                                     2
                                                                                                                 ),
                                                                                                   locationTagged(
                                                                                                                     "\"",
                                                                                                                     38,
                                                                                                                     0,
                                                                                                                     38
                                                                                                                 )
                                                                ),
                                                                factory().markup(" href=\"")
                                                                         .with(SpanCodeGenerator.Null),
                                                                factory().markup("mailto:jordansjones@gmail.com")
                                                                         .with(
                                                                                  LiteralAttributeCodeGenerator.fromValue(
                                                                                                                             locationTagged(
                                                                                                                                               9,
                                                                                                                                               0
                                                                                                                                           ),
                                                                                                                             locationTagged(
                                                                                                                                               "mailto:jordansjones@gmail.com",
                                                                                                                                               9,
                                                                                                                                               0,
                                                                                                                                               9
                                                                                                                                           )
                                                                                                                         )
                                                                              ),
                                                                factory().markup("\"").with(SpanCodeGenerator.Null)
                                             ),
                                             factory().markup(">Don't Email Me</a>").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseBlockGivesWhitespacePreceedingAtToCodeIfThereIsNoMarkupOnThatLine() {
        parseBlockTest(
                          "   <ul>" + Environment.NewLine
                          + "    @foreach(var p in Products) {" + Environment.NewLine
                          + "        <li>Product: @p.Name</li>" + Environment.NewLine
                          + "    }" + Environment.NewLine
                          + "    </ul>",
                          new MarkupBlock(
                                             factory().markup("   <ul>\r\n"),
                                             new StatementBlock(
                                                                   factory().code("    ").asStatement(),
                                                                   factory().codeTransition(),
                                                                   factory().code("foreach(var p in Products) {\r\n")
                                                                            .asStatement(),
                                                                   new MarkupBlock(
                                                                                      factory().markup("        <li>Product: "),
                                                                                      new ExpressionBlock(
                                                                                                             factory().codeTransition(),
                                                                                                             factory().code("p.Name")
                                                                                                                      .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                                                      .accepts(AcceptedCharacters.NonWhiteSpace)
                                                                                      ),
                                                                                      factory().markup("</li>\r\n")
                                                                                               .accepts(AcceptedCharacters.None)
                                                                   ),
                                                                   factory().code("    }\r\n")
                                                                            .asStatement()
                                                                            .accepts(AcceptedCharacters.None)
                                             ),
                                             factory().markup("    </ul>").accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void parseDocumentGivesWhitespacePreceedingAtToCodeIfThereIsNoMarkupOnThatLine() {
        parseDocumentTest(
                             "   <ul>" + Environment.NewLine
                             + "    @foreach(var p in Products) {" + Environment.NewLine
                             + "        <li>Product: @p.Name</li>" + Environment.NewLine
                             + "    }" + Environment.NewLine
                             + "    </ul>",
                             new MarkupBlock(
                                                factory().markup("   <ul>\r\n"),
                                                new StatementBlock(
                                                                      factory().code("    ").asStatement(),
                                                                      factory().codeTransition(),
                                                                      factory().code("foreach(var p in Products) {\r\n")
                                                                               .asStatement(),
                                                                      new MarkupBlock(
                                                                                         factory().markup("        <li>Product: "),
                                                                                         new ExpressionBlock(
                                                                                                                factory()
                                                                                                                    .codeTransition(),
                                                                                                                factory()
                                                                                                                    .code("p.Name")
                                                                                                                    .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                                                    .accepts(AcceptedCharacters.NonWhiteSpace)
                                                                                         ),
                                                                                         factory().markup("</li>\r\n")
                                                                                                  .accepts(AcceptedCharacters.None)
                                                                      ),
                                                                      factory().code("    }\r\n")
                                                                               .asStatement()
                                                                               .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().markup("    </ul>")
                             )
                         );
    }

    @Test
    public void sectionContextGivesWhitespacePreceedingAtToCodeIfThereIsNoMarkupOnThatLine() {
        parseDocumentTest(
                             "@section foo {" + Environment.NewLine
                             + "    <ul>" + Environment.NewLine
                             + "        @foreach(var p in Products) {" + Environment.NewLine
                             + "            <li>Product: @p.Name</li>" + Environment.NewLine
                             + "        }" + Environment.NewLine
                             + "    </ul>" + Environment.NewLine
                             + "}",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new SectionBlock(
                                                                    new SectionCodeGenerator("foo"),
                                                                    factory().codeTransition(),
                                                                    factory().metaCode("section foo {")
                                                                             .autoCompleteWith(null, true),
                                                                    new MarkupBlock(
                                                                                       factory().markup("\r\n    <ul>\r\n"),
                                                                                       new StatementBlock(
                                                                                                             factory().code("        ")
                                                                                                                      .asStatement(),
                                                                                                             factory().codeTransition(),
                                                                                                             factory().code("foreach(var p in Products) {\r\n")
                                                                                                                      .asStatement(),
                                                                                                             new MarkupBlock(
                                                                                                                                factory()
                                                                                                                                    .markup("            <li>Product: "),
                                                                                                                                new ExpressionBlock(
                                                                                                                                                       factory()
                                                                                                                                                           .codeTransition(),
                                                                                                                                                       factory()
                                                                                                                                                           .code("p.Name")
                                                                                                                                                           .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                                                                                           .accepts(AcceptedCharacters.NonWhiteSpace)
                                                                                                                                ),
                                                                                                                                factory()
                                                                                                                                    .markup("</li>\r\n")
                                                                                                                                    .accepts(AcceptedCharacters.None)
                                                                                                             ),
                                                                                                             factory().code("        }\r\n")
                                                                                                                      .asStatement()
                                                                                                                      .accepts(AcceptedCharacters.None)
                                                                                       ),
                                                                                       factory().markup("    </ul>\r\n")
                                                                    ),
                                                                    factory().metaCode("}")
                                                                             .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().emptyHtml()
                             )
                         );
    }

    @Test
    public void javaCodeParserDoesNotAcceptLeadingOrTrailingWhitespaceInDesignMode() {
        parseBlockTest(
                          "   <ul>" + Environment.NewLine
                          + "    @foreach(var p in Products) {" + Environment.NewLine
                          + "        <li>Product: @p.Name</li>" + Environment.NewLine
                          + "    }" + Environment.NewLine
                          + "    </ul>",
                          new MarkupBlock(
                                             factory().markup("   <ul>\r\n    "),
                                             new StatementBlock(
                                                                   factory().codeTransition(),
                                                                   factory().code("foreach(var p in Products) {\r\n        ")
                                                                            .asStatement(),
                                                                   new MarkupBlock(
                                                                                      factory().markup("<li>Product: "),
                                                                                      new ExpressionBlock(
                                                                                                             factory().codeTransition(),
                                                                                                             factory().code("p.Name")
                                                                                                                      .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                                                      .accepts(AcceptedCharacters.NonWhiteSpace)
                                                                                      ),
                                                                                      factory().markup("</li>")
                                                                                               .accepts(AcceptedCharacters.None)
                                                                   ),
                                                                   factory().code("\r\n    }")
                                                                            .asStatement()
                                                                            .accepts(AcceptedCharacters.None)
                                             ),
                                             factory().markup("\r\n    </ul>").accepts(AcceptedCharacters.None)
                          ),
                          true
                      );
    }

    @Test
    public void parseBlockTreatsTwoAtSignsAsEscapeSequence() {
        HtmlParserTestUtils.runSingleAtEscapeTest(this::parseBlockTest);
    }

    @Test
    public void parseBlockTreatsPairsOfAtSignsAsEscapeSequence() {
        HtmlParserTestUtils.runMultiAtEscapeTest(this::parseBlockTest);
    }

    @Test
    public void parseDocumentTreatsTwoAtSignsAsEscapeSequence() {
        HtmlParserTestUtils.runSingleAtEscapeTest(
                                                     this::parseDocumentTest,
                                                     AcceptedCharacters.Any
                                                 );
    }

    @Test
    public void parseDocumentTreatsPairsOfAtSignsAsEscapeSequence() {
        HtmlParserTestUtils.runMultiAtEscapeTest(
                                                    this::parseDocumentTest,
                                                    AcceptedCharacters.Any
                                                );
    }

    @Test
    public void sectionBodyTreatsTwoAtSignsAsEscapeSequence() {
        parseDocumentTest(
                             "@section Foo { <foo>@@bar</foo> }",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new SectionBlock(
                                                                    new SectionCodeGenerator("Foo"),
                                                                    factory().codeTransition(),
                                                                    factory().metaCode("section Foo {")
                                                                             .autoCompleteWith(null, true),
                                                                    new MarkupBlock(
                                                                                       factory().markup(" <foo>"),
                                                                                       factory().markup("@").hidden(),
                                                                                       factory().markup("@bar</foo> ")
                                                                    ),
                                                                    factory().metaCode("}")
                                                                             .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().emptyHtml()
                             )
                         );
    }

    @Test
    public void sectionBodyTreatsPairsOfAtSignsAsEscapeSequence() {
        parseDocumentTest(
                             "@section Foo { <foo>@@@@@bar</foo> }",
                             new MarkupBlock(
                                                factory().emptyHtml(),
                                                new SectionBlock(
                                                                    new SectionCodeGenerator("Foo"),
                                                                    factory().codeTransition(),
                                                                    factory().metaCode("section Foo {")
                                                                             .autoCompleteWith(null, true),
                                                                    new MarkupBlock(
                                                                                       factory().markup(" <foo>"),
                                                                                       factory().markup("@").hidden(),
                                                                                       factory().markup("@"),
                                                                                       factory().markup("@").hidden(),
                                                                                       factory().markup("@"),
                                                                                       new ExpressionBlock(
                                                                                                              factory().codeTransition(),
                                                                                                              factory().code("bar")
                                                                                                                       .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                                                       .accepts(AcceptedCharacters.NonWhiteSpace)
                                                                                       ),
                                                                                       factory().markup("</foo> ")
                                                                    ),
                                                                    factory().metaCode("}")
                                                                             .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().emptyHtml()
                             )
                         );
    }
}
