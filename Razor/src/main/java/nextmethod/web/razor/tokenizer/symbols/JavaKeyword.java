package nextmethod.web.razor.tokenizer.symbols;

import javax.annotation.Nonnull;

public enum JavaKeyword {
	Abstract("abstract"),
	Assert("assert"),
	Boolean("boolean"),
	Break("break"),
	Byte("byte"),
	Case("case"),
	Catch("catch"),
	Char("char"),
	Class("class"),
	Const("const"),
	Continue("continue"),
	Default("default"),
	Do("do"),
	Double("double"),
	Else("else"),
	Enum("enum"),
	Extends("extends"),
	Final("final"),
	Finally("finally"),
	Float("float"),
	For("for"),
	Goto("goto"),
	If("if"),
	Implements("implements"),
	Import("import"),
	Instanceof("instanceof"),
	Int("int"),
	Interface("interface"),
	Long("long"),
	Native("native"),
	New("new"),
	Package("package"),
	Private("private"),
	Protected("protected"),
	Public("public"),
	Return("return"),
	Short("short"),
	Static("static"),
	Strictfp("strictfp"),
	Super("super"),
	Switch("switch"),
	Synchronized("synchronized"),
	This("this"),
	Throw("throw"),
	Throws("throws"),
	Transient("transient"),
	Try("try"),
	Void("void"),
	Volatile("volatile"),
	While("while"),

	Null("null"),
	False("false"),
	True("true"),
	;

	private final String keyword;

	private JavaKeyword(@Nonnull final String keyword) {
		this.keyword = keyword;
	}

	public String keyword() {
		return keyword;
	}
}
