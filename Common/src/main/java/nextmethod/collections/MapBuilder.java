package nextmethod.collections;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class MapBuilder<K,V> {

	private final Map<K,V> map;

	private MapBuilder () {
		this.map = Maps.newHashMap();
	}

	public MapBuilder<K,V> clear() {
		map.clear();
		return this;
	}

	public MapBuilder<K,V> put(final K key, final V value) {
		map.put(key, value);
		return this;
	}

	public MapBuilder<K,V> putAll(final Map<? extends K, ? extends V> m) {
		map.putAll(m);
		return this;
	}

	public Map<K, V> build() {
		return new HashMap<>(this.map);
	}

	public static <K,V> MapBuilder<K,V> of() {
		return new MapBuilder<>();
	}

	public static <K,V> MapBuilder<K,V> of(@Nonnull final K k1, @Nullable final V v1) {
		return MapBuilder.<K,V>of()
			.put(k1, v1);
	}

	public static <K,V> MapBuilder<K,V> of(@Nonnull final K k1, @Nullable final V v1, @Nonnull final K k2, @Nullable final V v2) {
		return MapBuilder.<K,V>of()
			.put(k1, v1)
			.put(k2, v2);
	}

	public static <K,V> MapBuilder<K,V> of(@Nonnull final K k1, @Nullable final V v1, @Nonnull final K k2, @Nullable final V v2, @Nonnull final K k3, @Nullable final V v3) {
		return MapBuilder.<K,V>of()
			.put(k1, v1)
			.put(k2, v2)
			.put(k3, v3);
	}
}
