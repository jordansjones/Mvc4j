package nextmethod.web.razor.tokenizer;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

public final class JavaHelpers {

	private JavaHelpers() {}

	public static boolean isIdentifierStart(final char c) {
		return Character.isJavaIdentifierStart(c);
	}

	public static final Predicate<Character> IsIdentifierPartPredicate = input -> input != null && Character.isJavaIdentifierPart(input);

	public static boolean isIdentifierPart(final char c) {
		return IsIdentifierPartPredicate.apply(c);
	}

	public static boolean isRealLiteralSuffix(final char c) {
		return c == 'F' ||
			c == 'f' ||
			c == 'D' ||
			c == 'd';
	}

	public static boolean isIdentifierPartByCharMatcher(final char c) {
		final int type = Character.getType(c);
		return type == Character.NON_SPACING_MARK ||
			type == Character.COMBINING_SPACING_MARK ||
			type == Character.CONNECTOR_PUNCTUATION ||
			type == Character.FORMAT;
	}
}
