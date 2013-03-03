package nextmethod.codedom;

import nextmethod.annotations.Internal;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 *
 */
public class CodeThisReferenceExpression extends CodeExpression implements Serializable {

	private static final long serialVersionUID = 7421880660873931266L;

	public CodeThisReferenceExpression() {
	}

	@Internal
	@Override
	void accept(@Nonnull final ICodeDomVisitor visitor) {
		visitor.visit(this);
	}
}
