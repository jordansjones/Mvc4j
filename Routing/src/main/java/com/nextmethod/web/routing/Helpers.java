package com.nextmethod.web.routing;

import com.google.common.base.Equivalences;

/**
 * User: Jordan
 * Date: 8/7/11
 * Time: 9:35 PM
 */
final class Helpers {

	private Helpers() {
	}

	static boolean rvdHas(final RouteValueDictionary dict, final String key) {
		return dict != null && dict.containsKey(key);
	}

	static boolean rvdHas(final RouteValueDictionary dict, final String key, final Object value) {
		if (!rvdHas(dict, key))
			return false;

		final Object entryValue = dict.get(key);

		return Equivalences.equals().equivalent(value, entryValue);
	}
}
