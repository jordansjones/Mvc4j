package nextmethod.web.mvc.annotations;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import nextmethod.reflection.AnnotationInfo;
import nextmethod.reflection.MethodInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import static nextmethod.reflection.TypeOfHelper.asAnnotationInfo;

/**
 *
 */
public final class FilterHelpers {

	// "Static" class
	private FilterHelpers() {
	}

	private static final Ordering<AnnotationInfo<Filter>> filterOrdering = Ordering.from(new Comparator<AnnotationInfo<Filter>>() {
		@Override
		public int compare(final AnnotationInfo<Filter> o1, final AnnotationInfo<Filter> o2) {
			return Ints.compare(o1.wrappedType().order(), o2.wrappedType().order());
		}
	});

	public static ImmutableList<AnnotationInfo<Filter>> orderedCopy(final Collection<AnnotationInfo<Filter>> filters) {
		return ImmutableList.copyOf(filterOrdering.sortedCopy(filters));
	}

	public static ImmutableCollection<AnnotationInfo<Filter>> getFilters(final MethodInfo methodInfo, @Nullable final FilterTarget filterTarget) {
		if (filterTarget == null || methodInfo == null)
			return ImmutableList.of();


		return ImmutableSet.copyOf(Iterables.concat(
			getTargetFilter(methodInfo, filterTarget),
			getTargetFilters(methodInfo, filterTarget)
		));
	}

	private static ImmutableCollection<AnnotationInfo<Filter>> getTargetFilter(final MethodInfo methodInfo, @Nonnull final FilterTarget filterTarget) {
		switch (filterTarget) {
			case Controller:
				return methodInfo.getReflectedType().getAnnotations(Filter.class);
			default:
				return methodInfo.getAnnotations(Filter.class);
		}
	}

	private static ImmutableCollection<AnnotationInfo<Filter>> getTargetFilters(final MethodInfo methodInfo, @Nonnull final FilterTarget filterTarget) {
		final ImmutableCollection<AnnotationInfo<Filters>> annotations;
		switch (filterTarget) {
			case Controller:
				annotations = methodInfo.getReflectedType().getAnnotations(Filters.class);
				break;
			default:
				annotations = methodInfo.getAnnotations(Filters.class);
				break;
		}

		if (annotations == null)
			return ImmutableList.of();

		final Set<Filter> filters = Sets.newHashSet();
		for (AnnotationInfo<Filters> info : annotations) {
			final Filter[] values = info.wrappedType().value();
			if (values != null && values.length > 0)
				filters.addAll(Lists.newArrayList(values));
		}
		return asAnnotationInfo(filters);
	}
}
