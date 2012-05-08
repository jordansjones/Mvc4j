package nextmethod.web.razor.parser.syntaxtree;

import java.util.EnumSet;

public enum AcceptedCharacters {

	None,
	NewLine,
	WhiteSpace,
	NonWhiteSpace,
	;

	public static EnumSet<AcceptedCharacters> AllWhiteSpace() {
		return EnumSet.of(NewLine, WhiteSpace);
	}

	public static EnumSet<AcceptedCharacters> Any() {
		return EnumSet.allOf(AcceptedCharacters.class);
	}

	public static EnumSet<AcceptedCharacters> AnyExceptNewLine() {
		return EnumSet.of(NonWhiteSpace, WhiteSpace);
	}

}
