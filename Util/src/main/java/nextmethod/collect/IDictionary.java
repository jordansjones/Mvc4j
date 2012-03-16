package nextmethod.collect;

import nextmethod.OutParam;

import java.util.Map;

public interface IDictionary<K,V> extends Map<K,V> {

	boolean tryGetValue(K key, OutParam<V> value);

}
