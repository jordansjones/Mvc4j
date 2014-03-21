package nextmethod.codedom;

import javax.annotation.Nonnull;
import java.io.Serializable;

public class CodeConstructor extends CodeMemberMethod implements Serializable {

	private static final long serialVersionUID = -2249219535116018529L;

	private CodeExpressionCollection baseConstructorArgs;
	private CodeExpressionCollection chainedConstructorArgs;

	public CodeExpressionCollection getBaseConstructorArgs() {
		if (baseConstructorArgs == null) {
			baseConstructorArgs = new CodeExpressionCollection();
		}
		return baseConstructorArgs;
	}

	public CodeExpressionCollection getChainedConstructorArgs() {
		if (chainedConstructorArgs == null) {
			chainedConstructorArgs = new CodeExpressionCollection();
		}
		return chainedConstructorArgs;
	}

	@Override
	public void accept(@Nonnull final ICodeDomVisitor visitor) {
		visitor.visit(this);
	}

}
