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

package nextmethod.web.razor.parser;

import javax.annotation.Nonnull;

import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.KeyValue;
import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.ParserResults;
import nextmethod.web.razor.framework.ParserTestBase;
import nextmethod.web.razor.framework.SpanFactory;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.StringReaderDelegate;
import nextmethod.web.razor.text.TextReader;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 *
 */
public class RazorParserTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullCodeParser() {
        new RazorParser(null, new HtmlMarkupParser());
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullMarkupParser() {
        new RazorParser(new JavaCodeParser(), null);
    }

    @Test
    public void parseMethodCallsParseDocumentOnMarkupParserAndReturnsResults() {
        final SpanFactory factory = SpanFactory.createJavaHtml();
        final RazorParser parser = new RazorParser(new JavaCodeParser(), new HtmlMarkupParser());

        ParserTestBase.evaluateResults(
                                          parser.parse(new StringReaderDelegate("foo @bar baz")),
                                          new MarkupBlock(
                                                             factory.markup("foo "),
                                                             new ExpressionBlock(
                                                                                    factory.codeTransition(),
                                                                                    factory.code("bar")
                                                                                           .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                           .accepts(AcceptedCharacters.NonWhiteSpace)
                                                             ),
                                                             factory.markup(" baz")
                                          )
                                      );
    }

    @Test
    public void parseMethodUsesProvidedParserListenerIfSpecified() {
        final SpanFactory factory = SpanFactory.createJavaHtml();

        final RazorParser parser = new RazorParser(new JavaCodeParser(), new HtmlMarkupParser());

        final ParserResults results = parser.parse(new StringReaderDelegate("foo @bar baz"));

        ParserTestBase.evaluateResults(
                                          results,
                                          new MarkupBlock(
                                                             factory.markup("foo "),
                                                             new ExpressionBlock(
                                                                                    factory.codeTransition(),
                                                                                    factory.code("bar")
                                                                                           .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                                           .accepts(AcceptedCharacters.NonWhiteSpace)
                                                             ),
                                                             factory.markup(" baz")
                                          )
                                      );
    }

    @Test
    public void parseMethodSetsUpRunWithSpecifiedCodeParserMarkupParserAndListenerPassesToMarkupParser() {
        runParseWithListenerTest(
                                    (parser, reader) -> {
                                        assert parser != null;
                                        assert reader != null;
                                        parser.parse(reader);
                                    }
                                );
    }

    private void runParseWithListenerTest(final Delegates.IAction2<RazorParser, TextReader> parserAction) {
        final MockMarkupParser markupParser = new MockMarkupParser();
        final JavaCodeParser codeParser = new JavaCodeParser();
        final RazorParser parser = new RazorParser(codeParser, markupParser);
        final TextReader expectedReader = new StringReaderDelegate("foo");

        parserAction.invoke(parser, expectedReader);

        final ParserContext actualContext = markupParser.getContext();
        assertNotNull(actualContext);
        assertSame(markupParser, actualContext.getMarkupParser());
        assertSame(markupParser, actualContext.getActiveParser());
        assertSame(codeParser, actualContext.getCodeParser());
    }

    private static class MockMarkupParser extends ParserBase {

        @Override
        public boolean isMarkerParser() {
            return true;
        }

        @Override
        protected ParserBase getOtherParser() {
            return getContext().getCodeParser();
        }

        @SuppressWarnings("EmptyTryBlock")
        @Override
        public void parseDocument() {
            try (IDisposable ignored = getContext().startBlock(BlockType.Markup)) {

            }
        }

        @SuppressWarnings("EmptyTryBlock")
        @Override
        public void parseSection(@Nonnull final KeyValue<String, String> nestingSequence, final boolean caseSensitive) {
            try (IDisposable ignored = getContext().startBlock(BlockType.Markup)) {

            }
        }

        @SuppressWarnings("EmptyTryBlock")
        @Override
        public void parseBlock() {
            try (IDisposable ignored = getContext().startBlock(BlockType.Markup)) {

            }
        }

        @Override
        public void buildSpan(@Nonnull final SpanBuilder span, @Nonnull final SourceLocation start,
                              @Nonnull final String content
                             ) {
            throw new NotImplementedException();
        }
    }

}
