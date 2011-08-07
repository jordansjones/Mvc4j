package com.nextmethod.routing;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:34 PM
 */
public class RouteValueDictionary implements Map<String, Object> {

	private final Map<String, Object> routeValues = Maps.newHashMap();

	public RouteValueDictionary() {
		this(null);
	}

	public RouteValueDictionary(@Nullable final Object values) {
	}

	public RouteValueDictionary(@Nullable final Map<String, Object> values) {
		if (values != null && !values.isEmpty())
			this.routeValues.putAll(values);
	}

	public Object tryGetValue(final String key) {
		if (!routeValues.containsKey(checkNotNull(key)))
			return null;
		return routeValues.get(key);
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
}
