package nextmethod.web.razor.text;

import nextmethod.web.razor.parser.ParserHelpers;

import javax.annotation.Nonnull;

public class SourceLocationTracker {

	private int absoluteIndex = 0;
	private int lineIndex = 0;
	private int characterIndex = 0;
	private SourceLocation currentLocation;

	public SourceLocationTracker() {
		this(SourceLocation.Zero);
	}

	public SourceLocationTracker(@Nonnull final SourceLocation currentLocation) {
		this.currentLocation = currentLocation;
		updateInternalState();
	}

	public SourceLocation getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(final SourceLocation currentLocation) {
		if (SourceLocation.isNotEqual(this.currentLocation, currentLocation)) {
			this.currentLocation = currentLocation;
			updateInternalState();
		}
	}

	public void updateLocation(final char charRead, final char nextChar) {
		absoluteIndex++;
		if (ParserHelpers.isNewLine(charRead) && (charRead != '\r' || nextChar != '\n')) {
			lineIndex++;
			characterIndex = 0;
		}
		else {
			characterIndex++;
		}
		updateLocation();
	}

	@SuppressWarnings("ForLoopReplaceableByForEach")
	public SourceLocationTracker updateLocation(@Nonnull final String content) {
		final char[] chars = content.toCharArray();
		final int len = chars.length - 1;
		for (int i = 0; i < chars.length; i++) {
			char nextChar = '\0';
			if (i < len) {
				nextChar = chars[i + 1];
			}
			updateLocation(chars[i], nextChar);
		}
		return this;
	}

	private void updateLocation() {
		setCurrentLocation(new SourceLocation(absoluteIndex, lineIndex, characterIndex));
	}

	private void updateInternalState() {
		absoluteIndex = currentLocation.getAbsoluteIndex();
		lineIndex = currentLocation.getLineIndex();
		characterIndex = currentLocation.getCharacterIndex();
	}

	public static SourceLocation calculateNewLocation(@Nonnull final SourceLocation lastPosition, @Nonnull final String newContent) {
		return new SourceLocationTracker(lastPosition).updateLocation(newContent).currentLocation;
	}
}
