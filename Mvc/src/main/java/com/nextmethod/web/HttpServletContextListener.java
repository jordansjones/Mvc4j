package com.nextmethod.web;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;

import java.util.List;

public abstract class HttpServletContextListener extends GuiceServletContextListener {

	protected abstract Class<? extends IHttpApplication> getHttpApplication();

	protected abstract void loadModules(final List<Module> modules);

	private Class<? extends IHttpApplication> getHttpApplicationInternal() {
		final Class<? extends IHttpApplication> application = getHttpApplication();
		return application != null
			? application
			: DefaultHttpApplication.class;
	}

	private ImmutableList<Module> getModules() {
		final List<Module> modules = Lists.newArrayList();
		this.loadModules(modules);
		return ImmutableList.copyOf(modules);
	}


	@Override
	protected final Injector getInjector() {
		final List<Module> modules = Lists.<Module>newArrayList(new Mvc4jIoCModule(getHttpApplicationInternal()));
		modules.addAll(getModules());

		return Guice.createInjector(modules);
	}
}
