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

package nextmethod.web.razor.editor;

import java.util.EnumSet;
import javax.annotation.Nonnull;

import nextmethod.web.razor.PartialParseResult;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;

public class EditResult {

    private SpanBuilder editedSpan;
    private EnumSet<PartialParseResult> results;

    public EditResult(@Nonnull final SpanBuilder editedSpan, @Nonnull final PartialParseResult results) {
        this(editedSpan, EnumSet.of(results));
    }

    public EditResult(@Nonnull final SpanBuilder editedSpan, @Nonnull final PartialParseResult... results) {
        this(editedSpan, PartialParseResult.setOf(results));
    }

    public EditResult(@Nonnull final SpanBuilder editedSpan, @Nonnull final EnumSet<PartialParseResult> results) {
        this.results = results;
        this.editedSpan = editedSpan;
    }

    public SpanBuilder getEditedSpan() {
        return editedSpan;
    }

    public EnumSet<PartialParseResult> getResults() {
        return results;
    }
}
