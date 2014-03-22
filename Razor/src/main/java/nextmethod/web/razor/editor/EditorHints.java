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

import java.util.Arrays;
import java.util.EnumSet;
import javax.annotation.Nullable;

/**
 * Used within {@link SpanEditHandler#getEditorHints()}
 *
 * @see SpanEditHandler#getEditorHints()
 */
public enum EditorHints {

    /**
     * The default (Markup or Code) editor behavior for Statement completion should be used.
     * Editors can always use the default behavior, even if the span is labeled with a different CompletionType.
     */
    None,

    /**
     * Indicates that Virtual Path completion should be used for this span if the editor supports it.
     * Editors need not support this mode of completion, and will use the {@linkplain EditorHints#None default behavior}.
     */
    VirtualPath,

    /**
     * Indicates that this span's content contains the path to the layout page for this document.
     */
    LayoutPage,;

    public static final EnumSet<EditorHints> Any = EnumSet.allOf(EditorHints.class);
    public static final EnumSet<EditorHints> NotAny = EnumSet.noneOf(EditorHints.class);

    public static EnumSet<EditorHints> setOf(@Nullable final EditorHints... values) {
        if (values == null || values.length < 1) return NotAny;

        final EditorHints first = values[0];
        if (values.length == 1) return EnumSet.of(first);
        return EnumSet.of(first, Arrays.copyOfRange(values, 1, values.length));
    }
}
