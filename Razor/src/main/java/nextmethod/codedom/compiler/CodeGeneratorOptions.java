package nextmethod.codedom.compiler;


import nextmethod.base.SystemHelpers;

public class CodeGeneratorOptions {

	private boolean blankLinesBetweenMembers;
	private String bracingStyle;
	private boolean elseOnClosingProperty;
	private String indentString;
	private boolean verbatimOrder;
	private String newlineString = SystemHelpers.newLine();

	public boolean isBlankLinesBetweenMembers() {
		return blankLinesBetweenMembers;
	}

	public void setBlankLinesBetweenMembers(final boolean blankLinesBetweenMembers) {
		this.blankLinesBetweenMembers = blankLinesBetweenMembers;
	}

	public String getBracingStyle() {
		return bracingStyle;
	}

	public void setBracingStyle(final String bracingStyle) {
		this.bracingStyle = bracingStyle;
	}

	public boolean isElseOnClosingProperty() {
		return elseOnClosingProperty;
	}

	public void setElseOnClosingProperty(final boolean elseOnClosingProperty) {
		this.elseOnClosingProperty = elseOnClosingProperty;
	}

	public String getIndentString() {
		return indentString;
	}

	public void setIndentString(final String indentString) {
		this.indentString = indentString;
	}

	public boolean isVerbatimOrder() {
		return verbatimOrder;
	}

	public void setVerbatimOrder(final boolean verbatimOrder) {
		this.verbatimOrder = verbatimOrder;
	}

	public String getNewlineString() {
		return newlineString;
	}

	public void setNewlineString(final String newlineString) {
		this.newlineString = newlineString;
	}
}
