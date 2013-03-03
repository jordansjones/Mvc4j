package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;

public class RazorError {

	private final String message;
	private final SourceLocation location;
	private final int length;

	public RazorError(@Nonnull final String message, @Nonnull final SourceLocation location) {
		this(message, location, 1);
	}

	public RazorError(@Nonnull final String message, final int absoluteIndex, final int lineIndex, final int columnIndex) {
		this(message, new SourceLocation(absoluteIndex, lineIndex, columnIndex));
	}

	public RazorError(@Nonnull final String message, final int absoluteIndex, final int lineIndex, final int columnIndex, final int length) {
		this(message, new SourceLocation(absoluteIndex, lineIndex, columnIndex), length);
	}

	public RazorError(String message, SourceLocation location, int length) {
		this.message = message;
		this.location = location;
		this.length = length;
	}

	@Override
	public String toString() {
		return String.format("Error @ %s(%s) - [%d]", location, message, length);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RazorError)) return false;

		RazorError that = (RazorError) o;
		return message.equals(that.message) && SourceLocation.isEqual(location, that.location);
	}

	@Override
	public int hashCode() {
		int result = message != null ? message.hashCode() : 0;
		result = 31 * result + (location != null ? location.hashCode() : 0);
		result = 31 * result + length;
		return result;
	}
}
