package nextmethod.web.mvc;

/**
 *
 */
public abstract class ControllerDescriptor {

	public abstract ActionDescriptor findAction(final ControllerContext controllerContext, final String actionName);

}
