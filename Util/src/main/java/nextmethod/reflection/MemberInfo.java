package nextmethod.reflection;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 *
 */
abstract class MemberInfo<T extends Member> extends WrappedInfo<T> {

	protected final int modifiers;

	private final String name;
	private final boolean isAbstract;
	private final boolean isFinal;
	private final boolean isPrivate;
	private final boolean isProtected;
	private final boolean isPublic;
	private final boolean isStatic;

	public MemberInfo(final T member) {
		super(member);
		this.modifiers = member.getModifiers();

		this.name = member.getName();
		this.isAbstract = Modifier.isAbstract(this.modifiers);
		this.isFinal = Modifier.isFinal(this.modifiers);
		this.isPrivate = Modifier.isPrivate(this.modifiers);
		this.isProtected = Modifier.isProtected(this.modifiers);
		this.isPublic = Modifier.isPublic(this.modifiers);
		this.isStatic = Modifier.isStatic(this.modifiers);
	}

	public String getName() {
		return this.name;
	}

	public ClassInfo<?> getReflectedType() {
		return TypeOfHelper.typeOf(this.wrapped.getDeclaringClass());
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public boolean isProtected() {
		return isProtected;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isStatic() {
		return isStatic;
	}

}
