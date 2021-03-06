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
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import nextmethod.annotations.Internal;
import nextmethod.web.razor.editor.SpanEditHandler;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.SourceLocationTracker;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import static nextmethod.base.TypeHelpers.typeAs;

/**
 *
 */
public class Span extends SyntaxTreeNode {

    private SourceLocation start;
    private String content;
    private SpanKind kind;
    private Collection<ISymbol> symbols;
    private Span previous;
    private Span next;
    private SpanEditHandler editHandler;
    private ISpanCodeGenerator codeGenerator;

    public Span(@Nonnull final SpanBuilder builder) {
        replaceWith(builder);
    }

    public void change(@Nonnull final Function<SpanBuilder, Void> changes) {
        final SpanBuilder builder = new SpanBuilder(this);
        changes.apply(builder);
        replaceWith(builder);
    }

    public void replaceWith(@Nonnull final SpanBuilder builder) {
        assert builder.getSymbols().stream().allMatch(x -> x != null);

        this.kind = builder.getKind();
        this.symbols = builder.getSymbols();
        this.editHandler = builder.getEditHandler();
        this.codeGenerator = builder.getCodeGenerator() != null
                             ? builder.getCodeGenerator()
                             : SpanCodeGenerator.Null;
        this.start = builder.getStart();

        // Since we took references to the values in SpanBuilder, clear it's references out
        builder.reset();

        // Calculate other properties
        final StringBuilder sb = new StringBuilder();
        for (ISymbol symbol : symbols) {
            sb.append(symbol.getContent());
        }
        this.content = sb.toString();
    }

    @Override
    public void accept(@Nonnull final ParserVisitor visitor) {
        visitor.visitSpan(this);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        return new StringBuilder()
                   .append(kind)
                   .append(String.format(" Span at %s::%d - [%s]", start, getLength(), content))
                   .append(" Edit: <")
                   .append(
                              editHandler == null
                              ? "null"
                              : editHandler.toString()
                          )
                   .append(">")
                   .append(" Gen: <")
                   .append(codeGenerator.toString())
                   .append("> ")
                   .append("{")
                   .append(SymbolGroupJoiner.join(getSymbolGroupCounts()))
                   .append("}")
                   .toString();
    }

    public void changeStart(@Nonnull final SourceLocation newStart) {
        this.start = newStart;
        Span current = this;
        final SourceLocationTracker tracker = new SourceLocationTracker(newStart);
        tracker.updateLocation(content);
        while ((current = current.getNext()) != null) {
            current.start = tracker.getCurrentLocation();
            tracker.updateLocation(current.getContent());
        }
    }

    protected void setStart(@Nonnull final SourceLocation newStart) {
        this.start = newStart;
    }

    private static final Joiner SymbolGroupJoiner = Joiner.on(";").skipNulls();

    private String[] getSymbolGroupCounts() {
        final Multiset<Class<?>> counts = HashMultiset.create();
        counts.addAll(symbols.stream().map(ISymbol::getClass).collect(Collectors.toList()));
        final String[] ret = new String[counts.size()];
        int i = 0;
        for (Multiset.Entry<Class<?>> entry : counts.entrySet()) {
            ret[i++] = String.format("%s:%d", entry.getElement().getSimpleName(), entry.getCount());
        }

        return ret;
    }

    /**
     * Checks that the specified span is equivalent to the other in that it has the same start point and content.
     */
    @Override
    public boolean equivalentTo(@Nonnull final SyntaxTreeNode node) {
        if (!(node instanceof Span)) return false;
        final Span other = Span.class.cast(node);

        return kind.equals(other.kind)
               && start.equals(other.start)
               && editHandler.equals(other.editHandler)
               && content.equals(other.content);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || !(o instanceof Span)) return false;

        final Span other = typeAs(o, Span.class);
        return kind.equals(other.kind)
               && editHandler.equals(other.editHandler)
               && codeGenerator.equals(other.codeGenerator)
               && Iterables.elementsEqual(symbols, other.symbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                               kind,
                               start,
                               content
                           );
    }

    public SpanKind getKind() {
        return kind;
    }

    public Iterable<ISymbol> getSymbols() {
        return symbols;
    }

    @Nullable
    public Span getPrevious() {
        return previous;
    }

    @Internal
    public void setPrevious(@Nullable final Span s) {
        previous = s;
    }

    @Nullable
    public Span getNext() {
        return next;
    }

    @Internal
    public void setNext(@Nullable final Span s) {
        next = s;
    }

    public SpanEditHandler getEditHandler() {
        return editHandler;
    }

    public ISpanCodeGenerator getCodeGenerator() {
        return codeGenerator;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean isBlock() {
        return false;
    }

    @Override
    public int getLength() {
        return content == null
               ? 0
               : content.length();
    }

    @Override
    public SourceLocation getStart() {
        return this.start;
    }
}
