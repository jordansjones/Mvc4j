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

package nextmethod.web.razor.generator;

import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;

public class TestSpan {

    private final SpanKind kind;
    private final int start;
    private final int end;

    public TestSpan(final SpanKind kind, final int start, final int end) {
        this.kind = kind;
        this.start = start;
        this.end = end;
    }

    public TestSpan(final Span span) {
        this(span.getKind(), span.getStart().getAbsoluteIndex(), span.getStart().getAbsoluteIndex() + span.getLength());
    }

    public SpanKind getKind() {
        return kind;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return String.format("%s: %d-%d", kind, start, end);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TestSpan testSpan = (TestSpan) o;

        if (end != testSpan.end) return false;
        if (start != testSpan.start) return false;
        if (kind != testSpan.kind) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = kind.hashCode();
        result = 31 * result + start;
        result = 31 * result + end;
        return result;
    }
}
