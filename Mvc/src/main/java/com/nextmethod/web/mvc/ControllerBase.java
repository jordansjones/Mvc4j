package com.nextmethod.web.mvc;

import com.nextmethod.web.routing.RequestContext;

/**
 * User: Jordan
 * Date: 8/6/11
 * Time: 12:00 AM
 */
public abstract class ControllerBase implements IController {

	private ControllerContext controllerContext;

	@Override
	public void execute(final RequestContext requestContext) {
	}

	protected void initialize(final RequestContext requestContext) {

	}

	protected abstract void executeCore();


	public ControllerContext getControllerContext() {
		return controllerContext;
	}

	public void setControllerContext(final ControllerContext controllerContext) {
		this.controllerContext = controllerContext;
	}
}
