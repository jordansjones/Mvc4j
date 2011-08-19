package com.nextmethod.web.mvc;

import com.google.common.base.Strings;

import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class ReflectedControllerDescriptor extends ControllerDescriptor {

	private final Class<? extends IController> controllerCls;
	private final ActionMethodSelector selector;

	public ReflectedControllerDescriptor(final Class<? extends IController> controllerCls) {
		this.controllerCls = controllerCls;
		this.selector = new ActionMethodSelector(controllerCls);
	}

	@Override
	public ActionDescriptor findAction(final ControllerContext controllerContext, final String actionName) {
		checkNotNull(controllerContext);
		checkArgument(!Strings.isNullOrEmpty(actionName));

		final Method actionMethod = selector.findActionMethod(controllerContext, actionName);
		if (actionMethod == null)
			return null;

		return new ReflectedActionDescriptor(actionMethod, actionName, this);
	}
}
