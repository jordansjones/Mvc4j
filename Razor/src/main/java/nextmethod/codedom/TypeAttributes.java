package nextmethod.codedom;

import java.lang.reflect.Modifier;

/**
 *
 */
public enum TypeAttributes {

	Abstract(Modifier.ABSTRACT),
	Final(Modifier.FINAL),
	Interface(Modifier.INTERFACE),
	Private(Modifier.PRIVATE),
	Protected(Modifier.PROTECTED),
	Public(Modifier.PUBLIC),
	Static(Modifier.STATIC)
	;

	private final int modifier;

	private TypeAttributes(final int modifier) {
		this.modifier = modifier;
	}

	public int getModifier() {
		return modifier;
	}
}
