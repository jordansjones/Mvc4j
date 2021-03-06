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

package nextmethod.web.razor.parser.syntaxtree;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import nextmethod.web.razor.generator.IBlockCodeGenerator;
import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.TextChange;

import static com.google.common.base.Preconditions.checkArgument;
import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class Block extends SyntaxTreeNode {

    private final BlockType type;
    private final Collection<SyntaxTreeNode> children;
    private final IBlockCodeGenerator codeGenerator;
    private final String name;

    public Block(@Nonnull final BlockBuilder source) {
        checkArgument(source.getType().isPresent(), RazorResources().blockTypeNotSpecified());
        this.type = source.getType().get();
        this.children = source.getChildren();
        this.name = source.getName();
        this.codeGenerator = source.getCodeGenerator();

        source.reset();

        for (SyntaxTreeNode node : children) {
            node.setParent(this);
        }
    }

    Block(@Nonnull final BlockType type, @Nonnull final Collection<SyntaxTreeNode> contents,
          @Nullable final IBlockCodeGenerator generator
         ) {
        this.type = type;
        this.name = type.name();
        this.children = contents;
        this.codeGenerator = generator;
    }

    public BlockType getType() {
        return type;
    }

    public Collection<SyntaxTreeNode> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public IBlockCodeGenerator getCodeGenerator() {
        return codeGenerator;
    }

    @Override
    public boolean isBlock() {
        return true;
    }

    @Override
    public int getLength() {
        int size = 0;
        for (SyntaxTreeNode child : children) {
            size += child.getLength();
        }
        return size;
    }

    @Override
    public SourceLocation getStart() {
        final SyntaxTreeNode child = Iterables.getFirst(this.children, null);
        if (child == null) { return SourceLocation.Zero; }

        return child.getStart();
    }

    @Override
    public void accept(@Nonnull final ParserVisitor visitor) {
        visitor.visitBlock(this);
    }

    public Span findFirstDescendentSpan() {
        SyntaxTreeNode current = this;
        while (current != null && current.isBlock()) {
            current = Iterables.getFirst(((Block) current).children, null);
        }

        return typeAs(current, Span.class);
    }

    public Span findLastDescendentSpan() {
        SyntaxTreeNode current = this;
        while (current != null && current.isBlock()) {
            current = Iterables.getLast(((Block) current).children, null);
        }
        return typeAs(current, Span.class);
    }

    public Collection<Span> flatten() {
        final List<Span> values = Lists.newArrayList();

        // Create an enumerable that flattens the tree for use by syntax highlighters, etc.
        for (SyntaxTreeNode element : children) {
            final Span span = typeAs(element, Span.class);
            if (span != null) {
                values.add(span);
            }
            else {
                final Block block = typeAs(element, Block.class);
                if (block != null) {
                    values.addAll(block.flatten().stream().map(childSpan -> childSpan).collect(Collectors.toList()));
                }
            }
        }
        return values;
    }

    public Span locateOwner(final TextChange change) {
        // Ask each child recursively
        Span owner = null;
        for (SyntaxTreeNode element : children) {
            final Span span = typeAs(element, Span.class);
            if (span == null) {
                owner = ((Block) element).locateOwner(change);
            }
            else {
                if (change.getOldPosition() < span.getStart().getAbsoluteIndex()) {
                    // Early escape for cases when changes overlap multiple spans...
                    break;
                }
                owner = span.getEditHandler().ownsChange(span, change)
                        ? span
                        : owner;
            }

            if (owner != null) {
                break;
            }
        }
        return owner;
    }

    @Override
    public boolean equivalentTo(@Nonnull final SyntaxTreeNode node) {
        final Block other = typeAs(node, Block.class);
        if (other == null || other.getType() != getType()) {
            return false;
        }
        return childrenEqual(getChildren(), other.getChildren());
    }

    @Override
    public String toString() {
        return String.format("%s Block at %s::%d (Gen:%s)", this.type, getStart(), getLength(), this.codeGenerator);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
        final Block other = typeAs(obj, Block.class);
        return other != null
               && getType() == other.getType()
               && Objects.equal(getCodeGenerator(), other.getCodeGenerator())
               && childrenEqual(getChildren(), other.getChildren());
    }

    @Override
    public int hashCode() {
        return getType().hashCode();
    }

    private static boolean childrenEqual(final Iterable<SyntaxTreeNode> left, final Iterable<SyntaxTreeNode> right) {
        return Iterables.elementsEqual(left, right);
    }
}
