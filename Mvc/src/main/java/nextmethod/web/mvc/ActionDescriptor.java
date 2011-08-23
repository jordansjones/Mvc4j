package nextmethod.web.mvc;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import nextmethod.OutParam;
import nextmethod.TypeHelpers;
import nextmethod.reflection.AnnotationInfo;
import nextmethod.reflection.ClassInfo;
import nextmethod.reflection.MethodInfo;
import nextmethod.web.mvc.annotations.Filter;
import nextmethod.web.mvc.annotations.FilterHelpers;
import nextmethod.web.mvc.annotations.FilterTarget;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static nextmethod.TypeHelpers.typeAs;
import static nextmethod.reflection.TypeOfHelper.typeOf;

/**
 *
 */
public abstract class ActionDescriptor {

	private static final ActionSelector[] emptySelectors = new ActionSelector[0];
	private static final ActionMethodDispatcherCache staticDispatcherCache = new ActionMethodDispatcherCache();

	private ActionMethodDispatcherCache instanceDispatcherCache;

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
		final ImmutableCollection<AnnotationInfo<Filter>> typeFilters = FilterHelpers.getFilters(actionMethod, FilterTarget.Controller);
		final ImmutableCollection<AnnotationInfo<Filter>> methodFilters = FilterHelpers.getFilters(actionMethod, FilterTarget.Action);
		final ImmutableList<AnnotationInfo<Filter>> orderedFilters = FilterHelpers.orderedCopy(removeOverriddenFilters(typeFilters, methodFilters));

		final FilterInfo filterInfo = new FilterInfo();
		mergeFiltersIntoList(orderedFilters, filterInfo.getActionFilters(), IActionFilter.class);
		mergeFiltersIntoList(orderedFilters, filterInfo.getAuthorizationFilters(), IAuthorizationFilter.class);
		mergeFiltersIntoList(orderedFilters, filterInfo.getExceptionFilters(), IExceptionFilter.class);
		mergeFiltersIntoList(orderedFilters, filterInfo.getResultFilters(), IResultFilter.class);

		return filterInfo;
	}


	@SuppressWarnings({"unchecked"})
	static <TFilter extends IMvcFilter> void mergeFiltersIntoList(@Nonnull final Collection<AnnotationInfo<Filter>> allFilters, @Nonnull final List<TFilter> destFilters, @Nonnull final Class<TFilter> filterClass) {
		for (AnnotationInfo<Filter> filterInfo : allFilters) {
			final Filter filter = filterInfo.wrappedType();
			final Class<? extends IMvcFilter> impl = filter.impl();
			if (impl != null && filterClass.isAssignableFrom(impl)) {
				final ClassInfo<IMvcFilter> classInfo = typeAs(typeOf(impl), TypeHelpers.<ClassInfo<IMvcFilter>>rawType());
				if (classInfo != null) {
					final OutParam<IMvcFilter> param = OutParam.of();
					if (classInfo.tryGetInstance(param)) {
						destFilters.add((TFilter) param.get());
					}
				}
			}
		}
	}

	static ImmutableList<AnnotationInfo<Filter>> removeOverriddenFilters(final ImmutableCollection<AnnotationInfo<Filter>> typeFilters, final ImmutableCollection<AnnotationInfo<Filter>> methodFilters) {
		// If an annotation is declared on both the controller and on an action method and that annotation's
		// type has AllowMultiple = false (which is the default for attributes), we should ignore the attributes
		// declared on the controller.

		// Key = attribute type
		// Value = -1 if AllowMultiple true, last index of this attribute type if AllowMultiple false
		final Map<Object, Integer> annsIndexes = Maps.newHashMap();
//		fil

		return ImmutableList.copyOf(Iterables.concat(typeFilters, methodFilters));
	}

}
