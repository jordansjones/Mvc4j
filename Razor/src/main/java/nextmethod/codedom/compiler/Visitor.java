package nextmethod.codedom.compiler;

import nextmethod.base.NotImplementedException;
import nextmethod.codedom.CodeAssignStatement;
import nextmethod.codedom.CodeConstructor;
import nextmethod.codedom.CodeMemberField;
import nextmethod.codedom.CodeMemberMethod;
import nextmethod.codedom.CodePrimitiveExpression;
import nextmethod.codedom.CodePropertyReferenceExpression;
import nextmethod.codedom.CodeSnippetTypeMember;
import nextmethod.codedom.ICodeDomVisitor;

/**
*
*/
class Visitor implements ICodeDomVisitor {

	private final CodeGenerator cg;

	Visitor(final CodeGenerator cg) {
		this.cg = cg;
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
	public void visit(final CodeAssignStatement o) {
		throw new NotImplementedException();
	}

	@Override
	public void visit(final CodeConstructor o) {
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
	public void visit(final CodeSnippetTypeMember o) {
		throw new NotImplementedException();
	}
}
