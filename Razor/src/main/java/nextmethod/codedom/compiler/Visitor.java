package nextmethod.codedom.compiler;

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
		cg.generateArgumentReferenceExpression(o);
	}

	@Override
	public void visit(final CodeArrayCreateExpression o) {
		cg.generateArrayCreateExpression(o);
	}

	@Override
	public void visit(final CodeArrayIndexerExpression o) {
		cg.generateArrayIndexerExpression(o);
	}

	@Override
	public void visit(final CodeBaseReferenceExpression o) {
		cg.generateBaseReferenceExpression(o);
	}

	@Override
	public void visit(final CodeBinaryOperatorExpression o) {
		cg.generateBinaryOperatorExpression(o);
	}

	@Override
	public void visit(final CodeCastExpression o) {
		cg.generateCastExpression(o);
	}

	@Override
	public void visit(final CodeDefaultValueExpression o) {
		cg.generateDefaultValueExpression(o);
	}

	@Override
	public void visit(final CodeDelegateCreateExpression o) {
		cg.generateDelegateCreateExpression(o);
	}

	@Override
	public void visit(final CodeDelegateInvokeExpression o) {
		cg.generateDelegateInvokeExpression(o);
	}

	@Override
	public void visit(final CodeDirectionExpression o) {
		cg.generateDirectionExpression(o);
	}

	@Override
	public void visit(final CodeEventReferenceExpression o) {
		cg.generateEventReferenceExpression(o);
	}

	@Override
	public void visit(final CodeFieldReferenceExpression o) {
		cg.generateFieldReferenceExpression(o);
	}

	@Override
	public void visit(final CodeIndexerExpression o) {
		cg.generateIndexerExpression(o);
	}

	@Override
	public void visit(final CodeMethodInvokeExpression o) {
		cg.generateMethodInvokeExpression(o);
	}

	@Override
	public void visit(final CodeMethodReferenceExpression o) {
		cg.generateMethodReferenceExpression(o);
	}

	@Override
	public void visit(final CodeObjectCreateExpression o) {
		cg.generateObjectCreateExpression(o);
	}

	@Override
	public void visit(final CodeParameterDeclarationExpression o) {
		cg.generateParameterDeclarationExpression(o);
	}

	@Override
	public void visit(final CodePrimitiveExpression o) {
		cg.generatePrimitiveExpression(o);
	}

	@Override
	public void visit(final CodePropertyReferenceExpression o) {
		cg.generatePropertyReferenceExpression(o);
	}

	@Override
	public void visit(final CodePropertySetValueReferenceExpression o) {
		cg.generatePropertySetValueReferenceExpression(o);
	}

	@Override
	public void visit(final CodeSnippetExpression o) {
		cg.generateSnippetExpression(o);
	}

	@Override
	public void visit(final CodeThisReferenceExpression o) {
		cg.generateThisReferenceExpression(o);
	}

	@Override
	public void visit(final CodeTypeOfExpression o) {
		cg.generateTypeOfExpression(o);
	}

	@Override
	public void visit(final CodeTypeReferenceExpression o) {
		cg.generateTypeReferenceExpression(o);
	}

	@Override
	public void visit(final CodeVariableReferenceExpression o) {
		cg.generateVariableReferenceExpression(o);
	}

	@Override
	public void visit(final CodeAssignStatement o) {
		cg.generateAssignStatement(o);
	}

	@Override
	public void visit(final CodeAttachEventStatement o) {
		cg.generateAttachEventStatement(o);
	}

	@Override
	public void visit(final CodeCommentStatement o) {
		cg.generateCommentStatement(o);
	}

	@Override
	public void visit(final CodeConditionStatement o) {
		cg.generateConditionStatement(o);
	}

	@Override
	public void visit(final CodeExpressionStatement o) {
		cg.generateExpressionStatement(o);
	}

	@Override
	public void visit(final CodeGotoStatement o) {
		cg.generateGotoStatement(o);
	}

	@Override
	public void visit(final CodeIterationStatement o) {
		cg.generateIterationStatement(o);
	}

	@Override
	public void visit(final CodeLabeledStatement o) {
		cg.generateLabeledStatement(o);
	}

	@Override
	public void visit(final CodeMethodReturnStatement o) {
		cg.generateMethodReturnStatement(o);
	}

	@Override
	public void visit(final CodeRemoveEventStatement o) {
		cg.generateRemoveEventStatement(o);
	}

	@Override
	public void visit(final CodeThrowExceptionStatement o) {
		cg.generateThrowExceptionStatement(o);
	}

	@Override
	public void visit(final CodeTryCatchFinallyStatement o) {
		cg.generateTryCatchFinallyStatement(o);
	}

	@Override
	public void visit(final CodeVariableDeclarationStatement o) {
		cg.generateVariableDeclarationStatement(o);
	}

	@Override
	public void visit(final CodeConstructor o) {
		cg.generateConstructor(o, cg.getCurrentClass());
	}

	@Override
	public void visit(final CodeEntryPointMethod o) {
		cg.generateEntryPointMethod(o, cg.getCurrentClass());
	}

	@Override
	public void visit(final CodeMemberEvent o) {
		cg.generateEvent(o, cg.getCurrentClass());
	}

	@Override
	public void visit(final CodeMemberField o) {
		cg.generateField(o);
	}

	@Override
	public void visit(final CodeMemberMethod o) {
		cg.generateMethod(o, cg.getCurrentClass());
	}

	@Override
	public void visit(final CodeMemberProperty o) {
		cg.generateProperty(o, cg.getCurrentClass());
	}

	@Override
	public void visit(final CodeSnippetTypeMember o) {
		int indent = cg.getIndent();
		cg.setIndent(0);
		cg.generateSnippetMember(o);

		if (cg.getOptions().isVerbatimOrder()) {
			cg.getOutput().println();
		}
		cg.setIndent(indent);
	}

	@Override
	public void visit(final CodeTypeConstructor o) {
		cg.generateTypeConstructor(o);
	}

}
