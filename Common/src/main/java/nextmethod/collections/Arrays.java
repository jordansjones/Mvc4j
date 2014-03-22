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

package nextmethod.collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import nextmethod.base.Strings;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Arrays {

    private Arrays() {}

    public static <T> boolean all(@Nullable final T[] arr, @Nonnull final Predicate<T> predicate) {
        if (arr == null || arr.length == 0) { return true; }
        checkNotNull(predicate);
        for (T t : arr) {
            if (!predicate.apply(t)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean any(@Nullable final T[] arr, @Nonnull final Predicate<T> predicate) {
        if (arr == null || arr.length == 0) { return false; }
        checkNotNull(predicate);
        for (T t : arr) {
            if (predicate.apply(t)) {
                return true;
            }
        }
        return false;
    }

    public static Character[] asCharacterArray(@Nullable final String value) {
        if (Strings.isNullOrEmpty(value)) {
            return new Character[0];
        }

        final int numChars = value.length();
        final Character[] characters = new Character[numChars];
        for (int i = 0; i < numChars; i++) {
            characters[i] = value.charAt(i);
        }
        return characters;
    }
}
