package nextmethod.web.mvc;

/**
 *
 */
public class EmptyResult extends ActionResult {

	private static final EmptyResult instance = new EmptyResult();

	public static EmptyResult instance() {
		return instance;
	}

	@Override
	public void executeResult(final ControllerContext context) {
	}
}
