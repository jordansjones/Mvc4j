package nextmethod.web.mvc;

import com.google.common.base.Ascii;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.inject.TypeLiteral;
import nextmethod.OutParam;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
final class ControllerTypeCache {

	private Table<String, String, Set<AssemblyType<?>>> cache;
	private final Lock locker = new ReentrantLock();

	public int size() {
		return 0;
	}

	private static final String typeCacheName = "MVC-ControllerTypeCache.xml";

	public void ensureInitialized(final IBuildManager buildManager) {
		if (cache == null) {
			locker.lock();
			try {
				if (cache == null) {
					cache = HashBasedTable.create();

					final ImmutableList<AssemblyType<?>> controllerTypes = TypeCacheUtil.getFilteredTypesFromAssemblies(typeCacheName, isControllerType(), buildManager);
					final HashMultimap<String, AssemblyType<?>> grouping = HashMultimap.create();
					for (AssemblyType<?> type : controllerTypes) {
						String name = type.getName();
						name = name.substring(0, name.length() - ControllerString.length());
						grouping.put(name, type);
					}

					for (Map.Entry<String, AssemblyType<?>> entry : grouping.entries()) {
						final AssemblyType<?> value = entry.getValue();
						final Package aPackage = value.getPackage();
						final String pckName = (aPackage != null ? aPackage.getName() : "");
						final String controllerKey = getControllerKey(entry.getKey());
						if (!cache.contains(controllerKey, pckName)) {
							cache.put(controllerKey, pckName, Sets.<AssemblyType<?>>newHashSet());
						}
						cache.get(controllerKey, pckName).add(value);
					}
				}
			}
			finally {
				locker.unlock();
			}
		}
	}

	public Collection<Class<?>> getControllerTypes(final String controllerName, final Set<String> namespaces) {
		final Set<AssemblyType<?>> matchingTypes = Sets.newHashSet();

		final OutParam<Map<String, Set<AssemblyType<?>>>> nsLookup = OutParam.of(new TypeLiteral<Map<String, Set<AssemblyType<?>>>>() {
		});
		if (tryGetCacheValue(controllerName, nsLookup)) {
			final Map<String, Set<AssemblyType<?>>> nslookupMap = nsLookup.get();
			if (namespaces != null) {
				for (String requestedNamespace : namespaces) {
					for (String targetNamespaceGrouping : nslookupMap.keySet()) {
						if (isNamespaceMatch(requestedNamespace, targetNamespaceGrouping)) {
							matchingTypes.addAll(nslookupMap.get(targetNamespaceGrouping));
						}
					}
				}
			} else {
				for (Set<AssemblyType<?>> types : nslookupMap.values()) {
					matchingTypes.addAll(types);
				}
			}
		}

		return Collections2.transform(matchingTypes, new Function<AssemblyType<?>, Class<?>>() {
			@Override
			public Class<?> apply(@Nullable final AssemblyType<?> input) {
				if (input == null)
					return null;
				return input.getTypeClass();
			}
		});
	}

	private boolean tryGetCacheValue(final String controllerName, final OutParam<Map<String, Set<AssemblyType<?>>>> nsLookup) {
		final String key = getControllerKey(controllerName);
		if (cache.containsRow(key)) {
			nsLookup.set(cache.row(key));
			return true;
		}
		return false;
	}

	private static String getControllerKey(final String controllerName) {
		return Ascii.toUpperCase(Strings.nullToEmpty(controllerName));
	}

	private static final String ControllerString = "Controller";
	private static final Class<IController> IControllerClass = IController.class;

	private static Predicate<AssemblyType<?>> isControllerType() {
		return new Predicate<AssemblyType<?>>() {
			@Override
			public boolean apply(@Nullable final AssemblyType<?> input) {
				return input != null
					&& !input.isAbstract()
					&& input.isA(IControllerClass);
			}
		};
	}

	@SuppressWarnings({"SimplifiableIfStatement"})
	private static boolean isNamespaceMatch(String requestedNamespace, final String targetNamespace) {
		if (requestedNamespace == null) {
			return false;
		}
		if (requestedNamespace.length() == 0) {
			return true;
		}
		if (!requestedNamespace.endsWith(".*")) {
			return requestedNamespace.equalsIgnoreCase(targetNamespace);
		}

		requestedNamespace = requestedNamespace.substring(0, requestedNamespace.length() - ".*".length());
		if (!targetNamespace.startsWith(requestedNamespace))
			return false;

		if (requestedNamespace.length() == targetNamespace.length())
			return true;

		return targetNamespace.length() >= requestedNamespace.length()
			&& targetNamespace.charAt(requestedNamespace.length()) == '.';

	}
}
