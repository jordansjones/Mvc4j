package com.nextmethod.web.mvc;

import com.nextmethod.web.InvalidOperationException;
import com.nextmethod.web.routing.RequestContext;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.nextmethod.web.mvc.Mvc4jResources.MvcResources;

/**
 * User: Jordan
 * Date: 8/6/11
 * Time: 12:00 AM
 */
public abstract class ControllerBase implements IController {

	private final SingleEntryGate executeWasCalledGate = new SingleEntryGate();

	private ControllerContext controllerContext;

	@Override
	public void execute(final RequestContext requestContext) {
		checkNotNull(requestContext);

		verifyExecuteCalledOnce();
		initialize(requestContext);
		executeCore();
	}

	protected void initialize(final RequestContext requestContext) {
		controllerContext = new ControllerContext(requestContext, this);
	}

	protected abstract void executeCore();


	public ControllerContext getControllerContext() {
		return controllerContext;
	}

	public void setControllerContext(final ControllerContext controllerContext) {
		this.controllerContext = controllerContext;
	}

	void verifyExecuteCalledOnce() {
		if (!executeWasCalledGate.tryEnter()) {
			throw new InvalidOperationException(String.format(
				MvcResources().getString("controllerBase.cannotHandleMultipleRequests"),
				this.getClass().getName()
			));
		}
	}
}
