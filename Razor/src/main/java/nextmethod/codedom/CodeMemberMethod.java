package nextmethod.codedom;


import javax.annotation.Nonnull;
import java.io.Serializable;

public class CodeMemberMethod extends CodeTypeMember implements Serializable {

	private static final long serialVersionUID = 2082561759474834442L;

	private CodeTypeReferenceCollection implementationTypes;
	private CodeParameterDeclarationExpressionCollection parameters;
	private CodeTypeReference privateImplements;
	private CodeTypeReference returnType;
	private CodeStatementCollection statements;
	private CodeAnnotationDeclarationCollection returnAnnotations;

	CodeTypeParameterCollection typeParameters;

	public CodeMemberMethod() {
	}

	public CodeTypeReferenceCollection getImplementationTypes() {
		if (implementationTypes == null) {
			implementationTypes = new CodeTypeReferenceCollection();
			// PopulationImplementationTypes Event
		}
		return implementationTypes;
	}

	public CodeParameterDeclarationExpressionCollection getParameters() {
		if (parameters == null) {
			parameters = new CodeParameterDeclarationExpressionCollection();
			// PopulateParameters Event
		}
		return parameters;
	}

	public CodeTypeReference getPrivateImplementationType() {
		return privateImplements;
	}

	public void setPrivateImplementationType(final CodeTypeReference value) {
		this.privateImplements = value;
	}

	public CodeTypeReference getReturnType() {
		if (returnType == null) {
			return new CodeTypeReference(Void.class);
		}
		return returnType;
	}

	public void setReturnType(final CodeTypeReference returnType) {
		this.returnType = returnType;
	}

	public CodeStatementCollection getStatements() {
		if (statements == null) {
			statements = new CodeStatementCollection();
			// PopulateStatements Event
		}
		return statements;
	}

	public CodeAnnotationDeclarationCollection getReturnTypeCustomAnnotations() {
		if (returnAnnotations == null) {
			returnAnnotations = new CodeAnnotationDeclarationCollection();
		}
		return returnAnnotations;
	}

	public CodeTypeParameterCollection getTypeParameters() {
		if (typeParameters == null) {
			typeParameters = new CodeTypeParameterCollection();
		}
		return typeParameters;
	}

	@Override
	public void accept(@Nonnull final ICodeDomVisitor visitor) {
		visitor.visit(this);
	}
}
