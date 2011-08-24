package nextmethod.web.routing;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import nextmethod.OutParam;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 */
public class RouteValueDictionary implements Map<String, Object> {

	private final Map<String, Object> routeValues = Maps.newHashMap();

	public RouteValueDictionary() {
//		this(null);
	}

	public RouteValueDictionary(@Nullable final Builder values) {
		if (values != null) {
			this.routeValues.putAll(values.build().routeValues);
		}
	}

	public RouteValueDictionary(@Nullable final Map<String, Object> values) {
		if (values != null && !values.isEmpty())
			this.routeValues.putAll(values);
	}

	public boolean tryGetValue(final String key, final OutParam<Object> outParam) {
		if (!routeValues.containsKey(checkNotNull(key)))
			return false;

		checkNotNull(outParam).set(routeValues.get(key));
		return true;
	}

	public RouteValueDictionary filterEntries(final Predicate<Entry<String, Object>> predicate) {
		return new RouteValueDictionary(Maps.filterEntries(routeValues, predicate));
	}

	@Override
	public int size() {
		return routeValues.size();
	}

	@Override
	public boolean isEmpty() {
		return routeValues.isEmpty();
	}

	@Override
	public boolean containsKey(final Object key) {
		return routeValues.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return routeValues.containsValue(value);
	}

	@Override
	public Object get(final Object key) {
		return routeValues.get(key);
	}

	@Override
	public Object put(final String key, final Object value) {
		return routeValues.put(key, value);
	}

	@Override
	public Object remove(final Object key) {
		return routeValues.remove(key);
	}

	@Override
	public void putAll(final Map<? extends String, ?> m) {
		routeValues.putAll(m);
	}

	@Override
	public void clear() {
		routeValues.clear();
	}

	@Override
	public Set<String> keySet() {
		return routeValues.keySet();
	}

	@Override
	public Collection<Object> values() {
		return routeValues.values();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return routeValues.entrySet();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof RouteValueDictionary)) return false;

		final RouteValueDictionary that = (RouteValueDictionary) o;

		return routeValues.equals(that.routeValues);

	}

	@Override
	public int hashCode() {
		return routeValues.hashCode();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private final RouteValueDictionary dictionary;

		public Builder() {
			this.dictionary = new RouteValueDictionary();
		}

		public Builder put(final String key, final String value) {
			this.dictionary.put(checkNotNull(key), value);
			return this;
		}

		public RouteValueDictionary build() {
			return this.dictionary;
		}
	}
}
