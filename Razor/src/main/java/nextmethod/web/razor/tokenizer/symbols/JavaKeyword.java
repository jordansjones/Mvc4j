package nextmethod.web.razor.tokenizer.symbols;

import javax.annotation.Nonnull;

public enum JavaKeyword {
	Abstract("abstract"),
	Byte("byte"),
	Class("class"),
	If("if"),
	New("new"),
	Short("short"),
	Try("try"),
	Volatile("volatile"),
	Do("do"),
	Switch("switch"),
	While("while"),
	Case("case"),
	Float("float"),
	Null("null"),
	Private("private"),
	This("this"),
	Return("return"),
	Super("super"),
	Catch("catch"),
	Continue("continue"),
	Double("double"),
	For("for"),
	Object("object"),
	Protected("protected"),
	Static("static"),
	False("false"),
	Public("public"),
	Final("final"),
	Throw("throw"),
	Decimal("decimal"),
	Else("else"),
	String("String"),
	Boolean("boolean"),
	Char("char"),
	Character("Character"),
	Default("default"),
	Long("long"),
	Void("void"),
	Enum("enum"),
	Finally("finally"),
	True("true"),
	Interface("interface"),
	Break("break"),
	Package("package")
	;

	private final String keyword;

	private JavaKeyword(@Nonnull final String keyword) {
		this.keyword = keyword;
	}

	public String keyword() {
		return keyword;
	}
}
