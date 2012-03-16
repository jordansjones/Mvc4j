package nextmethod.collect;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import nextmethod.OutParam;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ImmutableDictionary<K,V> implements IDictionary<K,V> {
	
	private final ImmutableMap<K, V> delegate;

	ImmutableDictionary(final Map<K, V> delegate) {
		this.delegate = delegate == null ? ImmutableMap.<K, V>of() : ImmutableMap.copyOf(delegate);
	}
	
	public static <K, V> ImmutableDictionary<K, V> of(@Nullable final Map<K, V> map) {
		return new ImmutableDictionary<>(map);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public boolean containsKey(@Nullable Object key) {
		return delegate.containsKey(key);
	}

	@Override
	public boolean containsValue(@Nullable Object value) {
		return delegate.containsValue(value);
	}

	@Override
	public ImmutableSet<Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	@Override
	public boolean equals(@Nullable Object object) {
		return delegate.equals(object);
	}

	@Override
	public V get(@Nullable Object key) {
		return delegate.get(key);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public ImmutableSet<K> keySet() {
		return delegate.keySet();
	}

	public V put(K k, V v) {
		return delegate.put(k, v);
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		delegate.putAll(map);
	}

	@Override
	public V remove(Object o) {
		return delegate.remove(o);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	@Override
	public ImmutableCollection<V> values() {
		return delegate.values();
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public boolean tryGetValue(@Nonnull final K key, @Nonnull final OutParam<V> value) {
		final Optional<K> result = Iterables.tryFind(this.keySet(), new Predicate<K>() {
			@Override
			public boolean apply(@Nullable K input) {
				if (input == null) return false;
				if (key.getClass().equals(String.class)) {
					final String keyString = String.class.cast(key);
					final String inputString = String.class.cast(input);
					return inputString.equalsIgnoreCase(keyString);
				}
				return input.equals(key);
			}
		});

		if (!result.isPresent())
			return false;

		checkNotNull(value).set(this.get(result.get()));
		return true;
	}
}
