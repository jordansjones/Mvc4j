package nextmethod.web.mvc;

import com.google.common.base.Strings;

import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public abstract class ViewResultBase extends ActionResult {

	private String viewName = "";
	private IView view;
	@Inject
	private ViewEngineCollection viewEngineCollection;

	@Override
	public void executeResult(final ControllerContext context) {
		checkNotNull(context);
		if (Strings.isNullOrEmpty(viewName)) {
			viewName = context.getRouteData().getRequiredString(MagicStrings.ActionKey);
		}

		ViewEngineResult result = null;

		if (view == null) {
			result = findView(context);
			view = result.getView();
		}

		try {
			// TODO: This should be cleaner
			final PrintWriter writer = context.getHttpContext().getResponse().getServletResponse().getWriter();
			final ViewContext viewContext = new ViewContext();
			view.render(viewContext, writer);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		if (result != null)
			result.getViewEngine().releaseView(context, view);
	}

	protected abstract ViewEngineResult findView(final ControllerContext context);

	public String getViewName() {
		return viewName;
	}

	public void setViewName(final String viewName) {
		this.viewName = Strings.isNullOrEmpty(viewName) ? "" : viewName;
	}

	public IView getView() {
		return view;
	}

	public void setView(final IView view) {
		this.view = view;
	}

	public ViewEngineCollection getViewEngineCollection() {
		return viewEngineCollection;
	}

	public void setViewEngineCollection(final ViewEngineCollection viewEngineCollection) {
		this.viewEngineCollection = viewEngineCollection;
	}
}
