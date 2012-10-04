package nextmethod.codedom;

import java.io.Serializable;

// TODO
public class CodeAnnotationArgument implements Serializable {

	private static final long serialVersionUID = 6690697501718470562L;

	private String name;
	private CodeExpression value;

	public CodeAnnotationArgument() {
	}

	public CodeAnnotationArgument(final CodeExpression value) {
		this.value = value;
	}

	public CodeAnnotationArgument(final String name, final CodeExpression value) {
		this.name = name;
		this.value = value;
	}
}
