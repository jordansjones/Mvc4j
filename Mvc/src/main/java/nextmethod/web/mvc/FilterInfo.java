package nextmethod.web.mvc;

import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 */
public class FilterInfo {

	private final List<IActionFilter> actionFilters = Lists.newArrayList();
	private final List<IAuthorizationFilter> authorizationFilters = Lists.newArrayList();
	private final List<IExceptionFilter> exceptionFilters = Lists.newArrayList();
	private final List<IResultFilter> resultFilters = Lists.newArrayList();

	public List<IActionFilter> getActionFilters() {
		return actionFilters;
	}

	public List<IAuthorizationFilter> getAuthorizationFilters() {
		return authorizationFilters;
	}

	public List<IExceptionFilter> getExceptionFilters() {
		return exceptionFilters;
	}

	public List<IResultFilter> getResultFilters() {
		return resultFilters;
	}
}
