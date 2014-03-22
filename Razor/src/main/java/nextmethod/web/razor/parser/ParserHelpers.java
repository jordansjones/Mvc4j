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

package nextmethod.web.razor.parser;

import javax.annotation.Nonnull;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import nextmethod.base.Strings;

public final class ParserHelpers {

    public static final Predicate<Character> IsIdentifierPartPredicate = val -> val != null && (
                                                                                                   isLetter(val) ||
                                                                                                   isDecimalDigit(val) ||
                                                                                                   isConnecting(val) ||
                                                                                                   isCombining(val) ||
                                                                                                   isFormatting(val)
    );

    private ParserHelpers() {}

    public static boolean isNewLine(final char val) {
        return val == '\r' || // Carriage return
               val == '\n' || // Linefeed
               val == '\u0085' || // Next line
               val == '\u2028' || // Line separator
               val == '\u2029'; // Paragraph separator
    }

    public static boolean isNewLine(final String val) {
        return val != null && (val.length() == 1 && isNewLine(val.charAt(0)) || "\r\n".equalsIgnoreCase(val));
    }

    public static boolean isAnyOfString(final String val, final Predicate<Character> predicate) {
        return Iterables.any(Lists.charactersOf(val), predicate);
    }

    public static boolean isAllOfString(final String val, final Predicate<Character> predicate) {
        return Iterables.all(Lists.charactersOf(val), predicate);
    }

    public static Predicate<Character> IsWhitespacePredicate = val -> val != null && (
                                                                                         val == ' ' ||
                                                                                         val == '\f' ||
                                                                                         val == '\t' ||
                                                                                         val == '\u000B' ||
                                                                                         // Vertical Tab
                                                                                         Character.getType(val) ==
                                                                                         Character.SPACE_SEPARATOR
    );

    public static boolean isNullOrWhitespace(final String val) {
        if (Strings.isNullOrEmpty(val)) return true;
        final char[] chars = val.toCharArray();
        for (char aChar : chars) {
            if (!isWhitespace(aChar)) { return false; }
        }
        return true;
    }

    public static boolean isWhitespace(final char val) {
        return IsWhitespacePredicate.apply(val);
    }

    public static boolean isWhitespaceOrNewLine(final char val) {
        return isWhitespace(val) || isNewLine(val);
    }

    public static boolean isIdentifier(@Nonnull final String val) {
        return isIdentifier(val, true);
    }

    public static boolean isIdentifier(@Nonnull final String val, final boolean requireIdentifierStart) {
        Character[] identifierPart = nextmethod.collections.Arrays.asCharacterArray(val);
        if (requireIdentifierStart) {
            identifierPart = java.util.Arrays.copyOfRange(identifierPart, 1, identifierPart.length);
        }
        return (!requireIdentifierStart || isIdentifierStart(val.charAt(0)))
               && nextmethod.collections.Arrays.all(identifierPart, IsIdentifierPartPredicate);
    }

    public static boolean isHexDigit(final char val) {
        return (val >= '0' && val <= '9') || (val >= 'A' && val <= 'F') || (val >= 'a' && val <= 'f');
    }

    public static boolean isIdentifierStart(final char val) {
        return val == '_' || isLetter(val);
    }

    public static boolean isIdentifierPart(final char val) {
        return IsIdentifierPartPredicate.apply(val);
    }

    public static boolean isTerminatingCharToken(final char value) {
        return isNewLine(value) || value == '\'';
    }

    public static boolean isTerminatingQuotedStringToken(final char value) {
        return isNewLine(value) || value == '"';
    }

    public static boolean isDecimalDigit(final char value) {
        return Character.getType(value) == Character.DECIMAL_DIGIT_NUMBER;
    }

    public static boolean isLetterOrDecimalDigit(final char value) {
        return isLetter(value) || isDecimalDigit(value);
    }

    public static boolean isLetter(final char value) {
        final int type = Character.getType(value);
        return type == Character.UPPERCASE_LETTER ||
               type == Character.LOWERCASE_LETTER ||
               type == Character.TITLECASE_LETTER ||
               type == Character.MODIFIER_LETTER ||
               type == Character.OTHER_LETTER ||
               type == Character.LETTER_NUMBER;
    }

    public static boolean isFormatting(final char value) {
        return Character.getType(value) == Character.FORMAT;
    }

    public static boolean isCombining(final char value) {
        final int type = Character.getType(value);
        return type == Character.COMBINING_SPACING_MARK ||
               type == Character.NON_SPACING_MARK;
    }

    public static boolean isConnecting(final char value) {
        return Character.getType(value) == Character.CONNECTOR_PUNCTUATION;
    }

    public static String sanitizeClassName(@Nonnull String inputName) {
        final char c = inputName.charAt(0);
        if (!isIdentifierStart(c) && isIdentifierPart(c)) {
            inputName = "_" + inputName;
        }

        final char[] chars = inputName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            chars[i] = isIdentifierPart(ch)
                       ? ch
                       : '_';
        }
        return String.valueOf(chars);
    }

    public static boolean isEmailPart(final char character) {
        // Source: http://tools.ietf.org/html/rfc5322#section-3.4.1
        // We restrict the allowed characters to alpha-numerics and '_' in order to ensure we cover most of the cases where an
        // email address is intended without restricting the usage of code within JavaScript, CSS, and other contexts.
        return Character.isLetter(character) || Character.isDigit(character) || character == '_';
    }
}
