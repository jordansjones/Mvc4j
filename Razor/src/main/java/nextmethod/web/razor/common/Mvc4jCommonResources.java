package nextmethod.web.razor.common;

import java.util.ResourceBundle;

public final class Mvc4jCommonResources {

	private Mvc4jCommonResources() {
	}

	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(String.format(
		"%s.CommonResources",
		Mvc4jCommonResources.class.getPackage().getName()
	));

	public static ResourceBundle CommonResources() {
		return resourceBundle;
	}
}
