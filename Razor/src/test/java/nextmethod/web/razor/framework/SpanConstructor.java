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

import java.util.Collections;
import java.util.EnumSet;
import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import nextmethod.base.Delegates;
import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.editor.AutoCompleteEditHandler;
import nextmethod.web.razor.editor.EditorHints;
import nextmethod.web.razor.editor.SpanEditHandler;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

public class SpanConstructor extends SyntaxTreeNode implements ISpanConstructor {

    static Delegates.IFunc1<String, Iterable<ISymbol>> testTokenizer = input -> {
        assert input != null;
        return Lists.<ISymbol>newArrayList(new RawTextSymbol(SourceLocation.Zero, input));
    };

    public SpanBuilder builder;

    public SpanConstructor(final SpanKind kind, final Iterable<ISymbol> symbols) {
        builder = new SpanBuilder().setKind(kind).setEditHandler(SpanEditHandler.createDefault(testTokenizer));
        symbols.forEach(builder::accept);
    }

    @Override
    public Span build() {
        return builder.build();
    }

    public SpanConstructor with(final ISpanCodeGenerator generator) {
        builder.setCodeGenerator(generator);
        return this;
    }

    public SpanConstructor with(final SpanEditHandler handler) {
        builder.setEditHandler(handler);
        return this;
    }

    public SpanConstructor withGenerator(final Delegates.IAction1<ISpanCodeGenerator> generatorConfigurer) {
        generatorConfigurer.invoke(builder.getCodeGenerator());
        return this;
    }

    public SpanConstructor withHandler(final Delegates.IAction1<SpanEditHandler> handlerConfigurer) {
        handlerConfigurer.invoke(builder.getEditHandler());
        return this;
    }

    public SpanConstructor hidden() {
        builder.setCodeGenerator(SpanCodeGenerator.Null);
        return this;
    }

    public SpanBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(SpanBuilder builder) {
        this.builder = builder;
    }

    public SpanConstructor accepts(final AcceptedCharacters... accepted) {
        final EnumSet<AcceptedCharacters> s = EnumSet.noneOf(AcceptedCharacters.class);
        if (accepted != null && accepted.length > 0) {
            Collections.addAll(s, accepted);
        }
        return accepts(s);
    }

    public SpanConstructor accepts(final EnumSet<AcceptedCharacters> acceptedCharacters) {
        return this.withHandler(input -> input.setAcceptedCharacters(acceptedCharacters));
    }

    public SpanConstructor autoCompleteWith(final String autoCompleteString) {
        return this.autoCompleteWith(autoCompleteString, false);
    }

    public SpanConstructor autoCompleteWith(final String autoCompleteString, final boolean atEndOfSPan) {
        final AutoCompleteEditHandler autoCompleteEditHandler = new AutoCompleteEditHandler(SpanConstructor.testTokenizer);
        autoCompleteEditHandler.setAutoCompleteString(autoCompleteString);
        autoCompleteEditHandler.setAutoCompleteAtEndOfSpan(atEndOfSPan);
        return this.with(autoCompleteEditHandler);
    }

    public SpanConstructor withEditorHints(final EditorHints hint, final EditorHints... hints) {
        final EnumSet<EditorHints> editorHints;
        if (hints == null || hints.length == 0) {
            editorHints = EnumSet.of(hint);
        }
        else {
            editorHints = EnumSet.of(hint, hints);
        }
        return this.withHandler(input -> input.setEditorHints(editorHints));
    }

    @Override
    public boolean isBlock() {
        throw new NotImplementedException();
    }

    @Override
    public int getLength() {
        throw new NotImplementedException();
    }

    @Override
    public SourceLocation getStart() {
        throw new NotImplementedException();
    }

    @Override
    public void accept(@Nonnull final ParserVisitor visitor) {
        throw new NotImplementedException();
    }

    @Override
    public boolean equivalentTo(@Nonnull final SyntaxTreeNode node) {
        throw new NotImplementedException();
    }
}
