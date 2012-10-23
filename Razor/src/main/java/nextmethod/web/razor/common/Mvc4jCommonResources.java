package nextmethod.web.razor.common;

import nextmethod.i18n.ResourceBundleFactory;

public final class Mvc4jCommonResources {

	private Mvc4jCommonResources() {}

	private static ICommonResources resourceBundle = null;

	public static ICommonResources CommonResources() {
		if (resourceBundle == null) {
			resourceBundle = ResourceBundleFactory.newInstance(ICommonResources.class);
		}
		return resourceBundle;
	}
}
