package nextmethod.web.razor.parser.syntaxtree;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumSet;

public enum AcceptedCharacters {

	None,
	NewLine,
	WhiteSpace,
	NonWhiteSpace,;

	public static final EnumSet<AcceptedCharacters> AllWhiteSpace = EnumSet.of(NewLine, WhiteSpace);
	public static final EnumSet<AcceptedCharacters> Any = EnumSet.allOf(AcceptedCharacters.class);
	public static final EnumSet<AcceptedCharacters> NotAny = EnumSet.noneOf(AcceptedCharacters.class);
	public static final EnumSet<AcceptedCharacters> AnyExceptNewLine = EnumSet.of(NonWhiteSpace, WhiteSpace);

	public static final EnumSet<AcceptedCharacters> SetOfNone = EnumSet.of(None);

	public static EnumSet<AcceptedCharacters> setOf(@Nullable final AcceptedCharacters... values) {
		if (values == null || values.length < 1) return NotAny;

		final AcceptedCharacters first = values[0];
		if (values.length == 1)
			return EnumSet.of(first);

		return EnumSet.of(first, Arrays.copyOfRange(values, 1, values.length));
	}

}
