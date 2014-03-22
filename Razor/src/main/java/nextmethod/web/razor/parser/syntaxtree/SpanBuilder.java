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

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import nextmethod.web.razor.editor.SpanEditHandler;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.SourceLocationTracker;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

/**
 *
 */
public class SpanBuilder {

    private final SourceLocationTracker tracker = new SourceLocationTracker();
    private List<ISymbol> symbolList = Lists.newArrayList();

    private SourceLocation start;
    private SpanKind kind;
    private SpanEditHandler editHandler;
    private ISpanCodeGenerator codeGenerator;

    public SpanBuilder(@Nonnull final Span original) {
        this.kind = original.getKind();
        this.symbolList = Lists.newArrayList(original.getSymbols());
        this.editHandler = original.getEditHandler();
        this.codeGenerator = original.getCodeGenerator();
        this.start = original.getStart();
    }

    public SpanBuilder() {
        this.reset();
    }

    public void reset() {
        this.symbolList = Lists.newArrayList();
        this.editHandler = SpanEditHandler.createDefault();
        this.codeGenerator = SpanCodeGenerator.Null;
        this.start = SourceLocation.Zero;
    }

    public Span build() {
        return new Span(this);
    }

    public SpanBuilder clearSymbols() {
        this.symbolList.clear();
        return this;
    }

    public void accept(@Nullable final ISymbol symbol) {
        if (symbol == null) return;

        if (symbolList.isEmpty()) {
            this.start = symbol.getStart();
            symbol.changeStart(SourceLocation.Zero);
            tracker.setCurrentLocation(SourceLocation.Zero);
        }
        else {
            symbol.changeStart(tracker.getCurrentLocation());
        }
        this.symbolList.add(symbol);
        this.tracker.updateLocation(symbol.getContent());
    }

    public ImmutableCollection<ISymbol> getSymbols() {
        return ImmutableList.copyOf(symbolList);
    }

    public SpanKind getKind() {
        return kind;
    }

    public SpanBuilder setKind(@Nonnull final SpanKind kind) {
        this.kind = kind;
        return this;
    }

    public SourceLocation getStart() {
        return start;
    }

    public SpanBuilder setStart(@Nonnull final SourceLocation start) {
        this.start = start;
        return this;
    }

    public SpanEditHandler getEditHandler() {
        return editHandler;
    }

    public SpanBuilder setEditHandler(@Nonnull final SpanEditHandler editHandler) {
        this.editHandler = editHandler;
        return this;
    }

    public ISpanCodeGenerator getCodeGenerator() {
        return codeGenerator;
    }

    public SpanBuilder setCodeGenerator(@Nonnull final ISpanCodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
        return this;
    }
}
