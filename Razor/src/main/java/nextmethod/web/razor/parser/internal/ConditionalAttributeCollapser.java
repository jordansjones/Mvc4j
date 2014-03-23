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

package nextmethod.web.razor.parser.internal;

import java.util.Collection;
import javax.annotation.Nonnull;

import nextmethod.annotations.Internal;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.web.razor.editor.SpanEditHandler;
import nextmethod.web.razor.generator.AttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.generator.LiteralAttributeCodeGenerator;
import nextmethod.web.razor.generator.MarkupCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.HtmlTokenizer;

import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.base.TypeHelpers.typeIs;

/**
 *
 */
@Internal
public class ConditionalAttributeCollapser extends MarkupRewriter {

    public ConditionalAttributeCollapser(@Nonnull
                                         final Delegates.IAction3<SpanBuilder, SourceLocation, String> markupSpanFactory
                                        ) {
        super(markupSpanFactory);
    }

    @Override
    protected boolean canRewrite(@Nonnull final Block block) {
        final AttributeBlockCodeGenerator gen = typeAs(block.getCodeGenerator(), AttributeBlockCodeGenerator.class);
        return gen != null && !block.getChildren().isEmpty() && block.getChildren().stream().allMatch(ConditionalAttributeCollapser::isLiteralAttributeValue);
    }

    @Override
    protected SyntaxTreeNode rewriteBlock(@Nonnull final BlockBuilder parent, @Nonnull final Block block) {
        // Collect the content of this node
        final String content = concatNodeContent(block.getChildren());

        final SyntaxTreeNode first = parent.getChildren().stream().findFirst().orElse(null);
        assert first != null;

        // Create a new span containing this content
        final SpanBuilder span = new SpanBuilder();
        span.setEditHandler(new SpanEditHandler(HtmlTokenizer.createTokenizeDelegate()));
        fillSpan(span, first.getStart(), content);
        return span.build();
    }

    private String concatNodeContent(final Collection<SyntaxTreeNode> nodes) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (SyntaxTreeNode node : nodes) {
            final Span span = typeAs(node, Span.class);
            if (span != null) {
                stringBuilder.append(span.getContent());
            }
        }
        return stringBuilder.toString();
    }

    private static boolean isLiteralAttributeValue(SyntaxTreeNode node) {
        if (node == null) return false;
        if (node.isBlock()) return false;

        final Span span = typeAs(node, Span.class);
        if (Debug.isAssertEnabled()) assert span != null;

        if (span == null) { return false; }

        final ISpanCodeGenerator codeGen = span.getCodeGenerator();

        final LiteralAttributeCodeGenerator litGen = typeAs(codeGen, LiteralAttributeCodeGenerator.class);

        return (litGen != null && litGen.getValueGenerator() == null)
               || codeGen == SpanCodeGenerator.Null
               || typeIs(codeGen, MarkupCodeGenerator.class);
    }
}
