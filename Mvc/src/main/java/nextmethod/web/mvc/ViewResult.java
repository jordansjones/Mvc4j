package nextmethod.web.mvc;

import nextmethod.web.InvalidOperationException;

import static nextmethod.SystemHelpers.NewLine;
import static nextmethod.web.mvc.Mvc4jResources.MvcResources;

/**
 *
 */
public class ViewResult extends ViewResultBase {

	private String masterName;

	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(final String masterName) {
		this.masterName = masterName;
	}

	@Override
	protected ViewEngineResult findView(final ControllerContext context) {
		final ViewEngineResult result = getViewEngineCollection().findView(context, getViewName(), masterName);
		if (result.getView() != null)
			return result;


		final StringBuilder sb = new StringBuilder();
//		for (Object o : resu) {
		sb.append(NewLine())
			.append("No Where! Now GET OFF MY LAWN!");
//		}
		throw new InvalidOperationException(String.format(
			MvcResources().getString("common.viewNotFound"),
			getViewName(),
			sb.toString()
		));
	}

}
