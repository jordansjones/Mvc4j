package nextmethod.web.mvc;

/**
 *
 */
abstract class ActionExecutor<TReturn> {

	public abstract TReturn invoke(final ControllerBase controllerBase, final Object[] parameters);

}
