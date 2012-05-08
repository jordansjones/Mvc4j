package nextmethod.web.razor.utils;

public final class CharUtils {
	private CharUtils() {}

	public static boolean isNonNewLineWhitespace(final char c) {
		return Character.isWhitespace(c) && !isNewLine(c);
	}

	public static boolean isNewLine(final char c) {
		return c == 0x000d // Carriage return
			|| c == 0x000a // Linefeed
			|| c == 0x2028 // Line separator
			|| c == 0x2029; // Paragraph separator
	}
}
