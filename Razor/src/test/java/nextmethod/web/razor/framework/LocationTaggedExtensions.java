package nextmethod.web.razor.framework;

import nextmethod.base.Strings;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.text.LocationTagged;

public final class LocationTaggedExtensions {

	private LocationTaggedExtensions() {}

	public static LocationTagged<String> locationTagged(final int offset, final int line) {
		return locationTagged(Strings.Empty, offset, line, offset);
	}

	public static LocationTagged<String> locationTagged(final int offset, final int line, final int col) {
		return locationTagged(Strings.Empty, offset, line, col);
	}

	public static LocationTagged<String> locationTagged(final String value, final int offset, final int line, final int col) {
		return new LocationTagged<>(value, offset, line, col);
	}

	public static LocationTagged<SpanCodeGenerator> locationTagged(final SpanCodeGenerator generator, final int offset, final int line, final int col) {
		return new LocationTagged<>(generator, offset, line, col);
	}
}
