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

import nextmethod.base.Strings;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.generator.AttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.LiteralAttributeCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class HtmlErrorTest extends JavaHtmlMarkupParserTestBase {

    @Test
    public void parseBlockAllowsInvalidTagNamesAsLongAsParserCanIdentifyEndTag() {
        singleSpanBlockTest("<1-foo+bar>foo</1-foo+bar>", BlockType.Markup, SpanKind.Markup, AcceptedCharacters.None);
    }

    @Test
    public void parseBlockThrowsErrorIfStartTextTagContainsTextAfterName() {
        parseBlockTest(
                          "<text foo bar></text>",
                          new MarkupBlock(
                                             factory().markupTransition("<text").accepts(AcceptedCharacters.Any),
                                             factory().markup(" foo bar>"),
                                             factory().markupTransition("</text>")
                          ),
                          new RazorError(
                                            RazorResources().parseErrorTextTagCannotContainAttributes(),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockThrowsErrorIfEndTextTagContainsTextAfterName() {
        parseBlockTest(
                          "<text></text foo bar>",
                          new MarkupBlock(
                                             factory().markupTransition("<text>"),
                                             factory().markupTransition("</text").accepts(AcceptedCharacters.Any),
                                             factory().markup(" ")
                          ),
                          new RazorError(
                                            RazorResources().parseErrorTextTagCannotContainAttributes(),
                                            6, 0, 6
                          )
                      );
    }

    @Test
    public void parseBlockThrowsExceptionIfBlockDoesNotStartWithTag() {
        parseBlockTest(
                          "foo bar <baz>",
                          new MarkupBlock(
                          ),
                          new RazorError(
                                            RazorResources().parseErrorMarkupBlockMustStartWithTag(),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockStartingWithEndTagProducesRazorErrorThenOutputsMarkupSegmentAndEndsBlock() {
        parseBlockTest(
                          "</foo> bar baz",
                          new MarkupBlock(
                                             factory().markup("</foo> ").accepts(AcceptedCharacters.None)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorUnexpectedEndTag("foo"),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockWithUnclosedTopLevelTagThrowsMissingEndTagParserExceptionOnOutermostUnclosedTag() {
        parseBlockTest(
                          "<p><foo></bar>",
                          new MarkupBlock(
                                             factory().markup("<p><foo></bar>").accepts(AcceptedCharacters.None)
                          ),
                          new RazorError(
                                            RazorResources().parseErrorMissingEndTag("p"),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockWithUnclosedTagAtEOFThrowsMissingEndTagException() {
        parseBlockTest(
                          "<foo>blah blah blah blah blah",
                          new MarkupBlock(
                                             factory().markup("<foo>blah blah blah blah blah")
                          ),
                          new RazorError(
                                            RazorResources().parseErrorMissingEndTag("foo"),
                                            SourceLocation.Zero
                          )
                      );
    }

    @Test
    public void parseBlockWithUnfinishedTagAtEOFThrowsIncompleteTagException() {
        parseBlockTest(
                          "<foo bar=baz",
                          new MarkupBlock(
                                             factory().markup("<foo"),
                                             new MarkupBlock(
                                                                new AttributeBlockCodeGenerator(
                                                                                                   "bar",
                                                                                                   new LocationTagged<>(
                                                                                                                           " bar=",
                                                                                                                           4,
                                                                                                                           0,
                                                                                                                           4
                                                                                                   ),
                                                                                                   new LocationTagged<>(
                                                                                                                           Strings.Empty,
                                                                                                                           12,
                                                                                                                           0,
                                                                                                                           12
                                                                                                   )
                                                                ),
                                                                factory().markup(" bar=").with(SpanCodeGenerator.Null),
                                                                factory().markup("baz")
                                                                         .with(
                                                                                  LiteralAttributeCodeGenerator.fromValue(
                                                                                                                             new LocationTagged<String>(
                                                                                                                                                           Strings.Empty,
                                                                                                                                                           9,
                                                                                                                                                           0,
                                                                                                                                                           9
                                                                                                                             ),
                                                                                                                             new LocationTagged<String>(
                                                                                                                                                           "baz",
                                                                                                                                                           9,
                                                                                                                                                           0,
                                                                                                                                                           9
                                                                                                                             )
                                                                                                                         )
                                                                              )
                                             )
                          ),
                          new RazorError(
                                            RazorResources().parseErrorUnfinishedTag("foo"),
                                            SourceLocation.Zero
                          )
                      );
    }

}
