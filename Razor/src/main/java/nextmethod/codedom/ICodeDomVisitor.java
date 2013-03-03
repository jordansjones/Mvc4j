package nextmethod.codedom;

public interface ICodeDomVisitor {

	// CodeExpression
	void visit (CodeArgumentReferenceExpression o);
	void visit (CodeArrayCreateExpression o);
	void visit (CodeArrayIndexerExpression o);
	void visit (CodeBaseReferenceExpression o);
	void visit (CodeBinaryOperatorExpression o);
	void visit (CodeCastExpression o);
	void visit (CodeDefaultValueExpression o);
	void visit (CodeDelegateCreateExpression o);
	void visit (CodeDelegateInvokeExpression o);
	void visit (CodeDirectionExpression o);
	void visit (CodeEventReferenceExpression o);
	void visit (CodeFieldReferenceExpression o);
	void visit (CodeIndexerExpression o);
	void visit (CodeMethodInvokeExpression o);
	void visit (CodeMethodReferenceExpression o);
	void visit (CodeObjectCreateExpression o);
	void visit (CodeParameterDeclarationExpression o);
	void visit (CodePrimitiveExpression o);
	void visit (CodePropertyReferenceExpression o);
	void visit (CodePropertySetValueReferenceExpression o);
	void visit (CodeSnippetExpression o);
	void visit (CodeThisReferenceExpression o);
	void visit (CodeTypeOfExpression o);
	void visit (CodeTypeReferenceExpression o);
	void visit (CodeVariableReferenceExpression o);

	// CodeStatement
	void visit (CodeAssignStatement o);
	void visit (CodeAttachEventStatement o);
	void visit (CodeCommentStatement o);
	void visit (CodeConditionStatement o);
	void visit (CodeExpressionStatement o);
	void visit (CodeGotoStatement o);
	void visit (CodeIterationStatement o);
	void visit (CodeLabeledStatement o);
	void visit (CodeMethodReturnStatement o);
	void visit (CodeRemoveEventStatement o);
	void visit (CodeThrowExceptionStatement o);
	void visit (CodeTryCatchFinallyStatement o);
	void visit (CodeVariableDeclarationStatement o);

	// CodeTypeMember
	void visit (CodeConstructor o);
	void visit (CodeEntryPointMethod o);
	void visit (CodeMemberEvent o);
	void visit (CodeMemberField o);
	void visit (CodeMemberMethod o);
	void visit (CodeMemberProperty o);
	void visit (CodeSnippetTypeMember o);
	void visit (CodeTypeConstructor o);

}
