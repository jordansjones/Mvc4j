package com.nextmethod.web.mvc;

import java.lang.reflect.Method;
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

	static Object extractParameterFromMap(Object parameterInfo, Map<String, Object> parmeters, final Method actionMethod) {
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
}
