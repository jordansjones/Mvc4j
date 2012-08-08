package nextmethod.codedom;

import nextmethod.annotations.TODO;

import java.io.Serializable;

@TODO
public class CodeMemberField extends CodeTypeMember implements Serializable {

	private static final long serialVersionUID = -7687259189542307333L;

	private CodeTypeReference type;

	public CodeMemberField() {
	}

	public CodeMemberField(final Class<?> type, final String name) {
		this.setName(name);
	}
}
