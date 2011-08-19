package com.nextmethod.web.mvc;

/**
 *
 */
public abstract class ViewResultBase extends ActionResult {


	@Override
	public void executeResult(final ControllerContext context) {
	}

	protected abstract ViewEngineResult findView(final ControllerContext context);
}
