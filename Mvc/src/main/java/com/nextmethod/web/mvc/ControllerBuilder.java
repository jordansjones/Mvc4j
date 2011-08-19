package com.nextmethod.web.mvc;

import com.google.common.collect.Sets;

import javax.inject.Inject;
import java.util.Set;

/**
 *
 */
class ControllerBuilder {

	@Inject
	private IControllerFactory controllerFactory;
	private final Set<String> namespaces;

	ControllerBuilder() {
		this.namespaces = Sets.newHashSet();
	}

	public IControllerFactory getControllerFactory() {
		return controllerFactory;
	}

	public Set<String> getDefaultNamespaces() {
		return this.namespaces;
	}
}
