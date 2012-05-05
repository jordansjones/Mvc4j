package nextmethod.web.razor.text;

import com.google.common.base.Optional;

import javax.annotation.Nonnull;

public class SeekableTextReader implements ITextDocument {

	private int position = 0;
	private LineTrackingStringBuffer buffer = new LineTrackingStringBuffer();
	private SourceLocation location = SourceLocation.Zero;
	private Optional<Character> current;

	public SeekableTextReader(@Nonnull final String content) {
		buffer.append(content);
		updateState();
	}

//	public SeekableTextReader(@Nonnull final Reader reader) {
//		final StringBuilder sb = new StringBuilder();
//		CharStreams.copy(reader, sb);
//	}

	public SeekableTextReader(@Nonnull final ITextBuffer buffer) {
		this(TextExtensions.readToEnd(buffer));
	}

	@Override
	public SourceLocation getLocation() {
		return location;
	}

	@Override
	public int getLength() {
		return buffer.length();
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public void setPosition(int position) {
		if (this.position != position) {
			this.position = position;
			updateState();
		}
	}

	@Override
	public int read() {
		if (current == null || !current.isPresent())
			return -1;

		final Character chr = current.get();
		position++;
		updateState();
		return chr;
	}

	@Override
	public int peek() {
		if (current == null || !current.isPresent())
			return -1;
		return current.get();
	}

	private void updateState() {
		final int len = buffer.length();
		if (position < len) {
			final LineTrackingStringBuffer.CharRef chr = buffer.charAt(position);
			current = Optional.of(chr.get());
			location = chr.location();
		}
		else if (len == 0) {
			current = Optional.absent();
			location = SourceLocation.Zero;
		}
		else {
			current = Optional.absent();
			location = buffer.getEndLocation();
		}
	}
}
