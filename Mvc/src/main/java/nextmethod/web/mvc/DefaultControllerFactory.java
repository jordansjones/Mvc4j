package nextmethod.web.mvc;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.google.inject.Provider;
import nextmethod.OutParam;
import nextmethod.TypeHelpers;
import nextmethod.annotations.TODO;
import nextmethod.reflection.AmbiguousMatchException;
import nextmethod.web.InvalidOperationException;
import nextmethod.web.http.routing.RequestContext;
import nextmethod.web.http.routing.Route;
import nextmethod.web.http.routing.RouteBase;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.SystemHelpers.NewLine;
import static nextmethod.web.mvc.Mvc4jResources.MvcResources;

/**
 *
 */
@TODO
class DefaultControllerFactory implements IControllerFactory {

	private static ControllerTypeCache controllerTypeCache = new ControllerTypeCache();

	@Inject
	private Injector injector;
	@Inject
	private Provider<ControllerBuilder> controllerBuilder;
	@Inject
	private IBuildManager buildManager;

	@Override
	public IController createController(final RequestContext requestContext, final String controllerName) {
		checkNotNull(requestContext);
		checkArgument(!Strings.isNullOrEmpty(controllerName), MvcResources().getString("common.nullOrEmpty"));

		final Class<?> controllerType = getControllerType(requestContext, controllerName);
		return getControllerInstance(requestContext, controllerType);
	}

	@Override
	public void releaseController(final IController controller) {
	}

	protected IController getControllerInstance(final RequestContext requestContext, final Class<?> controllerType) {
		if (controllerType == null)
			// TODO: This should be some sort of HttpException
			throw new RuntimeException(String.format(
				MvcResources().getString("defaultControllerFactory.noControllerFound"),
				requestContext.getHttpContext().getRequest().getPath()
			));

		if (!IController.class.isAssignableFrom(controllerType))
			throw new IllegalArgumentException(String.format(
				MvcResources().getString("defaultControllerFactory.typeDoesNotSubclassControllerBase"),
				controllerType.getName()
			));

		try {
			return injector.getInstance(controllerType.asSubclass(IController.class));
//			final IController controller = IController.class.cast(controllerType.newInstance());
//			injector.injectMembers(controller);
//			return controller;
		} catch (Exception e) {
			throw new InvalidOperationException(String.format(
				MvcResources().getString("defaultControllerFactory.errorCreatingController"),
				controllerType.getName()
			));
		}
	}

	protected Class<?> getControllerType(@Nullable final RequestContext requestContext, final String controllerName) {
		checkArgument(!Strings.isNullOrEmpty(controllerName), MvcResources().getString("common.nullOrEmpty"));

		final OutParam<Object> routePackagesObj = OutParam.of(Object.class);
		Class<?> match;

		// First search in the current route's package collection
		if (requestContext != null && requestContext.getRouteData().getDataTokens().tryGetValue(MagicStrings.PackagesKey, routePackagesObj)) {
			final String[] routePackages = TypeHelpers.typeAs(routePackagesObj.get(), String[].class);
			if (routePackages != null && routePackages.length > 0) {
				final Set<String> nsSet = Sets.newHashSet(routePackages);
				match = getControllerTypeWithinPackages(requestContext.getRouteData().getRoute(), controllerName, nsSet);

				if (match != null || Boolean.FALSE.equals(requestContext.getRouteData().getDataTokens().get(MagicStrings.UsePackageFallbackKey))) {
					return match;
				}
			}
		}

		checkNotNull(requestContext);

		// Then search in the application's default package collection
		if (!controllerBuilder.get().getDefaultPackages().isEmpty()) {
			final Set<String> nsDefaults = Sets.newHashSet(controllerBuilder.get().getDefaultPackages());
			assert requestContext != null;
			match = getControllerTypeWithinPackages(requestContext.getRouteData().getRoute(), controllerName, nsDefaults);
			if (match != null)
				return match;
		}

		// If all else fails, search every package
		assert requestContext != null;
		return getControllerTypeWithinPackages(requestContext.getRouteData().getRoute(), controllerName, null);
	}

	private Class<?> getControllerTypeWithinPackages(final RouteBase route, final String controllerName, @Nullable final Set<String> packages) {
		// Once the master list of controllers has been created, we can quickly index into it
		controllerTypeCache.ensureInitialized(buildManager);

		final Collection<Class<?>> controllerTypes = controllerTypeCache.getControllerTypes(controllerName, packages);
		switch (controllerTypes.size()) {
			case 0:
				// No matching types
				return null;
			case 1:
				// Single matching type
				return Iterables.getFirst(controllerTypes, null);
			default:
				// Multiple matching types
				throw createAmbiguousControllerException(route, controllerName, controllerTypes);
		}
	}

	static AmbiguousMatchException createAmbiguousControllerException(final RouteBase route, final String controllerName, final Collection<Class<?>> matchingTypes) {
		final StringBuilder sb = new StringBuilder();
		for (Class<?> type : matchingTypes) {
			sb.append(NewLine());
			sb.append(type.getName());
		}

		final Route castRoute = TypeHelpers.typeAs(route, Route.class);
		final String errorText;
		if (castRoute != null) {
			errorText = String.format(MvcResources().getString("defaultControllerFactory.controllerNameAmbiguous.withRouteUrl"),
				controllerName, castRoute.getUrl(), sb.toString());
		} else {
			errorText = String.format(MvcResources().getString("defaultControllerFactory.controllerNameAmbiguous.withoutRouteUrl"),
				controllerName, sb.toString());
		}

		return new AmbiguousMatchException(errorText);
	}
}
