package nextmethod.web.razor.resources;

import java.util.ResourceBundle;

/**
 *
 */
public final class Mvc4jRazorResources {

	private Mvc4jRazorResources() {}

	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(String.format(
		"%s.RazorResources",
		Mvc4jRazorResources.class.getPackage().getName()
	));

	public static ResourceBundle RazorResources() {
		return resourceBundle;
	}
}
