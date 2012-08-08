package nextmethod.web.mvc;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import nextmethod.OutParam;
import nextmethod.reflection.ClassInfo;
import nextmethod.web.http.routing.RouteCollection;
import nextmethod.web.http.routing.RouteTable;

import javax.annotation.Nullable;

import static nextmethod.reflection.TypeOfHelper.typeOf;

/**
 *
 */
public abstract class AreaRegistration {

	public abstract String getAreaName();

	public abstract void registerArea(final AreaRegistrationContext context);

	void createContextAndRegister(final RouteCollection routes, final Object state) {
		final AreaRegistrationContext context = new AreaRegistrationContext(getAreaName(), routes, state);

		final String thisPackage = getClass().getPackage().getName();
		if (thisPackage != null) {
			context.getPackages().add(thisPackage + ".*");
		}

		registerArea(context);
	}

	private static boolean isAreaRegistrationType(final ClassInfo<?> cls) {
		if (cls == null)
			return false;
		final ClassInfo<?> info = typeOf(cls.wrappedType());
		return info != null
			&& info.isA(typeOf(AreaRegistration.class))
			&& info.hasDefaultConstructor();
	}


	@Inject
	private static Provider<IBuildManager> buildManagerProvider;

	public static void registerAllAreas() {
		registerAllAreas(null);
	}

	public static void registerAllAreas(@Nullable final Object state) {
		registerAllAreas(RouteTable.getRoutes(), state);
	}

	@SuppressWarnings({"unchecked"})
	static void registerAllAreas(final RouteCollection routes, @Nullable final Object state) {
		final ImmutableList<ClassInfo<?>> types = TypeCacheUtil.getFilteredTypesFromAssemblies(MagicStrings.AreaTypeCacheName, new Predicate<ClassInfo<?>>() {
			@Override
			public boolean apply(@Nullable final ClassInfo<?> input) {
				return isAreaRegistrationType(input);
			}
		}, buildManagerProvider.get());

		for (ClassInfo<?> type : types) {
			final Class<AreaRegistration> typeClass = (Class<AreaRegistration>) type.wrappedType();
			final OutParam<AreaRegistration> registration = OutParam.of(typeClass);
			if (typeOf(typeClass).tryGetInstance(registration)) {
				registration.get().createContextAndRegister(routes, state);
			}
		}
	}
}
