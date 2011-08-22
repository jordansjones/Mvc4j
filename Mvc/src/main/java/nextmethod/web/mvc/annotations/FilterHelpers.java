package nextmethod.web.mvc.annotations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import nextmethod.reflection.AnnotationInfo;

import java.util.Collection;
import java.util.Comparator;

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

}
