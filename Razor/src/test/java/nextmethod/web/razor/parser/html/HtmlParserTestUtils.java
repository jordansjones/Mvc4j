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

import java.util.EnumSet;
import javax.annotation.Nonnull;

import nextmethod.base.Delegates;
import nextmethod.web.razor.framework.SpanFactory;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;

final class HtmlParserTestUtils {

    private HtmlParserTestUtils() {}

    public static void runSingleAtEscapeTest(@Nonnull final Delegates.IAction2<String, Block> testMethod) {
        runSingleAtEscapeTest(testMethod, AcceptedCharacters.None);
    }

    public static void runSingleAtEscapeTest(@Nonnull final Delegates.IAction2<String, Block> testMethod,
                                             final AcceptedCharacters lastSpanAcceptedCharacters
                                            ) {
        runSingleAtEscapeTest(testMethod, EnumSet.of(lastSpanAcceptedCharacters));
    }

    public static void runSingleAtEscapeTest(@Nonnull final Delegates.IAction2<String, Block> testMethod,
                                             final EnumSet<AcceptedCharacters> lastSpanAcceptedCharacters
                                            ) {
        final SpanFactory factory = SpanFactory.createJavaHtml();
        testMethod.invoke(
                             "<foo>@@bar</foo>",
                             new MarkupBlock(
                                                factory.markup("<foo>"),
                                                factory.markup("@").hidden(),
                                                factory.markup("@bar</foo>").accepts(lastSpanAcceptedCharacters)
                             )
                         );
    }

    public static void runMultiAtEscapeTest(@Nonnull final Delegates.IAction2<String, Block> testMethod) {
        runMultiAtEscapeTest(testMethod, AcceptedCharacters.None);
    }

    public static void runMultiAtEscapeTest(@Nonnull final Delegates.IAction2<String, Block> testMethod,
                                            final AcceptedCharacters lastSpanAcceptedCharacters
                                           ) {
        runMultiAtEscapeTest(testMethod, EnumSet.of(lastSpanAcceptedCharacters));
    }

    public static void runMultiAtEscapeTest(@Nonnull final Delegates.IAction2<String, Block> testMethod,
                                            final EnumSet<AcceptedCharacters> lastSpanAcceptedCharacters
                                           ) {
        final SpanFactory factory = SpanFactory.createJavaHtml();
        testMethod.invoke(
                             "<foo>@@@@@bar</foo>",
                             new MarkupBlock(
                                                factory.markup("<foo>"),
                                                factory.markup("@").hidden(),
                                                factory.markup("@"),
                                                factory.markup("@").hidden(),
                                                factory.markup("@"),
                                                new ExpressionBlock(
                                                                       factory.codeTransition(),
                                                                       factory.code("bar")
                                                                              .asImplicitExpression(JavaCodeParser.DefaultKeywords)
                                                                              .accepts(AcceptedCharacters.NonWhiteSpace)
                                                ),
                                                factory.markup("</foo>").accepts(lastSpanAcceptedCharacters)
                             )
                         );
    }
}
