package nextmethod.collect;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import nextmethod.OutParam;
import nextmethod.base.IEqualityComparer;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;

public final class ImmutableDictionary<K,V> implements IDictionary<K,V> {
	
	private final IDictionary<K, V> delegate;

	ImmutableDictionary(final Map<K, V> delegate) {
		this(delegate == null ? new Dictionary<K, V>() : new Dictionary<K, V>(delegate));
	}
	
	ImmutableDictionary(final IDictionary<K, V> delegate) {
		this.delegate = delegate == null ? new Dictionary<K, V>() : new Dictionary<K, V>(delegate);
	}
	
	public static <K, V> ImmutableDictionary<K, V> of(@Nullable final Map<K, V> map) {
		return new ImmutableDictionary<>(map);
	}

	@Override
	public ImmutableDictionary<K, V> add(KeyValuePair<K, V> item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ImmutableDictionary<K, V> add(IDictionary<? extends K, ? extends V> items) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ImmutableDictionary<K, V> add(Map<K, V> items) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ImmutableDictionary<K, V> add(K k, V v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ImmutableDictionary<K, V> clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(KeyValuePair<K, V> item) {
		return delegate.contains(item);
	}

	@Override
	public boolean containsKey(K k) {
		return delegate.containsKey(k);
	}

	@Override
	public boolean containsValue(V v) {
		return delegate.containsValue(v);
	}

	@Override
	public ImmutableDictionary<K, V> copyTo(KeyValuePair<K, V>[] array, int arrayIndex) {
		delegate.copyTo(array, arrayIndex);
		return this;
	}

	@Override
	public ImmutableDictionary<K, V> filterEntries(final Predicate<KeyValuePair<K, V>> filter) {
		return new ImmutableDictionary<K, V>(this.delegate.filterEntries(filter));
	}

	@Override
	public V get(K k) {
		return delegate.get(k);
	}

	@Override
	public IEqualityComparer<K> getComparer() {
		return delegate.getComparer();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public ImmutableDictionary<K, V> put(final K k, final V v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<KeyValuePair<K, V>> iterator() {
		return delegate.iterator();
	}

	@Override
	public boolean remove(KeyValuePair<K, V> item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(K k) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IDictionary<K, V> set(K k, V v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public Map<K, V> toMap() {
		return null;
	}

	@Override
	public Optional<V> tryGetValue(K k) {
		return delegate.tryGetValue(k);
	}

	@Override
	public boolean tryGetValue(K k, OutParam<V> value) {
		return delegate.tryGetValue(k, value);
	}
}
