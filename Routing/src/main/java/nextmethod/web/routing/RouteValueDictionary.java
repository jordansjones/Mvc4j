package nextmethod.web.routing;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import nextmethod.OutParam;
import nextmethod.base.IEqualityComparer;
import nextmethod.collect.Dictionary;
import nextmethod.collect.IDictionary;
import nextmethod.collect.KeyValuePair;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 */
public class RouteValueDictionary implements IDictionary<String, Object> {

	private IDictionary<String, Object> routeValues = new Dictionary<>();

	public RouteValueDictionary() {
//		this(null);
	}

	public RouteValueDictionary(@Nullable final Builder values) {
		if (values != null) {
			this.routeValues.add(values.build().routeValues);
		}
	}

	public RouteValueDictionary(@Nullable final Map<String, Object> values) {
		if (values != null && !values.isEmpty())
			this.routeValues.add(values);
	}

	@Override
	public RouteValueDictionary add(final KeyValuePair<String, Object> item) {
		this.routeValues.add(item);
		return this;
	}

	@Override
	public RouteValueDictionary add(final String s, final Object o) {
		this.routeValues.add(s, o);
		return this;
	}

	@Override
	public RouteValueDictionary add(final IDictionary<? extends String, ? extends Object> items) {
		this.routeValues.add(items);
		return this;
	}

	@Override
	public RouteValueDictionary add(final Map<String, Object> items) {
		this.routeValues.add(items);
		return this;
	}

	@Override
	public RouteValueDictionary clear() {
		this.routeValues.clear();
		return this;
	}

	@Override
	public boolean contains(final KeyValuePair<String, Object> item) {
		return this.routeValues.contains(item);
	}

	@Override
	public boolean containsKey(final String s) {
		return this.routeValues.containsKey(s);
	}

	@Override
	public boolean containsValue(final Object o) {
		return this.routeValues.containsValue(o);
	}

	@Override
	public RouteValueDictionary copyTo(final KeyValuePair<String, Object>[] array, final int arrayIndex) {
		this.routeValues.copyTo(array, arrayIndex);
		return this;
	}

	@Override
	public RouteValueDictionary filterEntries(final Predicate<KeyValuePair<String, Object>> filter) {
		final RouteValueDictionary dictionary = new RouteValueDictionary();
		dictionary.routeValues = this.routeValues.filterEntries(filter);
		return dictionary;
	}

	@Override
	public Object get(final String s) {
		return this.routeValues.get(s);
	}

	@Override
	public IEqualityComparer<String> getComparer() {
		return this.routeValues.getComparer();
	}

	@Override
	public boolean isEmpty() {
		return this.routeValues.isEmpty();
	}

	@Override
	public boolean isReadOnly() {
		return this.routeValues.isReadOnly();
	}

	@Override
	public RouteValueDictionary put(final String s, final Object o) {
		this.routeValues.put(s, o);
		return this;
	}

	@Override
	public boolean remove(final KeyValuePair<String, Object> item) {
		return this.routeValues.remove(item);
	}

	@Override
	public boolean remove(final String s) {
		return this.routeValues.remove(s);
	}

	@Override
	public RouteValueDictionary set(final String s, final Object o) {
		this.routeValues.set(s, o);
		return this;
	}

	@Override
	public int size() {
		return this.routeValues.size();
	}

	@Override
	public Map<String, Object> toMap() {
		return this.routeValues.toMap();
	}

	@Override
	public Optional<Object> tryGetValue(final String s) {
		return this.routeValues.tryGetValue(s);
	}

	@Override
	public boolean tryGetValue(final String s, final OutParam<Object> value) {
		return this.routeValues.tryGetValue(s, value);
	}

	@Override
	public Iterator<KeyValuePair<String, Object>> iterator() {
		return this.routeValues.iterator();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private final RouteValueDictionary dictionary;

		public Builder() {
			this.dictionary = new RouteValueDictionary();
		}

		public Builder put(final String key, final Object value) {
			this.dictionary.add(checkNotNull(key), value);
			return this;
		}

		public RouteValueDictionary build() {
			return this.dictionary;
		}
	}
}
