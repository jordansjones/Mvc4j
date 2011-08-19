package com.nextmethod.web.mvc;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.nextmethod.OutParam;
import com.nextmethod.TypeHelpers;
import com.nextmethod.web.InvalidOperationException;
import com.nextmethod.web.routing.RequestContext;
import com.nextmethod.web.routing.Route;
import com.nextmethod.web.routing.RouteBase;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.nextmethod.SystemHelpers.NewLine;
import static com.nextmethod.web.mvc.Mvc4jResources.MvcResources;

/**
 *
 */
class DefaultControllerFactory implements IControllerFactory {

	protected static final String NamespacesKey = "Namespaces";
	protected static final String UseNamespaceFallbackKey = "UseNamespaceFallback";

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
			final IController controller = IController.class.cast(controllerType.newInstance());
			injector.injectMembers(controller);
			return controller;
		}
		catch (Exception e) {
			throw new InvalidOperationException(String.format(
				MvcResources().getString("defaultControllerFactory.errorCreatingController"),
				controllerType.getName()
			));
		}
	}

	protected Class<?> getControllerType(@Nullable final RequestContext requestContext, final String controllerName) {
		checkArgument(!Strings.isNullOrEmpty(controllerName), MvcResources().getString("common.nullOrEmpty"));

		final OutParam<Object> routeNamespacesObj = OutParam.of(Object.class);
		Class<?> match;

		// First search in the current route's namespace collection
		if (requestContext != null && requestContext.getRouteData().getDataTokens().tryGetValue(NamespacesKey, routeNamespacesObj)) {
			final Iterable<String> routeNamespaces = TypeHelpers.typeAs(routeNamespacesObj.get(), new TypeLiteral<Iterable<String>>() {
			});
			if (routeNamespaces != null && !Iterables.isEmpty(routeNamespaces)) {
				final Set<String> nsSet = Sets.newHashSet(routeNamespaces);
				match = getControllerTypeWithinPackages(requestContext.getRouteData().getRoute(), controllerName, nsSet);

				if (match != null || Boolean.FALSE.equals(requestContext.getRouteData().getDataTokens().get(UseNamespaceFallbackKey))) {
					return match;
				}
			}
		}

		checkNotNull(requestContext);

		// Then search in the application's default namespace collection
		if (!controllerBuilder.get().getDefaultNamespaces().isEmpty()) {
			final Set<String> nsDefaults = Sets.newHashSet(controllerBuilder.get().getDefaultNamespaces());
			assert requestContext != null;
			match = getControllerTypeWithinPackages(requestContext.getRouteData().getRoute(), controllerName, nsDefaults);
			if (match != null)
				return match;
		}

		// If all else fails, search every package
		assert requestContext != null;
		return getControllerTypeWithinPackages(requestContext.getRouteData().getRoute(), controllerName, null);
	}

	private Class<?> getControllerTypeWithinPackages(final RouteBase route, final String controllerName, @Nullable final Set<String> namespaces) {
		// Once the master list of controllers has been created, we can quickly index into it
		controllerTypeCache.ensureInitialized(buildManager);

		final Collection<Class<?>> controllerTypes = controllerTypeCache.getControllerTypes(controllerName, namespaces);
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

	static InvalidOperationException createAmbiguousControllerException(final RouteBase route, final String controllerName, final Collection<Class<?>> matchingTypes) {
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

		return new InvalidOperationException(errorText);
	}
}
