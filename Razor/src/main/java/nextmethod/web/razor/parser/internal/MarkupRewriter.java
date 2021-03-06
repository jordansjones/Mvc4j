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

import java.util.Deque;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Queues;
import nextmethod.annotations.Internal;
import nextmethod.base.Delegates;
import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.SourceLocation;

import static com.google.common.base.Preconditions.checkNotNull;

@Internal
abstract class MarkupRewriter extends ParserVisitor implements ISyntaxTreeRewriter {

    private final Deque<BlockBuilder> blocks = Queues.newArrayDeque();
    private Delegates.IAction3<SpanBuilder, SourceLocation, String> markupSpanFactory;

    protected MarkupRewriter(@Nonnull final Delegates.IAction3<SpanBuilder, SourceLocation, String> markupSpanFactory) {
        this.markupSpanFactory = checkNotNull(markupSpanFactory);
    }

    @Nullable
    protected BlockBuilder getParent() {
        return blocks.size() > 0
               ? blocks.peek()
               : null;
    }

    public Block rewrite(@Nonnull final Block input) {
        input.accept(this);
        assert blocks.size() == 1;
        return blocks.pop().build();
    }

    @Override
    public void visitBlock(@Nonnull final Block block) {
        if (canRewrite(block)) {
            final SyntaxTreeNode syntaxTreeNode = rewriteBlock(blocks.peek(), block);
            if (syntaxTreeNode != null) {
                blocks.peek().getChildren().add(syntaxTreeNode);
            }
        }
        else {
            final BlockBuilder builder = new BlockBuilder(block);
            builder.getChildren().clear();
            blocks.push(builder);
            super.visitBlock(block);
            assert com.google.common.base.Equivalence.identity().equivalent(builder, blocks.peek());

            if (blocks.size() > 1) {
                blocks.pop();
                blocks.peek().getChildren().add(builder.build());
            }
        }
    }

    @Override
    public void visitSpan(@Nonnull final Span span) {
        if (canRewrite(span)) {
            final SyntaxTreeNode newNode = rewriteSpan(blocks.peek(), span);
            if (newNode != null) {
                blocks.peek().getChildren().add(newNode);
            }
        }
        else {
            blocks.peek().getChildren().add(span);
        }
    }

    protected boolean canRewrite(@Nonnull final Block block) {
        return false;
    }

    protected boolean canRewrite(@Nonnull final Span span) {
        return false;
    }

    @Nullable
    protected SyntaxTreeNode rewriteBlock(@Nonnull final BlockBuilder parent, @Nonnull final Block block) {
        throw new NotImplementedException();
    }

    @Nullable
    protected SyntaxTreeNode rewriteSpan(@Nonnull final BlockBuilder parent, @Nonnull final Span span) {
        throw new NotImplementedException();
    }

    protected void fillSpan(@Nonnull final SpanBuilder builder, @Nonnull final SourceLocation start,
                            @Nonnull final String content
                           ) {
        markupSpanFactory.invoke(builder, start, content);
    }
}
