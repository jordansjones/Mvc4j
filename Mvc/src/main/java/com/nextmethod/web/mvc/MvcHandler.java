package com.nextmethod.web.mvc;

import com.google.common.base.Predicate;
import com.nextmethod.OutParam;
import com.nextmethod.web.HttpException;
import com.nextmethod.web.IHttpContext;
import com.nextmethod.web.IHttpHandler;
import com.nextmethod.web.InvalidOperationException;
import com.nextmethod.web.routing.RequestContext;
import com.nextmethod.web.routing.RouteValueDictionary;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;

import static com.nextmethod.web.mvc.Mvc4jResources.MvcResources;
import static com.nextmethod.web.mvc.Mvc4jResources.Version;

public class MvcHandler implements IHttpHandler {


	private static final String ControllerKey = "controller";

	static final String MvcVersion = getMvcVersionString();
	public static final String MvcVersionHeaderName = "X-Mvc4j-Version";

	private final RequestContext requestContext;

	@Inject
	private ControllerBuilder controllerBuilder;

	public MvcHandler(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	@Override
	public void processRequest(IHttpContext httpContext) throws HttpException {
		final OutParam<IController> controller = OutParam.of(IController.class);
		final OutParam<IControllerFactory> factory = OutParam.of(IControllerFactory.class);

		processRequestInit(httpContext, controller, factory);

		try {
			controller.get().execute(requestContext);
		}
		finally {
			factory.get().releaseController(controller.get());
		}
	}

	private void processRequestInit(final IHttpContext httpContext, final OutParam<IController> controller, final OutParam<IControllerFactory> factory) {
		addVersionHeader(httpContext);
		removeOptionalRoutingParameters();

		// Get the controller type
		final String controllerName = requestContext.getRouteData().getRequiredString(ControllerKey);

		// Instantiate the controller and call execute
		factory.set(controllerBuilder.getControllerFactory());
		controller.set(factory.get().createController(requestContext, controllerName));
		if (controller.isNull()) {
			throw new InvalidOperationException(
				MessageFormat.format(
					MvcResources().getString("controllerBuilder.factoryReturnedNull"),
					factory.get().getClass().getSimpleName(),
					controllerName
				)
			);
		}
	}

//	@Override
//	public boolean isReusable() {
//		return false;
//	}

	private void addVersionHeader(final IHttpContext httpContext) {
		httpContext.getResponse().appendHeader(MvcVersionHeaderName, MvcVersion);
	}

	private void removeOptionalRoutingParameters() {
		final RouteValueDictionary rvd = requestContext.getRouteData().getValues();
		final Set<String> matchingKeys = rvd.filterEntries(new Predicate<Map.Entry<String, Object>>() {
			@Override
			public boolean apply(@Nullable final Map.Entry<String, Object> input) {
				return input != null
					&& input.getValue() != null
					&& UrlParameter.Optional.equals(input.getValue());
			}
		}).keySet();

		for (String s : matchingKeys) {
			rvd.remove(s);
		}
	}

	private static String getMvcVersionString() {
		return Version().getString("version");
	}

}
