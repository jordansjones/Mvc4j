package nextmethod.codedom;

import nextmethod.annotations.Internal;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 *
 */
public class CodeTypeConstructor extends CodeMemberMethod implements Serializable {

	private static final long serialVersionUID = -875510709045390214L;

	public CodeTypeConstructor() {
		this.setName(".cctor");
	}

	@Internal
	@Override
	void accept(@Nonnull final ICodeDomVisitor visitor) {
		visitor.visit(this);
	}
}
