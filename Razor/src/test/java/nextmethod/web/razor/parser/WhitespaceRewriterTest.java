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

import nextmethod.web.razor.framework.ParserTestBase;
import nextmethod.web.razor.framework.SpanFactory;
import nextmethod.web.razor.parser.internal.WhiteSpaceRewriter;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import org.junit.Test;

/**
 *
 */
public class WhitespaceRewriterTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullSymbolConverter() {
        new WhiteSpaceRewriter(null);
    }

    @Test
    public void rewriteMovesWhitespacePreceedingExpressionBlockToParentBlock() {
        final SpanFactory factory = SpanFactory.createJavaHtml();
        final Block start = new MarkupBlock(
                                               factory.markup("test"),
                                               new ExpressionBlock(
                                                                      factory.code("    ").asExpression(),
                                                                      factory.codeTransition(SyntaxConstants.TransitionString),
                                                                      factory.code("foo").asExpression()
                                               ),
                                               factory.markup("test")
        );

        final WhiteSpaceRewriter rewriter = new WhiteSpaceRewriter(new HtmlMarkupParser().createBuildSpanDelegate());

        final Block actual = rewriter.rewrite(start);

        factory.reset();

        ParserTestBase.evaluateParseTree(
                                            actual,
                                            new MarkupBlock(
                                                               factory.markup("test"),
                                                               factory.markup("    "),
                                                               new ExpressionBlock(
                                                                                      factory.codeTransition(SyntaxConstants.TransitionString),
                                                                                      factory.code("foo").asExpression()
                                                               ),
                                                               factory.markup("test")
                                            )
                                        );
    }
}
