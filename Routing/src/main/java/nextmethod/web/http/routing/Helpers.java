package nextmethod.web.http.routing;

import com.google.common.base.Equivalences;

/**
 * 
 */
final class Helpers {

	private Helpers() {
	}

	static boolean rvdHas(final HttpRouteValueDictionary dict, final String key) {
		return dict != null && dict.containsKey(key);
	}

	static boolean rvdHas(final HttpRouteValueDictionary dict, final String key, final Object value) {
		if (!rvdHas(dict, key))
			return false;

		final Object entryValue = dict.get(key);

		return Equivalences.equals().equivalent(value, entryValue);
	}
}
