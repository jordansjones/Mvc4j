package nextmethod.codedom;

import nextmethod.annotations.TODO;

import java.io.Serializable;

@TODO
public class CodePropertyReferenceExpression extends CodeExpression implements Serializable {

	private static final long serialVersionUID = 6391820834110266987L;

	private CodeExpression targetObject;
	private String propertyName;

	public CodePropertyReferenceExpression() {
	}

	public CodePropertyReferenceExpression(final CodeExpression targetObject, final String propertyName) {
		this.targetObject = targetObject;
		this.propertyName = propertyName;
	}
}
