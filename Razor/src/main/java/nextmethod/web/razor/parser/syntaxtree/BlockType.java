package nextmethod.web.razor.parser.syntaxtree;

/**
 *
 */
public enum BlockType {

	// Code
	Statement,
	Directive,
	Functions,
	Expression,
	Helper,

	// Markup
	Markup,
	Section,
	Template,

	// Special
	Comment

}
