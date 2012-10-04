package nextmethod.codedom;

import java.io.Serializable;

// TODO
public class CodeSnippetTypeMember extends CodeTypeMember implements Serializable {

	private static final long serialVersionUID = 7331691969139845195L;

	private String text;

	public CodeSnippetTypeMember() {
	}

	public CodeSnippetTypeMember(final String text) {
		this.text = text;
	}
}
