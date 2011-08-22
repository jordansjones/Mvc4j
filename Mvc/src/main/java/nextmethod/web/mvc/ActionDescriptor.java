package nextmethod.web.mvc;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import nextmethod.reflection.AnnotationInfo;
import nextmethod.reflection.MethodInfo;
import nextmethod.web.mvc.annotations.Filter;
import nextmethod.web.mvc.annotations.FilterHelpers;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: Jordan
 * Date: 8/6/11
 * Time: 12:20 AM
 */
public abstract class ActionDescriptor {

	private static final ActionMethodDispatcherCache staticDispatcherCache = new ActionMethodDispatcherCache();
	private ActionMethodDispatcherCache instanceDispatcherCache;

	private static final ActionSelector[] emptySelectors = new ActionSelector[0];

	public abstract Object execute(final ControllerContext controllerContext, final Map<String, Object> parameters);

	static Object extractParameterFromMap(Object parameterInfo, Map<String, Object> parmeters, final MethodInfo actionMethod) {
		Object value = null;


		return value;
	}

	public ActionMethodDispatcherCache getDispatcherCache() {
		if (instanceDispatcherCache == null)
			instanceDispatcherCache = staticDispatcherCache;

		return instanceDispatcherCache;
	}

	public void setDispatcherCache(final ActionMethodDispatcherCache instanceDispatcherCache) {
		this.instanceDispatcherCache = instanceDispatcherCache;
	}

	public FilterInfo getFilters() {
		return new FilterInfo();
	}

	static FilterInfo getFilters(final MethodInfo actionMethod) {
		final ImmutableCollection<AnnotationInfo<Filter>> typeFilters = actionMethod.getReflectedType().getAnnotations(Filter.class);
		final ImmutableCollection<AnnotationInfo<Filter>> methodFilters = actionMethod.getAnnotations(Filter.class);
		final ImmutableList<AnnotationInfo<Filter>> orderedFilters = FilterHelpers.orderedCopy(removeOverriddenFilters(typeFilters, methodFilters));

		return null;
	}

	static <TFilter extends IMvcFilter> void mergeFiltersIntoList(final Collection<AnnotationInfo<Filter>> allFilters, final List<TFilter> destFilters) {
		for (AnnotationInfo<Filter> filterInfo : allFilters) {
			final Filter filter = filterInfo.wrappedType();
//			typeAs()
		}
	}

	static ImmutableList<AnnotationInfo<Filter>> removeOverriddenFilters(final ImmutableCollection<AnnotationInfo<Filter>> typeFilters, final ImmutableCollection<AnnotationInfo<Filter>> methodFilters) {
		return ImmutableList.copyOf(Iterables.concat(typeFilters, methodFilters));
	}

}
