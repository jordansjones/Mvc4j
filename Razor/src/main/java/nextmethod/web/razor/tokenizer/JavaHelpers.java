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

package nextmethod.web.razor.tokenizer;

import java.util.function.Predicate;

public final class JavaHelpers {

    private JavaHelpers() {}

    public static boolean isIdentifierStart(final char c) {
        return Character.isJavaIdentifierStart(c);
    }

    public static final Predicate<Character> IsIdentifierPartPredicate = input -> input != null && Character.isJavaIdentifierPart(input);

    public static boolean isIdentifierPart(final char c) {
        return IsIdentifierPartPredicate.test(c);
    }

    public static boolean isRealLiteralSuffix(final char c) {
        return c == 'F' ||
               c == 'f' ||
               c == 'D' ||
               c == 'd';
    }

    public static boolean isIdentifierPartByCharMatcher(final char c) {
        final int type = Character.getType(c);
        return type == Character.NON_SPACING_MARK ||
               type == Character.COMBINING_SPACING_MARK ||
               type == Character.CONNECTOR_PUNCTUATION ||
               type == Character.FORMAT;
    }
}
