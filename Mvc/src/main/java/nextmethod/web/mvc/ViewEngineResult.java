package nextmethod.web.mvc;

/**
 *
 */
public class ViewEngineResult {

	private Iterable<String> searchedLocations;
	private IView view;
	private IViewEngine viewEngine;

	public ViewEngineResult(final Iterable<String> searchedLocations) {
		this.searchedLocations = searchedLocations;
	}

	public ViewEngineResult(final IView view, final IViewEngine viewEngine) {
		this.view = view;
		this.viewEngine = viewEngine;
	}

	public Iterable<String> getSearchedLocations() {
		return searchedLocations;
	}

	public IView getView() {
		return view;
	}

	public IViewEngine getViewEngine() {
		return viewEngine;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof ViewEngineResult)) return false;

		final ViewEngineResult that = (ViewEngineResult) o;

		if (searchedLocations != null ? !searchedLocations.equals(that.searchedLocations) : that.searchedLocations != null)
			return false;
		if (view != null ? !view.equals(that.view) : that.view != null) return false;
		if (viewEngine != null ? !viewEngine.equals(that.viewEngine) : that.viewEngine != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = searchedLocations != null ? searchedLocations.hashCode() : 0;
		result = 31 * result + (view != null ? view.hashCode() : 0);
		result = 31 * result + (viewEngine != null ? viewEngine.hashCode() : 0);
		return result;
	}
}
