package nextmethod.codedom;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

// TODO
public class CodeTypeReference extends CodeObject implements Serializable {

	private static final long serialVersionUID = 7348697595800892347L;

	private String baseType;

	public CodeTypeReference() {
	}

	public CodeTypeReference(final String baseType) {
		this.baseType = baseType;
	}

	public CodeTypeReference(final Class<?> baseType) {
		checkNotNull(baseType);

	}

	public String getBaseType() {
		return baseType;
	}
}
