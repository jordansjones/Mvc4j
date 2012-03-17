package nextmethod.collect;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import nextmethod.OutParam;
import nextmethod.base.IEqualityComparer;

import java.util.Map;

public interface IDictionary<TKey,TValue> extends Iterable<KeyValuePair<TKey,TValue>> {

	IDictionary<TKey, TValue> add(final KeyValuePair<TKey, TValue> item);
	IDictionary<TKey, TValue> add(final TKey key, final TValue value);
	IDictionary<TKey, TValue> add(final IDictionary<? extends TKey, ? extends TValue> items);
	IDictionary<TKey, TValue> add(final Map<TKey, TValue> items);
	
	IDictionary<TKey, TValue> clear();
	
	boolean contains(final KeyValuePair<TKey, TValue> item);
	boolean containsKey(final TKey key);
	boolean containsValue(final TValue value);
	
	IDictionary<TKey, TValue> copyTo(final KeyValuePair<TKey, TValue>[] array, int arrayIndex);
	
	IDictionary<TKey, TValue> filterEntries(final Predicate<KeyValuePair<TKey, TValue>> filter);
	
	TValue get(final TKey key);
	IEqualityComparer<TKey> getComparer();

	boolean isEmpty();
	boolean isReadOnly();
	
	IDictionary<TKey, TValue> put(final TKey key, final TValue value);
	
	boolean remove(final KeyValuePair<TKey, TValue> item);
	boolean remove(final TKey key);

	IDictionary<TKey, TValue> set(final TKey key, final TValue value);

	int size();
	
	Map<TKey, TValue> toMap();

	Optional<TValue> tryGetValue(final TKey key);
	boolean tryGetValue(final TKey key, final OutParam<TValue> value);

}
