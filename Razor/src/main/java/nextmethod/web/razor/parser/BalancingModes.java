package nextmethod.web.razor.parser;

import java.util.EnumSet;

public enum BalancingModes {

	None,
	BacktrackOnFailure,
	NoErrorOnFailure,
	AllowCommentsAndTemplates,
	AllowEmbeddedTransitions,;

	public static final EnumSet<BalancingModes> Any = EnumSet.allOf(BalancingModes.class);
	public static final EnumSet<BalancingModes> NotAny = EnumSet.noneOf(BalancingModes.class);
	public static final EnumSet<BalancingModes> SetOfNone = EnumSet.of(None);

}
