package nextmethod.web.mvc;

import nextmethod.web.HttpException;
import nextmethod.web.InvalidOperationException;
import nextmethod.web.routing.RequestContext;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.web.mvc.Mvc4jResources.MvcResources;

/**
 * 
 */
public abstract class ControllerBase implements IController {

	private final SingleEntryGate executeWasCalledGate = new SingleEntryGate();

	private ControllerContext controllerContext;
	private boolean validateRequest;

	@Override
	public void execute(final RequestContext requestContext) throws HttpException {
		checkNotNull(requestContext);

		verifyExecuteCalledOnce();
		initialize(requestContext);
		executeCore();
	}

	protected void initialize(final RequestContext requestContext) {
		controllerContext = new ControllerContext(requestContext, this);
	}

	protected abstract void executeCore() throws HttpException;

	public ControllerContext getControllerContext() {
		return controllerContext;
	}

	public void setControllerContext(final ControllerContext controllerContext) {
		this.controllerContext = controllerContext;
	}

	public boolean isValidateRequest() {
		return validateRequest;
	}

	public void setValidateRequest(final boolean validateRequest) {
		this.validateRequest = validateRequest;
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
