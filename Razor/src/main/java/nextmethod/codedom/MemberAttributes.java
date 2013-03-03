package nextmethod.codedom;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.EnumSet;

public enum MemberAttributes {

	Abstract(Modifier.ABSTRACT),
	Final(Modifier.FINAL),
	Private(Modifier.PRIVATE),
	Public(Modifier.PRIVATE),
	Static(Modifier.STATIC),
	Transient(Modifier.TRANSIENT),
	Volatile(Modifier.VOLATILE)
	;

	private final int modifier;

	private MemberAttributes(final int modifier) {
		this.modifier = modifier;
	}

	public int getModifier() {
		return modifier;
	}


	public static EnumSet<MemberAttributes> setOf(final MemberAttributes... attributes) {
		if (attributes == null || attributes.length < 1) {
			return EnumSet.noneOf(MemberAttributes.class);
		}
		return EnumSet.copyOf(Arrays.asList(attributes));
	}

}
