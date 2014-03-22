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
import nextmethod.web.razor.generator.HelperCodeGenerator;
import nextmethod.web.razor.generator.SectionCodeGenerator;
import nextmethod.web.razor.generator.StatementCodeGenerator;
import nextmethod.web.razor.parser.SyntaxConstants;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.DirectiveBlock;
import nextmethod.web.razor.parser.syntaxtree.FunctionsBlock;
import nextmethod.web.razor.parser.syntaxtree.HelperBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.SectionBlock;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

/**
 *
 */
public class JavaDirectivesTest extends JavaHtmlCodeParserTestBase {

    @Test
    public void inheritsDirective() {
        testInheritsDirective("System.Web.WebPages.WebPage");
    }

    @Test
    public void inheritsDirectiveSupportsArrays() {
        testInheritsDirective("string[[]][]");
    }

    @Test
    public void inheritsDirectiveSupportsNestedGenerics() {
        testInheritsDirective("System.Web.Mvc.WebViewPage<IEnumerable<MvcApplication2.Models.RegisterModel>>");
    }

    @Test
    public void inheritsDirectiveSupportsTypeKeywords() {
        testInheritsDirective("string");
    }

    @Test
    public void inheritsDirectiveSupportsVSTemplateTokens() { // NOTE: Is this really needed (VS)?
        testInheritsDirective("$rootnamespace$.MyBase");
    }

    private void testInheritsDirective(final String type) {
        parseBlockTest(
                          "@inherits " + type,
                          new DirectiveBlock(
                                                factory().codeTransition(),
                                                factory().metaCode(SyntaxConstants.Java.InheritsKeyword + " ")
                                                         .accepts(AcceptedCharacters.None),
                                                factory().code(type)
                                                         .asBaseType(type)
                          )
                      );
    }


    @Test
    public void sessionStateDirectiveWorks() {
        final String keyword = "InProc";
        parseBlockTest(
                          "@sessionstate " + keyword,
                          new DirectiveBlock(
                                                factory().codeTransition(),
                                                factory().metaCode(SyntaxConstants.Java.SessionStateKeyword + " ")
                                                         .accepts(AcceptedCharacters.None),
                                                factory().code(keyword)
                                                         .asRazorDirectiveAnnotation("sessionstate", keyword)
                          )
                      );
    }

    @Test
    public void sessionStateDirectiveParsesInvalidSessionValue() {
        final String keyword = "Blah";
        parseBlockTest(
                          "@sessionstate " + keyword,
                          new DirectiveBlock(
                                                factory().codeTransition(),
                                                factory().metaCode(SyntaxConstants.Java.SessionStateKeyword + " ")
                                                         .accepts(AcceptedCharacters.None),
                                                factory().code(keyword)
                                                         .asRazorDirectiveAnnotation("sessionstate", keyword)
                          )
                      );
    }


    @Test
    public void functionsDirective() {
        parseBlockTest(
                          "@functions { foo(); bar(); }",
                          new FunctionsBlock(
                                                factory().codeTransition(),
                                                factory().metaCode(SyntaxConstants.Java.FunctionsKeyword + " {")
                                                         .accepts(AcceptedCharacters.None),
                                                factory().code(" foo(); bar(); ")
                                                         .asFunctionsBody(),
                                                factory().metaCode("}")
                                                         .accepts(AcceptedCharacters.None)
                          )
                      );
    }

    @Test
    public void emptyFunctionsDirective() {
        parseBlockTest(
                          "@functions { }",
                          new FunctionsBlock(
                                                factory().codeTransition(),
                                                factory().metaCode(SyntaxConstants.Java.FunctionsKeyword + " {")
                                                         .accepts(AcceptedCharacters.None),
                                                factory().code(" ")
                                                         .asFunctionsBody(),
                                                factory().metaCode("}")
                                                         .accepts(AcceptedCharacters.None)
                          )
                      );
    }


    @Test
    public void sectionDirective() {
        parseBlockTest(
                          "@section Header { <p>F{o}o</p> }",
                          new SectionBlock(
                                              new SectionCodeGenerator("Header"),
                                              factory().codeTransition(),
                                              factory().metaCode("section Header {")
                                                       .autoCompleteWith(null, true)
                                                       .accepts(AcceptedCharacters.Any),
                                              new MarkupBlock(
                                                                 factory().markup(" <p>F", "{", "o", "}", "o", "</p> ")
                                              ),
                                              factory().metaCode("}")
                                                       .accepts(AcceptedCharacters.None)
                          )
                      );
    }


    @Test
    public void helperDirective() {
        parseBlockTest(
                          "@helper Strong(string value) { foo(); }",
                          new HelperBlock(
                                             new HelperCodeGenerator(
                                                                        new LocationTagged<>(
                                                                                                "Strong(string value) {",
                                                                                                new SourceLocation(
                                                                                                                      8,
                                                                                                                      0,
                                                                                                                      8
                                                                                                )
                                                                        ), true
                                             ),
                                             factory().codeTransition(),
                                             factory().metaCode("helper ")
                                                      .accepts(AcceptedCharacters.None),
                                             factory().code("Strong(string value) {")
                                                      .hidden()
                                                      .accepts(AcceptedCharacters.None),
                                             new StatementBlock(
                                                                   factory().code(" foo(); ")
                                                                            .asStatement()
                                                                            .with(new StatementCodeGenerator())
                                             ),
                                             factory().code("}")
                                                      .hidden()
                                                      .accepts(AcceptedCharacters.None)
                          )
                      );
    }

}
