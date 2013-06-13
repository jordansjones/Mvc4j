package nextmethod.codedom;

import java.io.Serializable;
import java.lang.reflect.Modifier;

public class MemberAttributes implements Serializable {

	private static final long serialVersionUID = 3039744525780381022L;

	public static final MemberAttributes Abstract = new MemberAttributes(Modifier.ABSTRACT);
	public static final MemberAttributes Final = new MemberAttributes(Modifier.FINAL);
	public static final MemberAttributes Private = new MemberAttributes(Modifier.PRIVATE);
	public static final MemberAttributes Protected = new MemberAttributes(Modifier.PROTECTED);
	public static final MemberAttributes Public = new MemberAttributes(Modifier.PUBLIC);
	public static final MemberAttributes Static = new MemberAttributes(Modifier.STATIC);
	public static final MemberAttributes Transient = new MemberAttributes(Modifier.TRANSIENT);
	public static final MemberAttributes Volatile = new MemberAttributes(Modifier.VOLATILE);

	public static final MemberAttributes ScopeMask = new MemberAttributes(Modifier.constructorModifiers() | Modifier.fieldModifiers() | Modifier.methodModifiers());
	public static final MemberAttributes AccessMask = new MemberAttributes(Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC);

	public static MemberAttributes valueOf(final int val) {
		return new MemberAttributes(val);
	}

	public final int val;

	private MemberAttributes(final int modifier) {
		this.val = modifier;
	}

}
