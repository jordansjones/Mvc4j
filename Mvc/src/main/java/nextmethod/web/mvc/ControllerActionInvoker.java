package nextmethod.web.mvc;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.reflection.TypeOfHelper.getType;

/**
 *
 */
public class ControllerActionInvoker implements IActionInvoker {

//	protected FilterInfo getFilters(final ControllerContext controllerContext, final ActionDescriptor actionDescriptor) {
//		actionDescriptor
//	}

	@Override
	public boolean invokeAction(final ControllerContext controllerContext, final String actionName) {
		checkNotNull(controllerContext);
		checkArgument(!Strings.isNullOrEmpty(actionName));

		final ControllerDescriptor controllerDescriptor = getControllerDescriptor(controllerContext);
		final ActionDescriptor actionDescriptor = findAction(controllerContext, controllerDescriptor, actionName);
		if (actionDescriptor != null) {
			// TODO GetFilters

			invokeActionMethod(controllerContext, actionDescriptor, Maps.<String, Object>newHashMap());
			return true;
		}

		// Notify controller that no method matched
		return false;
	}

	protected ActionResult invokeActionMethod(final ControllerContext controllerContext, final ActionDescriptor actionDescriptor, @Nullable final Map<String, Object> parameters) {
		final Object returnValue = actionDescriptor.execute(controllerContext, parameters);
		// TODO: final ActionResult result = createActionResult(controllerContext, actionDescriptor, returnValue);
		return null;
	}

	protected ControllerDescriptor getControllerDescriptor(final ControllerContext controllerContext) {
		// TODO: DescriptorCache
		return new ReflectedControllerDescriptor(getType(controllerContext.getController()));
	}

	protected ActionDescriptor findAction(final ControllerContext context, final ControllerDescriptor controllerDescriptor, final String actionName) {
		return controllerDescriptor.findAction(context, actionName);
	}
}
