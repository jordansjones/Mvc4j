package nextmethod.web.razor.parser;

import com.google.common.base.CharMatcher;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ParserHelpers {
	private ParserHelpers() {}

	public static boolean isNewLine(final char val) {
		return val == '\r' || // Carriage return
			val == '\n' || // Linefeed
			val == '\u0085' || // Next line
			val == '\u2028' || // Line separator
			val == '\u2029'; // Paragraph separator
	}

	public static boolean isNewLine(final String val) {
		return val != null && (val.length() == 1 && isNewLine(val.charAt(0)) || "\r\n".equalsIgnoreCase(val));
	}

	public static boolean isWhitespace(final char val) {
		return val == ' ' ||
			val == '\f' ||
			val == '\t' ||
			val == '\u000B' || // Vertical Tab
			Character.isSpaceChar(val);
	}

	public static boolean isWhitespaceOrNewLine(final char val) {
		return isWhitespace(val) || isNewLine(val);
	}

	public static boolean isIdentifier(@Nonnull final String val) {
		return isIdentifier(val, true);
	}

	public static boolean isIdentifier(@Nonnull final String val, final boolean requireIdentifierStart) {
		Iterable<Character> characters = Lists.newArrayList(Lists.charactersOf(val));
		if (requireIdentifierStart) {
			characters = Iterables.skip(characters, 1);
		}
		final Character first = Iterables.getFirst(characters, null);
		return (!requireIdentifierStart || isIdentifierStart(first) && Iterables.all(characters, new Predicate<Character>() {
			@Override
			public boolean apply(@Nullable Character input) {
				return input != null && isIdentifierPart(input);
			}
		}));
	}

	public static boolean isIdentifierStart(final char val) {
		return val == '_' || isLetter(val);
	}

	public static boolean isIdentifierPart(final char val) {
		return isLetter(val)
			|| isDecimalDigit(val)
			|| isConnecting(val)
			|| isCombining(val)
			|| isFormatting(val);
	}

	public static boolean isTerminatingCharToken(final char value)
	{
		return isNewLine(value) || value == '\'';
	}

	public static boolean isTerminatingQuotedStringToken(final char value)
	{
		return isNewLine(value) || value == '"';
	}

	public static boolean isDecimalDigit(final char value)
	{
		return CharMatcher.DIGIT.matches(value);
	}

	public static boolean isLetterOrDecimalDigit(char value)
	{
		return isLetter(value) || isDecimalDigit(value);
	}

	public static boolean isLetter(char value)
	{
		return CharMatcher.JAVA_UPPER_CASE
			.or(CharMatcher.JAVA_LOWER_CASE)
			.or(CharMatcher.JAVA_LETTER)
			.matches(value);
	}

	public static boolean isFormatting(char value)
	{
		return false;
//		return Char.GetUnicodeCategory(value) == UnicodeCategory.Format;
	}

	public static boolean isCombining(char value)
	{
		return false;
//		var cat = Char.GetUnicodeCategory(value);
//		return cat == UnicodeCategory.SpacingCombiningMark || cat == UnicodeCategory.NonSpacingMark;
	}

	public static boolean isConnecting(char value)
	{
		return false;
//		return Char.GetUnicodeCategory(value) == UnicodeCategory.ConnectorPunctuation;
	}

//	public static string SanitizeClassName(string inputName)
//	{
//		if (!IsIdentifierStart(inputName[0]) && IsIdentifierPart(inputName[0]))
//		{
//			inputName = "_" + inputName;
//		}
//
//		return new String((from value in inputName
//			select IsIdentifierPart(value) ? value : '_')
//			.ToArray());
//	}

	public static boolean isEmailPart(final char character)
	{
		// Source: http://tools.ietf.org/html/rfc5322#section-3.4.1
		// We restrict the allowed characters to alpha-numerics and '_' in order to ensure we cover most of the cases where an
		// email address is intended without restricting the usage of code within JavaScript, CSS, and other contexts.
		return CharMatcher.JAVA_LETTER_OR_DIGIT.matches(character) || character == '_';
	}
}
