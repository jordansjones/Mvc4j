package nextmethod.codedom;

import nextmethod.base.Strings;

import java.io.Serializable;

/**
 *
 */
public class CodeTypeDelegate extends CodeTypeDeclaration implements Serializable {

	private static final long serialVersionUID = 6788307286035287069L;

	private CodeParameterDeclarationExpressionCollection parameters;
	private CodeTypeReference returnType;

	public CodeTypeDelegate() {
		super.getBaseTypes().add(new CodeTypeReference("System.Delegate"));
	}

	public CodeTypeDelegate(final String name) {
		this();
		setName(name);
	}

	public CodeParameterDeclarationExpressionCollection getParameters() {
		if (parameters == null) {
			parameters = new CodeParameterDeclarationExpressionCollection();
		}
		return parameters;
	}

	public CodeTypeReference getReturnType() {
		if (returnType == null) {
			returnType = new CodeTypeReference(Strings.Empty);
		}
		return returnType;
	}

	public void setReturnType(final CodeTypeReference returnType) {
		this.returnType = returnType;
	}
}
