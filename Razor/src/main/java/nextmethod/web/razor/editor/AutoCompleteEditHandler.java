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

import nextmethod.base.Delegates;
import nextmethod.web.razor.PartialParseResult;
import nextmethod.web.razor.parser.ParserHelpers;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.TextChange;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import static nextmethod.base.Strings.nullToEmpty;

public class AutoCompleteEditHandler extends SpanEditHandler {

    private boolean autoCompleteAtEndOfSpan;
    private String autoCompleteString;

    public AutoCompleteEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer) {
        super(tokenizer);
    }

    public AutoCompleteEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer,
                                   @Nonnull final AcceptedCharacters accepted
                                  ) {
        super(tokenizer, accepted);
    }

    public AutoCompleteEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer,
                                   @Nonnull final EnumSet<AcceptedCharacters> accepted
                                  ) {
        super(tokenizer, accepted);
    }

    @Override
    protected EnumSet<PartialParseResult> canAcceptChange(@Nonnull final Span target,
                                                          @Nonnull final TextChange normalizedChange
                                                         ) {
        if (((autoCompleteAtEndOfSpan && isAtEndOfSpan(target, normalizedChange)) ||
             isAtEndOfFirstLine(target, normalizedChange))
            && normalizedChange.isInsert()
            && ParserHelpers.isNewLine(normalizedChange.getNewText())
            && autoCompleteString != null
            ) {
            return PartialParseResult.setOf(PartialParseResult.Rejected, PartialParseResult.AutoCompleteBlock);
        }
        return EnumSet.of(PartialParseResult.Rejected);
    }

    public boolean isAutoCompleteAtEndOfSpan() {
        return autoCompleteAtEndOfSpan;
    }

    public void setAutoCompleteAtEndOfSpan(boolean autoCompleteAtEndOfSpan) {
        this.autoCompleteAtEndOfSpan = autoCompleteAtEndOfSpan;
    }

    public String getAutoCompleteString() {
        return autoCompleteString;
    }

    public void setAutoCompleteString(String autoCompleteString) {
        this.autoCompleteString = autoCompleteString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AutoCompleteEditHandler)) return false;
        final AutoCompleteEditHandler that = (AutoCompleteEditHandler) o;

        return super.equals(that)
               && nullToEmpty(autoCompleteString).equals(nullToEmpty(that.autoCompleteString))
               && autoCompleteAtEndOfSpan == that.autoCompleteAtEndOfSpan;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (autoCompleteString != null
                                ? autoCompleteString.hashCode()
                                : 0);
        return result;
    }
}
