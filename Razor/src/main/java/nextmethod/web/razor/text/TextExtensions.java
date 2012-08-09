package nextmethod.web.razor.text;

import com.google.common.base.Predicate;
import nextmethod.base.Delegates;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
		return new LookaheadToken(new Delegates.IFunc<Void>() {
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

	public static String readUntil(@Nonnull final TextReader reader, final char terminator) {
		return readUntil(reader, terminator, false);
	}

	public static String readUntil(@Nonnull final TextReader reader, final char terminator, final boolean inclusive) {
		return readUntil(
			reader,
			new Predicate<Character>() {
				@Override
				public boolean apply(@Nullable final Character input) {
					return input != null && input == terminator;
				}
			},
			inclusive
		);
	}

	public static String readUntil(@Nonnull final TextReader reader, @Nonnull final Predicate<Character> condition) {
		return readUntil(reader, condition, false);
	}

	public static String readUntil(@Nonnull final TextReader reader, @Nonnull final Predicate<Character> condition, final boolean inclusive) {
		final StringBuilder sb = new StringBuilder();
		int ch = -1;
		while ((ch = reader.peek()) != -1 && !(condition.apply((char) ch))) {
			reader.read(); // Advance the reader
			sb.append((char) ch);
		}

		if (inclusive && reader.peek() != -1) {
			sb.append((char) reader.read());
		}
		return sb.toString();
	}

	public static String readWhile(@Nonnull final TextReader reader, @Nonnull final Predicate<Character> condition) {
		return readWhile(reader, condition, false);
	}

	public static String readWhile(@Nonnull final TextReader reader, @Nonnull final Predicate<Character> condition, final boolean inclusive) {
		return readUntil(reader, new Predicate<Character>() {
			@Override
			public boolean apply(@Nullable final Character input) {
				return !condition.apply(input);
			}
		}, inclusive);
	}

	public static String readWhiteSpace(@Nonnull final TextReader reader) {
		return readWhile(reader, new Predicate<Character>() {
			@Override
			public boolean apply(@Nullable final Character input) {
				return input != null && Character.isWhitespace(input);
			}
		});
	}

	public static String readUntilWhiteSpace(@Nonnull final TextReader reader) {
		return readUntil(reader, new Predicate<Character>() {
			@Override
			public boolean apply(@Nullable final Character input) {
				return input != null && Character.isWhitespace(input);
			}
		});
	}
}
