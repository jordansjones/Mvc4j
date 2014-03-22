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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;

public final class MapBuilder<K, V> {

    private final Map<K, V> map;

    private MapBuilder() {
        this.map = Maps.newHashMap();
    }

    public MapBuilder<K, V> clear() {
        map.clear();
        return this;
    }

    public MapBuilder<K, V> put(final K key, final V value) {
        map.put(key, value);
        return this;
    }

    public MapBuilder<K, V> putAll(final Map<? extends K, ? extends V> m) {
        map.putAll(m);
        return this;
    }

    public Map<K, V> build() {
        return new HashMap<>(this.map);
    }

    public static <K, V> MapBuilder<K, V> of() {
        return new MapBuilder<>();
    }

    public static <K, V> MapBuilder<K, V> of(@Nonnull final K k1, @Nullable final V v1) {
        return MapBuilder.<K, V>of()
                         .put(k1, v1);
    }

    public static <K, V> MapBuilder<K, V> of(@Nonnull final K k1, @Nullable final V v1, @Nonnull final K k2,
                                             @Nullable final V v2
                                            ) {
        return MapBuilder.<K, V>of()
                         .put(k1, v1)
                         .put(k2, v2);
    }

    public static <K, V> MapBuilder<K, V> of(@Nonnull final K k1, @Nullable final V v1, @Nonnull final K k2,
                                             @Nullable final V v2, @Nonnull final K k3, @Nullable final V v3
                                            ) {
        return MapBuilder.<K, V>of()
                         .put(k1, v1)
                         .put(k2, v2)
                         .put(k3, v3);
    }
}
