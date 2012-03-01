package nextmethod.web.mvc;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.util.Modules;
import nextmethod.web.IHttpApplication;
import org.reflections.Reflections;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class Mvc4jHttpRuntime extends GuiceServletContextListener {

	private static final String ApplicationClassName = "MvcApplication";

	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		super.contextInitialized(servletContextEvent);
		final ServletContext servletContext = servletContextEvent.getServletContext();
		servletContext.addFilter(GuiceFilter.class.getName(), new GuiceFilter())
			.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

//		ServletContextAnalyzer.analyze(servletContext);
	}

	@SuppressWarnings({"unchecked"})
	private Class<? extends IHttpApplication> getHttpApplicationInternal() {
		final ClassLoader cl = this.getClass().getClassLoader();
		Class<? extends IHttpApplication> application = null;
		try {
			final Class<?> tmpClass = cl.loadClass(ApplicationClassName);
			if (IHttpApplication.class.isAssignableFrom(tmpClass)) {
				application = (Class<? extends IHttpApplication>) tmpClass;
			}
		}
		catch (ClassNotFoundException e) {
			// Do nothing
		}
		return application != null
			? application
			: DefaultHttpApplication.class;
	}

	private ImmutableList<Module> getModules() {
		final List<Module> modules = Lists.newArrayList();
		final Reflections reflections = new Reflections(new Object[]{Module.class});
		final Set<Class<? extends Module>> subTypesOf = reflections.getSubTypesOf(Module.class);
		return ImmutableList.copyOf(modules);
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(
			new Mvc4jIoCServletModule(getHttpApplicationInternal()),
			new Mvc4jIocVirtualPathProviderModule(),
			Modules.override(new Mvc4jIocCoreModule()).with(getModules())
		);
	}

	static class DefaultHttpApplication implements IHttpApplication {

	}
}
