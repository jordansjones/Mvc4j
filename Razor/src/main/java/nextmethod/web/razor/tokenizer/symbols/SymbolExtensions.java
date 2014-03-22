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

package nextmethod.web.razor.tokenizer.symbols;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import nextmethod.base.Delegates;
import nextmethod.base.Strings;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;

public final class SymbolExtensions {

    private SymbolExtensions() {}

    public static LocationTagged<String> getContent(@Nonnull final SpanBuilder spanBuilder) {
        return getContent(spanBuilder, input1 -> input1);
    }

    public static LocationTagged<String> getContent(@Nonnull final SpanBuilder spanBuilder, @Nonnull
    final Delegates.IFunc1<Iterable<ISymbol>, Iterable<ISymbol>> filter
                                                   ) {
        return getContent(filter.invoke(spanBuilder.getSymbols()), spanBuilder.getStart());
    }

    public static LocationTagged<String> getContent(@Nullable final Iterable<? extends ISymbol> symbols,
                                                    @Nonnull final SourceLocation spanStart
                                                   ) {
        if (symbols == null || Iterables.isEmpty(symbols)) {
            return new LocationTagged<>(Strings.Empty, spanStart);
        }
        else {
            final ISymbol first = Iterables.getFirst(symbols, null);
            final StringBuilder sb = new StringBuilder();
            for (ISymbol symbol : symbols) {
                sb.append(symbol.getContent());
            }
            return new LocationTagged<>(sb.toString(), SourceLocation.add(spanStart, first.getStart()));
        }
    }

    public static LocationTagged<String> getContent(@Nonnull final ISymbol symbol) {
        return new LocationTagged<>(symbol.getContent(), symbol.getStart());
    }
}
