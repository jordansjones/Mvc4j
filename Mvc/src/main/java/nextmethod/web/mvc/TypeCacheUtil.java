package nextmethod.web.mvc;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import nextmethod.reflection.AssemblyInfo;
import nextmethod.reflection.ClassInfo;

import javax.annotation.Nullable;
import java.util.Set;

/**
 *
 */
final class TypeCacheUtil {

	private TypeCacheUtil() {
	}

	private static final Predicate<ClassInfo<?>> IsPublicClass = createTypeIsPublicClass();

	public static ImmutableList<ClassInfo<?>> getFilteredTypesFromAssemblies(final String cacheName, final Predicate<ClassInfo<?>> predicate, final IBuildManager buildManager) {
		final TypeCacheSerializer serializer = new TypeCacheSerializer();

		// Try to read from cache on disk first
		Iterable<ClassInfo<?>> matchingTypes = readTypesFromCache(cacheName, predicate, buildManager, serializer);
		if (matchingTypes != null)
			return ImmutableList.copyOf(matchingTypes);

		// If reading from cache failed, enumerate over every assembly looking for a matching type
		matchingTypes = filterTypesInAssemblies(buildManager, predicate);

		// Cache results to disk
		saveTypesToCache(cacheName, matchingTypes, buildManager, serializer);

		return ImmutableList.copyOf(matchingTypes);
	}

	private static Iterable<ClassInfo<?>> filterTypesInAssemblies(final IBuildManager buildManager, final Predicate<ClassInfo<?>> predicate) {
		final Set<ClassInfo<?>> typesSoFar = Sets.newHashSet();

		final ImmutableCollection<AssemblyInfo> assemblies = buildManager.getReferencedAssemblies();
		for (AssemblyInfo assembly : assemblies) {
			typesSoFar.addAll(assembly.getEntries());
		}

		final Predicate<ClassInfo<?>> classPredicate = Predicates.and(IsPublicClass, predicate);

		return Iterables.filter(typesSoFar, classPredicate);
	}

	static Iterable<ClassInfo<?>> readTypesFromCache(final String cacheName, final Predicate<ClassInfo<?>> predicate, final IBuildManager buildManager, final TypeCacheSerializer serializer) {
		// TODO: This
		return null;
	}

	static void saveTypesToCache(final String cacheName, final Iterable<ClassInfo<?>> matchingTypes, final IBuildManager buildManager, final TypeCacheSerializer serializer) {
		// TODO: This
	}

	private static Predicate<ClassInfo<?>> createTypeIsPublicClass() {
		return new Predicate<ClassInfo<?>>() {
			@Override
			public boolean apply(@Nullable final ClassInfo<?> input) {
				return input != null
					&& input.isPublic()
					&& input.isClass()
					&& !input.isAbstract();
			}
		};
	}
}
