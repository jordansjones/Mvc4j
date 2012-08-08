package nextmethod.codedom;

import nextmethod.annotations.TODO;

import java.io.Serializable;

@TODO
public class CodePrimitiveExpression extends CodeExpression implements Serializable {

	private static final long serialVersionUID = 6501060541239915815L;

	private Object value;

	public CodePrimitiveExpression() {
	}

	public CodePrimitiveExpression(final Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(final Object value) {
		this.value = value;
	}
}
