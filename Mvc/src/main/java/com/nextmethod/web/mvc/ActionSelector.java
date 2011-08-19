package com.nextmethod.web.mvc;

/**
 *
 */
abstract class ActionSelector {

	public abstract boolean invoke(final ControllerContext controllerContext);

}
