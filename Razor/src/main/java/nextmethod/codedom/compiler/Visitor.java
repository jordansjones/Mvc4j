package nextmethod.codedom.compiler;

import nextmethod.base.NotImplementedException;
import nextmethod.codedom.*;

/**
 *
 */
class Visitor implements ICodeDomVisitor {

	private final CodeGenerator cg;

	Visitor(final CodeGenerator cg) {
		this.cg = cg;
	}

	@Override
	public void visit(final CodeArgumentReferenceExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeArrayCreateExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeArrayIndexerExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeBaseReferenceExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeBinaryOperatorExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeCastExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeDefaultValueExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeDelegateCreateExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeDelegateInvokeExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeDirectionExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeEventReferenceExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeFieldReferenceExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeIndexerExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeMethodInvokeExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeMethodReferenceExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeObjectCreateExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeParameterDeclarationExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodePrimitiveExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodePropertyReferenceExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodePropertySetValueReferenceExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeSnippetExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeThisReferenceExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeTypeOfExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeTypeReferenceExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeVariableReferenceExpression o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeAssignStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeAttachEventStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeCommentStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeConditionStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeExpressionStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeGotoStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeIterationStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeLabeledStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeMethodReturnStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeRemoveEventStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeThrowExceptionStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeTryCatchFinallyStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeVariableDeclarationStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeConstructor o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeEntryPointMethod o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeMemberEvent o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeMemberField o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeMemberMethod o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeMemberProperty o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeSnippetTypeMember o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeTypeConstructor o) {
		throw new NotImplementedException();
	}

}
