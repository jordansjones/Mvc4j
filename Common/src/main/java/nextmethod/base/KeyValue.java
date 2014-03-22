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

package nextmethod.base;

import javax.annotation.Nullable;

public class KeyValue<A, B> {

    private final A key;
    private final B value;

    public KeyValue(@Nullable final A key, @Nullable final B value) {
        this.key = key;
        this.value = value;
    }

    @Nullable
    public A getKey() {
        return key;
    }

    @Nullable
    public B getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final KeyValue keyValue = (KeyValue) o;

        if (key != null
            ? !key.equals(keyValue.key)
            : keyValue.key != null) { return false; }
        if (value != null
            ? !value.equals(keyValue.value)
            : keyValue.value != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null
                     ? key.hashCode()
                     : 0;
        result = 31 * result + (value != null
                                ? value.hashCode()
                                : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s{key=%s, value=%s}", this.getClass().getSimpleName(), key, value);
    }

    public static <A, B> KeyValue<A, B> of(@Nullable final A a, @Nullable final B b) {
        return new KeyValue<>(a, b);
    }

}
