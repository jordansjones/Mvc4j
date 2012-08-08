package nextmethod.codedom;

import nextmethod.annotations.TODO;

import java.io.Serializable;

@TODO
public class CodeSnippetStatement extends CodeStatement implements Serializable {

	private static final long serialVersionUID = -3520176438251317371L;

	private String value;

	public CodeSnippetStatement() {
	}

	public CodeSnippetStatement(final String value) {
		this.value = value;
	}
}
