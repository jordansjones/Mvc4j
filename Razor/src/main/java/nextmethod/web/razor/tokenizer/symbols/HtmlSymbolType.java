package nextmethod.web.razor.tokenizer.symbols;

public enum HtmlSymbolType {

	Unknown,
	Text, // Text which isn't one fo the below
	WhiteSpace, // Non-newline WhiteSpace
	NewLine,
	OpenAngle, // <
	Bang, // !
	Solidus, // /
	QuestionMark, // ?
	DoubleHyphen, // --
	LeftBracket, // [
	CloseAngle, // >
	RightBracket, // ]
	Equals, // =
	DoubleQuote, // "
	SingleQuote, // '
	Transition, // @
	Colon,
	RazorComment,
	RazorCommentStar,
	RazorCommentTransition
}
