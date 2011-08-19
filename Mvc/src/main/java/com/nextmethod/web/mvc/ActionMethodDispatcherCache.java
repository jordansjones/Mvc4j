package com.nextmethod.web.mvc;

import com.google.common.base.Suppliers;

import java.lang.reflect.Method;

/**
 *
 */
final class ActionMethodDispatcherCache extends ReaderWriterCache<Method, ActionMethodDispatcher> {

	ActionMethodDispatcherCache() {
	}

	public ActionMethodDispatcher getDispatcher(final Method actionMethod) {
		return fetchOrCreateItem(actionMethod, Suppliers.ofInstance(new ActionMethodDispatcher(actionMethod)));
	}
}
