package nextmethod.codedom;

import nextmethod.annotations.TODO;

import java.io.Serializable;

@TODO
public class CodeAssignStatement extends CodeStatement implements Serializable {

	private static final long serialVersionUID = -1217184902214014662L;

	CodeExpression left, right;

	public CodeAssignStatement() {
	}

	public CodeAssignStatement(final CodeExpression left, final CodeExpression right) {
		this.left = left;
		this.right = right;
	}
}
