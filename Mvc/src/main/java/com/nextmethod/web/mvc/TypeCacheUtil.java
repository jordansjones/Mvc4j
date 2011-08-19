package com.nextmethod.web.mvc;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.Set;

/**
 *
 */
final class TypeCacheUtil {

	private TypeCacheUtil() {
	}

	private static final Predicate<AssemblyType<?>> IsPublicClass = createTypeIsPublicClass();

	public static ImmutableList<AssemblyType<?>> getFilteredTypesFromAssemblies(final String cacheName, final Predicate<AssemblyType<?>> predicate, final IBuildManager buildManager) {
		final TypeCacheSerializer serializer = new TypeCacheSerializer();

		// Try to read from cache on disk first
		Iterable<AssemblyType<?>> matchingTypes = readTypesFromCache(cacheName, predicate, buildManager, serializer);
		if (matchingTypes != null)
			return ImmutableList.copyOf(matchingTypes);

		// If reading from cache failed, enumerate over every assembly looking for a matching type
		matchingTypes = filterTypesInAssemblies(buildManager, predicate);

		// Cache results to disk
		saveTypesToCache(cacheName, matchingTypes, buildManager, serializer);

		return ImmutableList.copyOf(matchingTypes);
	}

	private static Iterable<AssemblyType<?>> filterTypesInAssemblies(final IBuildManager buildManager, final Predicate<AssemblyType<?>> predicate) {
		final Set<AssemblyType<?>> typesSoFar = Sets.newHashSet();

		final ImmutableCollection<Assembly> assemblies = buildManager.getReferencedAssemblies();
		for (Assembly assembly : assemblies) {
			typesSoFar.addAll(assembly.getEntries());
		}

		final Predicate<AssemblyType<?>> classPredicate = Predicates.and(IsPublicClass, predicate);

		return Iterables.filter(typesSoFar, classPredicate);
	}

	static Iterable<AssemblyType<?>> readTypesFromCache(final String cacheName, final Predicate<AssemblyType<?>> predicate, final IBuildManager buildManager, final TypeCacheSerializer serializer) {
		// TODO: This
		return null;
	}

	static void saveTypesToCache(final String cacheName, final Iterable<AssemblyType<?>> matchingTypes, final IBuildManager buildManager, final TypeCacheSerializer serializer) {
		// TODO: This
	}

	private static Predicate<AssemblyType<?>> createTypeIsPublicClass() {
		return new Predicate<AssemblyType<?>>() {
			@Override
			public boolean apply(@Nullable final AssemblyType<?> input) {
				return input != null
					&& input.isPublic()
					&& input.isClass()
					&& !input.isAbstract();
			}
		};
	}
}
