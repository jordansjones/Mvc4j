package com.nextmethod.web.mvc;

import com.google.inject.AbstractModule;
import com.nextmethod.web.routing.IRouteHandler;

import javax.inject.Singleton;

/**
 *
 */
class Mvc4jIocCoreModule extends AbstractModule {

	@Override
	protected void configure() {
		// Action Invoker
		bind(IActionInvoker.class).to(ControllerActionInvoker.class);
		// Build Manager
		bind(IBuildManager.class).to(BuildManagerWrapper.class);
		// Controller Builder
		bind(ControllerBuilder.class).in(Singleton.class);
		// Controller Factory
		bind(IControllerFactory.class).to(DefaultControllerFactory.class).in(Singleton.class);
		// URL route Handler
		bind(IRouteHandler.class).to(MvcRouteHandler.class);
		// Virtual Path Utility
		bind(VirtualPathUtility.class).in(Singleton.class);
	}

}
