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

package nextmethod.web.razor.framework;

import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nonnull;

import nextmethod.web.razor.parser.ParserBase;
import nextmethod.web.razor.parser.SyntaxConstants;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;

/**
 *
 */
public abstract class CodeParserTestBase extends ParserTestBase {

    protected abstract Set<String> getKeywordSet();

    @Override
    protected ParserBase selectActiveParser(@Nonnull final ParserBase codeParser,
                                            @Nonnull final ParserBase markupParser
                                           ) {
        return codeParser;
    }

    protected void implicitExpressionTest(final String input, final RazorError... errors) {
        implicitExpressionTest(input, AcceptedCharacters.NonWhiteSpace, errors);
    }

    protected void implicitExpressionTest(final String input, final EnumSet<AcceptedCharacters> acceptedCharacters,
                                          final RazorError... errors
                                         ) {
        implicitExpressionTest(input, input, acceptedCharacters, errors);
    }

    protected void implicitExpressionTest(final String input, final AcceptedCharacters acceptedCharacters,
                                          final RazorError... errors
                                         ) {
        implicitExpressionTest(input, input, acceptedCharacters, errors);
    }

    protected void implicitExpressionTest(final String input, final String expected, final RazorError... errors) {
        implicitExpressionTest(input, expected, AcceptedCharacters.NonWhiteSpace, errors);
    }

    protected void implicitExpressionTest(final String input, final String expected,
                                          final AcceptedCharacters acceptedCharacters, final RazorError... errors
                                         ) {
        implicitExpressionTest(input, expected, EnumSet.of(acceptedCharacters), errors);
    }


    protected void implicitExpressionTest(final String input, final String expected,
                                          final EnumSet<AcceptedCharacters> acceptedCharacters,
                                          final RazorError... errors
                                         ) {
        final SpanFactory factory = createSpanFactory();
        parseBlockTest(
                          SyntaxConstants.TransitionString + input,
                          new ExpressionBlock(
                                                 factory.codeTransition().build(),
                                                 factory.code(expected)
                                                        .asImplicitExpression(getKeywordSet())
                                                        .accepts(acceptedCharacters).build()
                          ),
                          errors
                      );
    }

    @Override
    protected void singleSpanBlockTest(final String document, final BlockType blockType, final SpanKind spanType,
                                       final EnumSet<AcceptedCharacters> acceptedCharacters
                                      ) {
        singleSpanBlockTest(document, blockType, spanType, acceptedCharacters, new RazorError[0]);
    }

    @Override
    protected void singleSpanBlockTest(final String document, final String spanContent, final BlockType blockType,
                                       final SpanKind spanType, final EnumSet<AcceptedCharacters> acceptedCharacters
                                      ) {
        singleSpanBlockTest(document, spanContent, blockType, spanType, acceptedCharacters, new RazorError[0]);
    }

    @Override
    protected void singleSpanBlockTest(final String document, final BlockType blockType, final SpanKind spanType,
                                       final RazorError... expectedError
                                      ) {
        singleSpanBlockTest(document, document, blockType, spanType, expectedError);
    }

    @Override
    protected void singleSpanBlockTest(final String document, final String spanContent, final BlockType blockType,
                                       final SpanKind spanType, final RazorError... expectedError
                                      ) {
        singleSpanBlockTest(document, spanContent, blockType, spanType, AcceptedCharacters.Any, expectedError);
    }

    @Override
    protected void singleSpanBlockTest(final String document, final BlockType blockType, final SpanKind spanType,
                                       final EnumSet<AcceptedCharacters> acceptedCharacters,
                                       final RazorError... expectedErrors
                                      ) {
        singleSpanBlockTest(document, document, blockType, spanType, acceptedCharacters, expectedErrors);
    }

    @Override
    protected void singleSpanBlockTest(final String document, final String spanContent, final BlockType blockType,
                                       final SpanKind spanType, final EnumSet<AcceptedCharacters> acceptedCharacters,
                                       final RazorError... expectedErrors
                                      ) {
        final Block b = createSimpleBlockAndSpan(spanContent, blockType, spanType, acceptedCharacters);
        parseBlockTest(document, b, expectedErrors != null
                                    ? expectedErrors
                                    : new RazorError[0]
                      );
    }
}
