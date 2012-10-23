package nextmethod.web.razor.resources;

import nextmethod.i18n.ResourceBundleFactory;

/**
 *
 */
public final class Mvc4jRazorResources {

	private Mvc4jRazorResources() {}

	private static IRazorResources resourceBundle = null;

	public static IRazorResources RazorResources() {
		if (resourceBundle == null) {
			resourceBundle = ResourceBundleFactory.newInstance(IRazorResources.class);
		}
		return resourceBundle;
	}
}
