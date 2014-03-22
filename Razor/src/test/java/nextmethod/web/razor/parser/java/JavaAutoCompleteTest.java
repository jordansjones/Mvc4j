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
import nextmethod.web.razor.editor.AutoCompleteEditHandler;
import nextmethod.web.razor.framework.Environment;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.generator.HelperCodeGenerator;
import nextmethod.web.razor.generator.MarkupCodeGenerator;
import nextmethod.web.razor.generator.SectionCodeGenerator;
import nextmethod.web.razor.generator.StatementCodeGenerator;
import nextmethod.web.razor.parser.JavaLanguageCharacteristics;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.FunctionsBlock;
import nextmethod.web.razor.parser.syntaxtree.HelperBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SectionBlock;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class JavaAutoCompleteTest extends JavaHtmlCodeParserTestBase {

    @Test
    public void functionsDirectiveAutoCompleteAtEOF() {
        parseBlockTest(
                          "@functions{",
                          new FunctionsBlock(
                                                factory().codeTransition("@").accepts(AcceptedCharacters.None),
                                                factory().metaCode("functions{").accepts(AcceptedCharacters.None),
                                                factory().emptyJava()
                                                         .asFunctionsBody()
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
                                                                                                      "functions",
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            1, 0, 1
                          )
                      );
    }

    @Test
    public void helperDirectiveAutoCompleteAtEOF() {
        parseBlockTest(
                          "@helper Strong(string value) {",
                          new HelperBlock(
                                             new HelperCodeGenerator(
                                                                        new LocationTagged<>(
                                                                                                "Strong(string value) {",
                                                                                                8, 0, 8
                                                                        ), true
                                             ),
                                             factory().codeTransition(),
                                             factory().metaCode("helper ").accepts(AcceptedCharacters.None),
                                             factory().code("Strong(string value) {")
                                                      .hidden().accepts(AcceptedCharacters.None),
                                             new StatementBlock(
                                                                   factory().emptyJava().asStatement().with(
                                                                                                               new AutoCompleteEditHandler(
                                                                                                                                              JavaLanguageCharacteristics.Instance
                                                                                                                                                  .createTokenizeStringDelegate()
                                                                                                               ) {{
                                                                                                                   this.setAutoCompleteString("}");
                                                                                                               }}
                                                                                                           )
                                             )
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      "helper",
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            1, 0, 1
                          )
                      );
    }

    @Test
    public void sectionDirectiveAutoCompleteAtEOF() {
        parseBlockTest(
                          "@section Header {",
                          new SectionBlock(
                                              new SectionCodeGenerator("Header"),
                                              factory().codeTransition(),
                                              factory().metaCode("section Header {")
                                                       .autoCompleteWith("}", true)
                                                       .accepts(AcceptedCharacters.Any),
                                              new MarkupBlock()
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedX(
                                                                                    "}"
                                                                                ),
                                            17, 0, 17
                          )
                      );
    }

    @Test
    public void verbatimBlockAutoCompleteAtEOF() {
        parseBlockTest(
                          "@{",
                          new StatementBlock(
                                                factory().codeTransition(),
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().emptyJava().asStatement()
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
                                            1, 0, 1
                          )
                      );
    }

    @Test
    public void functionsDirectiveAutoCompleteAtStartOfFile() {
        parseBlockTest(
                          "@functions{" + Environment.NewLine + "foo",
                          new FunctionsBlock(
                                                factory().codeTransition("@").accepts(AcceptedCharacters.None),
                                                factory().metaCode("functions{").accepts(AcceptedCharacters.None),
                                                factory().code(Environment.NewLine + "foo").asFunctionsBody()
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
                                                                                                      "functions", "}",
                                                                                                      "{"
                                                                                                  ),
                                            1, 0, 1
                          )
                      );
    }

    @Test
    public void helperDirectiveAutoCompleteAtStartOfFile() {
        parseBlockTest(
                          "@helper Strong(string value) {" + Environment.NewLine + "<p></p>",
                          new HelperBlock(
                                             new HelperCodeGenerator(
                                                                        new LocationTagged<>(
                                                                                                "Strong(string value) {",
                                                                                                8, 0, 8
                                                                        ), true
                                             ),
                                             factory().codeTransition(),
                                             factory().metaCode("helper ").accepts(AcceptedCharacters.None),
                                             factory().code("Strong(string value) {")
                                                      .hidden()
                                                      .accepts(AcceptedCharacters.None),
                                             new StatementBlock(
                                                                   factory().code(Environment.NewLine).asStatement()
                                                                            .with(
                                                                                     new AutoCompleteEditHandler(
                                                                                                                    JavaLanguageCharacteristics.Instance
                                                                                                                        .createTokenizeStringDelegate()
                                                                                     ) {{
                                                                                         this.setAutoCompleteString("}");
                                                                                     }}
                                                                                 ),
                                                                   new MarkupBlock(
                                                                                      factory().markup("<p></p>")
                                                                                               .with(new MarkupCodeGenerator())
                                                                                               .accepts(AcceptedCharacters.None)
                                                                   ),
                                                                   factory().span(
                                                                                     SpanKind.Code,
                                                                                     new JavaSymbol(
                                                                                                       factory().getLocationTracker()
                                                                                                                .getCurrentLocation(),
                                                                                                       Strings.Empty,
                                                                                                       JavaSymbolType.Unknown
                                                                                     )
                                                                                 ).with(new StatementCodeGenerator())
                                             )
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof("helper", "}", "{"),
                                            1, 0, 1
                          )
                      );
    }

    @Test
    public void sectionDirectiveAutoCompleteAtStartOfFile() {
        parseBlockTest(
                          "@section Header {" + Environment.NewLine + "<p>Foo</p>",
                          new SectionBlock(
                                              new SectionCodeGenerator("Header"),
                                              factory().codeTransition(),
                                              factory().metaCode("section Header {")
                                                       .autoCompleteWith("}", true)
                                                       .accepts(AcceptedCharacters.Any),
                                              new MarkupBlock(factory().markup(Environment.NewLine + "<p>Foo</p>"))
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedX("}"),
                                            29, 1, 10
                          )
                      );
    }

    @Test
    public void verbatimBlockAutoCompleteAtStartOfFile() {
        parseBlockTest(
                          "@{" + Environment.NewLine + "<p></p>",
                          new StatementBlock(
                                                factory().codeTransition(),
                                                factory().metaCode("{").accepts(AcceptedCharacters.None),
                                                factory().code(Environment.NewLine).asStatement()
                                                         .with(
                                                                  new AutoCompleteEditHandler(
                                                                                                 JavaLanguageCharacteristics.Instance
                                                                                                     .createTokenizeStringDelegate()
                                                                  ) {{ this.setAutoCompleteString("}"); }}
                                                              ),
                                                new MarkupBlock(
                                                                   factory().markup("<p></p>")
                                                                            .with(new MarkupCodeGenerator())
                                                                            .accepts(AcceptedCharacters.None)
                                                ),
                                                factory().span(
                                                                  SpanKind.Code,
                                                                  new JavaSymbol(
                                                                                    factory().getLocationTracker()
                                                                                             .getCurrentLocation(),
                                                                                    Strings.Empty,
                                                                                    JavaSymbolType.Unknown
                                                                  )
                                                              )
                                                         .with(new StatementCodeGenerator())
                          ),
                          new RazorError(
                                            RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                      RazorResources().blockNameCode(),
                                                                                                      "}",
                                                                                                      "{"
                                                                                                  ),
                                            1, 0, 1
                          )
                      );
    }

}
