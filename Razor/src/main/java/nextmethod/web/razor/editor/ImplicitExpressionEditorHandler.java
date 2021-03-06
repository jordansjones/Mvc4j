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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import nextmethod.base.Delegates;
import nextmethod.base.Strings;
import nextmethod.web.razor.PartialParseResult;
import nextmethod.web.razor.parser.ParserHelpers;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.StringReaderDelegate;
import nextmethod.web.razor.text.TextChange;
import nextmethod.web.razor.text.TextExtensions;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import static nextmethod.base.TypeHelpers.typeAs;

public class ImplicitExpressionEditorHandler extends SpanEditHandler {

    public Set<String> keywords;
    public boolean acceptTrailingDot;

    public ImplicitExpressionEditorHandler(final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer,
                                           final Set<String> keywords, final boolean acceptTrailingDot
                                          ) {
        super(tokenizer);
        initialize(keywords, acceptTrailingDot);
    }

    private void initialize(final Set<String> keywords, final boolean acceptTrailingDot) {
        this.keywords = Sets.newHashSet(keywords);
        this.acceptTrailingDot = acceptTrailingDot;
    }

    @Override
    protected EnumSet<PartialParseResult> canAcceptChange(@Nonnull final Span target,
                                                          @Nonnull final TextChange normalizedChange
                                                         ) {
        if (getAcceptedCharacters() == AcceptedCharacters.Any) {
            return PartialParseResult.setOf(PartialParseResult.Rejected);
        }
        if (isAcceptableReplace(target, normalizedChange)) {
            return handleReplacement(target, normalizedChange);
        }
        final int changeRelativePosition = normalizedChange.getOldPosition() - target.getStart().getAbsoluteIndex();

        // Get the edit context
        Optional<Character> lastChar = Optional.empty();
        if (changeRelativePosition > 0 && target.getContent().length() > 0) {
            lastChar = Optional.of(target.getContent().charAt(changeRelativePosition - 1));
        }

        // Don't support 0->1 length edits
        if (!lastChar.isPresent()) {
            return PartialParseResult.setOf(PartialParseResult.Rejected);
        }

        // Only support insertions at the end of the span
        if (isAcceptableInsertion(target, normalizedChange)) {
            return handleInsertion(target, lastChar.get(), normalizedChange);
        }
        if (isAcceptableDeletion(target, normalizedChange)) {
            return handleDeletion(target, lastChar.get(), normalizedChange);
        }
        return PartialParseResult.setOf(PartialParseResult.Rejected);
    }

    private static boolean isAcceptableReplace(final Span target, final TextChange change) {
        return isEndReplace(target, change) || (change.isReplace() && remainingIsWhitespace(target, change));
    }

    private static boolean isAcceptableDeletion(final Span target, final TextChange change) {
        return isEndDeletion(target, change) || (change.isDelete() && remainingIsWhitespace(target, change));
    }

    private static boolean isAcceptableInsertion(final Span target, final TextChange change) {
        return isEndInsertion(target, change) || (change.isInsert() && remainingIsWhitespace(target, change));
    }

    private static boolean remainingIsWhitespace(final Span target, final TextChange change) {
        int offset = (change.getOldPosition() - target.getStart().getAbsoluteIndex()) + change.getOldLength();
        return ParserHelpers.isNullOrWhitespace(target.getContent().substring(offset));
    }

    private EnumSet<PartialParseResult> handleReplacement(final Span target, final TextChange change) {
        final String oldText = getOldText(target, change);

        EnumSet<PartialParseResult> result = EnumSet.of(PartialParseResult.Rejected);
        if (endsWithDot(oldText) && endsWithDot(change.getNewText())) {
            result = EnumSet.of(PartialParseResult.Accepted);
            if (!acceptTrailingDot) {
                result.add(PartialParseResult.Provisional);
            }
        }

        return result;
    }

    private EnumSet<PartialParseResult> handleDeletion(final Span target, final char previousChar,
                                                       final TextChange change
                                                      ) {
        // What's left after deleting
        if (previousChar == '.') {
            return tryAcceptChange(
                                      target, change,
                                      EnumSet.of(PartialParseResult.Accepted, PartialParseResult.Provisional)
                                  );
        }
        else if (ParserHelpers.isIdentifierPart(previousChar)) {
            return tryAcceptChange(target, change);
        }
        else {
            return EnumSet.of(PartialParseResult.Rejected);
        }
    }

    private EnumSet<PartialParseResult> handleInsertion(final Span target, final char previousChar,
                                                        final TextChange change
                                                       ) {
        // What are we inserting after
        if (previousChar == '.') {
            return handleInsertionAfterDot(target, change);
        }
        else if (ParserHelpers.isIdentifierPart(previousChar) || previousChar == ')' || previousChar == ']') {
            return handleInsertionAfterIdPart(target, change);
        }
        else {
            return EnumSet.of(PartialParseResult.Rejected);
        }
    }

    private EnumSet<PartialParseResult> handleInsertionAfterIdPart(final Span target, final TextChange change) {
        // If the insertion is full identifier part, accept it
        if (ParserHelpers.isIdentifier(change.getNewText(), false)) {
            return tryAcceptChange(target, change);
        }
        else if (endsWithDot(change.getNewText())) {
            // Accept it, possibly provisional
            EnumSet<PartialParseResult> result = EnumSet.of(PartialParseResult.Accepted);
            if (!acceptTrailingDot) {
                result.add(PartialParseResult.Provisional);
            }
            return tryAcceptChange(target, change, result);
        }
        else {
            return EnumSet.of(PartialParseResult.Rejected);
        }
    }

    private EnumSet<PartialParseResult> handleInsertionAfterDot(final Span target, final TextChange change) {
        // If the insertion is a full identifier, accept it
        if (ParserHelpers.isIdentifier(change.getNewText())) {
            return tryAcceptChange(target, change);
        }
        return EnumSet.of(PartialParseResult.Rejected);
    }

    private static boolean endsWithDot(final String content) {
        return (content.length() == 1 && content.charAt(0) == '.')
               || (content.charAt(content.length() - 1) == '.' && isAllIdentifierPart(content));
    }

    private static boolean isAllIdentifierPart(final String content) {
        return Strings.toCharStream(content).limit(content.length() - 1).allMatch(ParserHelpers::isIdentifierPart);
    }

    private EnumSet<PartialParseResult> tryAcceptChange(final Span target, final TextChange change) {
        return tryAcceptChange(target, change, null);
    }

    private EnumSet<PartialParseResult> tryAcceptChange(final Span target, final TextChange change,
                                                        EnumSet<PartialParseResult> acceptResult
                                                       ) {
        if (acceptResult == null) {
            acceptResult = EnumSet.of(PartialParseResult.Accepted);
        }
        final String content = change.applyChange(target);
        if (startsWithKeyword(content)) {
            return EnumSet.of(PartialParseResult.Rejected, PartialParseResult.SpanContextChanged);
        }
        return acceptResult;
    }

    private boolean startsWithKeyword(final String newContent) {
        try (final StringReaderDelegate reader = new StringReaderDelegate(newContent)) {
            return keywords.contains(TextExtensions.readWhile(reader, ParserHelpers::isIdentifierPart));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean isAcceptTrailingDot() {
        return acceptTrailingDot;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Set<String> getKeywords() {
        return keywords;
    }

    @Override
    public String toString() {
        return String.format(
                                "%s;ImplicitExpression[%s];K%d",
                                super.toString(),
                                acceptTrailingDot
                                ? "ATD"
                                : "RTD",
                                keywords.size()
                            );
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object o) {
        final ImplicitExpressionEditorHandler other = typeAs(o, ImplicitExpressionEditorHandler.class);
        return other != null
               && super.equals(other)
               && keywords.equals(other.keywords)
               && acceptTrailingDot == other.acceptTrailingDot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                               super.hashCode(),
                               acceptTrailingDot,
                               keywords
                           );
    }
}
