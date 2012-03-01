package nextmethod.web.mvc;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.inject.TypeLiteral;
import nextmethod.Idx;
import nextmethod.OutParam;
import nextmethod.reflection.ClassInfo;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static nextmethod.reflection.TypeOfHelper.typeOf;

/**
 *
 */
final class ControllerTypeCache {

	private Table<Idx, String, Set<ClassInfo<?>>> cache;
	private final Lock locker = new ReentrantLock();

	public int size() {
		return 0;
	}

	public void ensureInitialized(final IBuildManager buildManager) {
		if (cache == null) {
			locker.lock();
			try {
				if (cache == null) {
					cache = HashBasedTable.create();

					final ImmutableList<ClassInfo<?>> controllerTypes = TypeCacheUtil.getFilteredTypesFromAssemblies(MagicStrings.ControllerTypeCacheName, isControllerType(), buildManager);
					final HashMultimap<String, ClassInfo<?>> grouping = HashMultimap.create();
					for (ClassInfo<?> type : controllerTypes) {
						String name = type.getName();
						name = name.substring(0, name.length() - ControllerString.length());
						grouping.put(name, type);
					}

					for (Map.Entry<String, ClassInfo<?>> entry : grouping.entries()) {
						final ClassInfo<?> value = entry.getValue();
						final Package aPackage = value.getPackage();
						final String pckName = (aPackage != null ? aPackage.getName() : "");
						final Idx controllerKey = Idx.of(entry.getKey());
						if (!cache.contains(controllerKey, pckName)) {
							cache.put(controllerKey, pckName, Sets.<ClassInfo<?>>newHashSet());
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

	public Collection<Class<?>> getControllerTypes(final String controllerName, final Set<String> packages) {
		final Set<ClassInfo<?>> matchingTypes = Sets.newHashSet();

		final OutParam<Map<String, Set<ClassInfo<?>>>> packageLookup = OutParam.of(new TypeLiteral<Map<String, Set<ClassInfo<?>>>>() {
		});
		if (tryGetCacheValue(controllerName, packageLookup)) {
			final Map<String, Set<ClassInfo<?>>> packageMap = packageLookup.get();
			if (packages != null) {
				for (String requestedPackage : packages) {
					for (String targetPackageGroup : packageMap.keySet()) {
						if (isPackageMatch(requestedPackage, targetPackageGroup)) {
							matchingTypes.addAll(packageMap.get(targetPackageGroup));
						}
					}
				}
			} else {
				for (Set<ClassInfo<?>> types : packageMap.values()) {
					matchingTypes.addAll(types);
				}
			}
		}

		return Collections2.transform(matchingTypes, new Function<ClassInfo<?>, Class<?>>() {
			@Override
			public Class<?> apply(@Nullable final ClassInfo<?> input) {
				if (input == null)
					return null;
				return input.wrappedType();
			}
		});
	}

	private boolean tryGetCacheValue(final String controllerName, final OutParam<Map<String, Set<ClassInfo<?>>>> nsLookup) {
		final Idx key = Idx.of(controllerName);
		if (cache.containsRow(key)) {
			nsLookup.set(cache.row(key));
			return true;
		}
		return false;
	}

	private static final String ControllerString = "Controller";
	private static final ClassInfo<IController> IControllerClass = typeOf(IController.class);

	private static Predicate<ClassInfo<?>> isControllerType() {
		return new Predicate<ClassInfo<?>>() {
			@Override
			public boolean apply(@Nullable final ClassInfo<?> input) {
				return input != null
					&& !input.isAbstract()
					&& input.isA(IControllerClass);
			}
		};
	}

	@SuppressWarnings({"SimplifiableIfStatement"})
	private static boolean isPackageMatch(String requestedPackage, final String targetPackage) {
		if (requestedPackage == null) {
			return false;
		}
		if (requestedPackage.length() == 0) {
			return true;
		}
		if (!requestedPackage.endsWith(".*")) {
			return requestedPackage.equalsIgnoreCase(targetPackage);
		}

		requestedPackage = requestedPackage.substring(0, requestedPackage.length() - ".*".length());
		if (!targetPackage.startsWith(requestedPackage))
			return false;

		if (requestedPackage.length() == targetPackage.length())
			return true;

		return targetPackage.length() >= requestedPackage.length()
			&& targetPackage.charAt(requestedPackage.length()) == '.';

	}
}
