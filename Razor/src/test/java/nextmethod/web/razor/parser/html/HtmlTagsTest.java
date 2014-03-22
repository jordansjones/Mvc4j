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

import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class HtmlTagsTest extends JavaHtmlMarkupParserTestBase {

    private static String[] voidElementNames = new String[]{
                                                               "area",
                                                               "base",
                                                               "br",
                                                               "col",
                                                               "command",
                                                               "embed",
                                                               "hr",
                                                               "img",
                                                               "input",
                                                               "keygen",
                                                               "link",
                                                               "meta",
                                                               "param",
                                                               "source",
                                                               "track",
                                                               "wbr"
    };

    @Test
    public void emptyTagNestsLikeNormalTag() {
        parseBlockTest(
                          "<p></> Bar",
                          new MarkupBlock(
                                             factory().markup("<p></> ").accepts(AcceptedCharacters.None)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorMissingEndTag("p"),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void emptyTag() {
        parseTagTest("<></> ", "Bar");
    }

    @Test
    public void commentTag() {
        parseTagTest("<!--Foo--> ", "Bar");
    }

    @Test
    public void docTypeTag() {
        parseTagTest("<!DOCTYPE html> ", "Bar");
    }

    @Test
    public void processingInstructionTag() {
        parseTagTest("<?xml version=\"1.0\" ?> ", "Bar");
    }

    @Test
    public void elementTags() {
        parseTagTest("<p>Foo</p> ", "Bar");
    }

    @Test
    public void textTags() {
        parseBlockTest(
                          "<text>Foo</text>}",
                          new MarkupBlock(
                                             factory().markupTransition("<text>"),
                                             factory().markup("Foo"),
                                             factory().markupTransition("</text>")
                          )
                      );
    }

    @Test
    public void cDataTag() {
        parseTagTest("<![CDATA[Foo]]> ", "Bar");
    }

    @Test
    public void scriptTag() {
        parseDocumentTest(
                             "<script>foo < bar && quantity.toString() !== orderQty.val()</script>",
                             new MarkupBlock(
                                                factory().markup("<script>foo < bar && quantity.toString() !== orderQty.val()</script>")
                             )
                         );
    }

    @Test
    public void voidElementFollowedByContent() {
        for (String elementName : voidElementNames) {
            parseElementFollowedByTest(elementName, "foo");
        }
    }

    @Test
    public void voidElementFollowedByOtherTag() {
        for (String elementName : voidElementNames) {
            parseElementFollowedByTest(elementName, "<other>foo");
        }
    }

    @Test
    public void voidElementFollowedByCloseTag() {
        for (String elementName : voidElementNames) {
            parseTagTest(String.format("<%1$s> </%1$s>", elementName), "foo");
        }
    }

    @Test
    public void incompleteVoidElementEndTag() {
        for (String elementName : voidElementNames) {
            final String exepcted = String.format("<%1$s></%1$s", elementName);
            parseBlockTest(
                              exepcted,
                              new MarkupBlock(
                                                 factory().markup(exepcted).accepts(AcceptedCharacters.Any)
                              )
                          );
        }
    }

    private void parseElementFollowedByTest(final String tagName, final String followedBy) {
        parseTagTest(String.format("<%s>", tagName), followedBy);
    }

    private void parseTagTest(final String tag, final String content) {
        parseBlockTest(
                          tag + content,
                          new MarkupBlock(
                                             factory().markup(tag).accepts(AcceptedCharacters.None)
                          )
                      );
    }
}
