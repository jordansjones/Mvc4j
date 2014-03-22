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

package nextmethod.web.razor;

import java.util.Arrays;
import java.util.EnumSet;
import javax.annotation.Nullable;

/**
 * The result of attempt an incremental parse.
 * <p>
 * Either the Accepted or Rejeted flag is ALWAYS set.
 * Additionally, Provisional my be set with Accepted and SpanContextChanged may be set with Rejected.
 * Provisional may NOT be set with Rejected and SpanContextChanged may NOT be set with Accepted.
 * </p>
 */
public enum PartialParseResult {

    /**
     * Indicates that the edit could not be accepted and that a reparse is underway.
     */
    Rejected,

    /**
     * Indicates that the edit was accepted and has been added to the parse tree
     */
    Accepted,

    /**
     * Indiicates that the edit was accepted, but that a reparse should be forced when idle time is available sice the edit may be misclassified.
     * <p>
     * This generally occurs when a "." is typed in an Implicit Expression, since editors require that this
     * be assigned to Code in order to properly support features like code completion. However, if no further edits
     * occur following the ".", it should be treated as Markup
     * </p>
     */
    Provisional,

    /**
     * Indicates that the edit caused a change in the span's context and that if any statement completions were active prior to starting this
     * initial parse, they should be reinitialized.
     */
    SpanContextChanged,

    /**
     * Indicates that the edit requires an auto completion to occur.
     */
    AutoCompleteBlock,;

    public static final EnumSet<PartialParseResult> Any = EnumSet.allOf(PartialParseResult.class);
    public static final EnumSet<PartialParseResult> NotAny = EnumSet.noneOf(PartialParseResult.class);

    public static EnumSet<PartialParseResult> setOfRejected() {
        return EnumSet.of(PartialParseResult.Rejected);
    }

    public static EnumSet<PartialParseResult> setOf(@Nullable final PartialParseResult... values) {
        if (values == null || values.length < 1) return NotAny;

        final PartialParseResult first = values[0];
        if (values.length == 1) return EnumSet.of(first);
        return EnumSet.of(first, Arrays.copyOfRange(values, 1, values.length));
    }

}
