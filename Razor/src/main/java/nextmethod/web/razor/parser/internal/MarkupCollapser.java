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

import javax.annotation.Nonnull;

import com.google.common.collect.Iterables;
import nextmethod.annotations.Internal;
import nextmethod.base.Delegates;
import nextmethod.web.razor.generator.MarkupCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.SourceLocation;

import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.base.TypeHelpers.typeIs;

/**
 *
 */
@Internal
public class MarkupCollapser extends MarkupRewriter {

    public MarkupCollapser(@Nonnull final Delegates.IAction3<SpanBuilder, SourceLocation, String> markupSpanFactory) {
        super(markupSpanFactory);
    }

    @Override
    protected boolean canRewrite(@Nonnull final Span span) {
        return span.getKind() == SpanKind.Markup && typeIs(span.getCodeGenerator(), MarkupCodeGenerator.class);
    }

    @Override
    protected SyntaxTreeNode rewriteSpan(@Nonnull final BlockBuilder parent, @Nonnull final Span span) {
        // Only rewrite if we have a previous that is also markup (canRewrite does this check for us!)
        final Span previous = typeAs(Iterables.getLast(parent.getChildren(), null), Span.class);
        if (previous == null || !canRewrite(previous)) {
            return span;
        }

        // Merge spans
        parent.getChildren().remove(previous);
        final SpanBuilder merged = new SpanBuilder();
        fillSpan(merged, previous.getStart(), previous.getContent() + span.getContent());
        return merged.build();
    }
}
