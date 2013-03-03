package nextmethod.codedom;

import nextmethod.annotations.Internal;
import nextmethod.base.NotImplementedException;

import javax.annotation.Nonnull;
import java.io.Serializable;

public class CodeObject implements Serializable {

	private static final long serialVersionUID = 4834403342206879294L;

	private Object userData;

	public Object getUserData() {
		if (userData == null) {
			userData = new Object();
		}
		return userData;
	}

	@Internal
	void accept(@Nonnull final ICodeDomVisitor visitor) {
		throw new NotImplementedException();
	}

}
