package nextmethod.web.razor.text;

import nextmethod.base.IAction;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;

public final class TextExtensions {

	private TextExtensions() {}

	public static void seek(@Nonnull final ITextBuffer buffer, final int characters) {
		buffer.setPosition(buffer.getPosition() + characters);
	}

	public static ITextDocument toDocument(@Nonnull final ITextBuffer buffer) {
		if (buffer instanceof ITextDocument) return ITextDocument.class.cast(buffer);
		return new SeekableTextReader(buffer);
	}

	public static LookaheadToken beginLookahead(@Nonnull final ITextBuffer buffer) {
		final int start = buffer.getPosition();
		return new LookaheadToken(new IAction<Void>() {
			@Override
			public Void invoke() {
				buffer.setPosition(start);
				return null;
			}
		});
	}

	public static String readToEnd(@Nonnull final ITextBuffer buffer) {
		final StringBuilder builder = new StringBuilder();
		int read;
		while ((read = buffer.read()) != -1) {
			builder.append((char)read);
		}
		return builder.toString();
	}

	public static String readToEnd(@Nonnull final Reader reader) {
		final StringBuilder sb = new StringBuilder();
		try {
			int read;
			while ((read = reader.read()) != -1)
				sb.append((char) read);
		}
		catch (IOException ignored) {}

		return sb.toString();
	}
}
