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

import java.util.Objects;
import javax.annotation.Nonnull;

import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.SourceLocationTracker;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import static nextmethod.base.TypeHelpers.typeAs;

class RawTextSymbol implements ISymbol {

    private SourceLocation start;
    private String content;

    RawTextSymbol(@Nonnull final SourceLocation start, @Nonnull final String content) {
        this.start = start;
        this.content = content;
    }

    @Override
    public SourceLocation getStart() {
        return start;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void offsetStart(final SourceLocation documentStart) {
        this.start = SourceLocation.add(documentStart, this.start);
    }

    @Override
    public void changeStart(final SourceLocation newStart) {
        this.start = newStart;
    }

    void calculateStart(final Span prev) {
        if (prev == null) {
            this.start = SourceLocation.Zero;
        }
        else {
            this.start = new SourceLocationTracker(prev.getStart()).updateLocation(prev.getContent())
                                                                   .getCurrentLocation();
        }
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
        final RawTextSymbol other = typeAs(obj, RawTextSymbol.class);
        return other != null && Objects.equals(start, other.start) && Objects.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                               start,
                               content
                           );
    }
}
