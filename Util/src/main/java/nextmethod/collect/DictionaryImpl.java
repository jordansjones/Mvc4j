package nextmethod.collect;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import nextmethod.NotImplementedException;
import nextmethod.OutParam;
import nextmethod.base.IEqualityComparer;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class DictionaryImpl<TKey, TValue> implements IDictionary<TKey, TValue> {

    private final TreeMap<TKey, TValue> treeMap;

    public DictionaryImpl(Comparator<TKey> comparator) {
        this.treeMap = new TreeMap<>(comparator);
    }

    // This requires keys to implement Comparable interface.
    public DictionaryImpl() {
        this.treeMap = new TreeMap<>();
    }

    @Override
    public IDictionary<TKey, TValue> add(KeyValuePair<TKey, TValue> item) {
        treeMap.put(item.getKey(), item.getValue());
        return this;
    }

    @Override
    public IDictionary<TKey, TValue> add(TKey tKey, TValue tValue) {
        treeMap.put(tKey, tValue);
        return this;
    }

    @Override
    public IDictionary<TKey, TValue> add(IDictionary<? extends TKey, ? extends TValue> items) {
        for (KeyValuePair<? extends TKey, ? extends TValue> item : items) {
            treeMap.put(item.getKey(), item.getValue());
        }
        return this;
    }

    @Override
    public IDictionary<TKey, TValue> add(Map<TKey, TValue> items) {
        treeMap.putAll(items);
        return this;
    }

    @Override
    public IDictionary<TKey, TValue> clear() {
        treeMap.clear();
        return this;
    }

    @Override
    public boolean contains(KeyValuePair<TKey, TValue> item) {
        return treeMap.containsKey(item.getKey())
                && treeMap.get(item.getKey()).equals(item.getValue());
    }

    @Override
    public boolean containsKey(TKey tKey) {
        return treeMap.containsKey(tKey);
    }

    @Override
    public boolean containsValue(TValue tValue) {
        return treeMap.containsValue(tValue);
    }

    @Override
    public IDictionary<TKey, TValue> copyTo(KeyValuePair<TKey, TValue>[] array, int arrayIndex) {
        // What's the arrayIndex for?
        throw new NotImplementedException();
    }

    @Override
    public IDictionary<TKey, TValue> filterEntries(Predicate<KeyValuePair<TKey, TValue>> filter) {
        throw new NotImplementedException();
    }

    @Override
    public TValue get(TKey tKey) {
        return treeMap.get(tKey);
    }

    @Override
    public IEqualityComparer<TKey> getComparer() {
        // Do we still want to use IEqualityComparer instead of Compartor?
        throw new NotImplementedException();
    }

    @Override
    public boolean isEmpty() {
        return treeMap.isEmpty();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public IDictionary<TKey, TValue> put(TKey tKey, TValue tValue) {
        treeMap.put(tKey, tValue);
        return this;
    }

    @Override
    public boolean remove(KeyValuePair<TKey, TValue> item) {
        if(this.contains(item)) {
            treeMap.remove(item.getKey());
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(TKey tKey) {
        return treeMap.remove(tKey) == null ? false : true;
    }

    @Override
    public IDictionary<TKey, TValue> set(TKey tKey, TValue tValue) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int size() {
        return treeMap.size();
    }

    @Override
    public Map<TKey, TValue> toMap() {
        return treeMap;
    }

    @Override
    public Optional<TValue> tryGetValue(TKey tKey) {
        return Optional.fromNullable(treeMap.get(tKey));
    }

    @Override
    public boolean tryGetValue(TKey tKey, OutParam<TValue> value) {
        value.set(treeMap.get(tKey));
        return treeMap.containsKey(tKey);
    }

    @Override
    public Iterator<KeyValuePair<TKey, TValue>> iterator() {
        // I want to do this by "yielding", so I'll implement it when I figure that out. -ea
        throw new NotImplementedException();
    }
}
