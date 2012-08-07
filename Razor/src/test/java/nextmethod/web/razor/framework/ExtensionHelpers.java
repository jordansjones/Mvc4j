package nextmethod.web.razor.framework;

import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.SourceLocationTracker;

public final class ExtensionHelpers {

	private ExtensionHelpers() {}

	public static SourceLocation getLocationAndAdvance(final SourceLocationTracker self, final String content) {
		final SourceLocation ret = self.getCurrentLocation();
		self.updateLocation(content);
		return ret;
	}
}
