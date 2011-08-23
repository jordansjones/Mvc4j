package nextmethod.web.mvc;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class ResultExecutingContext extends ControllerContext {

	private ActionResult result;
	private boolean cancel;

	// parameterless constructor used for mocking
	public ResultExecutingContext() {
	}

	public ResultExecutingContext(final ControllerContext controllerContext, final ActionResult result) {
		super(controllerContext);
		this.result = checkNotNull(result);
	}

	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(final boolean cancel) {
		this.cancel = cancel;
	}

	public ActionResult getResult() {
		return result;
	}

	public void setResult(final ActionResult result) {
		this.result = result;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof ResultExecutingContext)) return false;

		final ResultExecutingContext that = (ResultExecutingContext) o;

		if (cancel != that.cancel) return false;
		if (result != null ? !result.equals(that.result) : that.result != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result1 = result != null ? result.hashCode() : 0;
		result1 = 31 * result1 + (cancel ? 1 : 0);
		return result1;
	}
}
