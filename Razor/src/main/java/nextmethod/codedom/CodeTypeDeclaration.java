package nextmethod.codedom;

import nextmethod.base.NotImplementedException;

import java.io.Serializable;

// TODO
public class CodeTypeDeclaration extends CodeTypeMember implements Serializable {

	private static final long serialVersionUID = 16283811121931559L;

	private CodeTypeReferenceCollection baseTypes;
	private CodeTypeMemberCollection members;

	private int attributes = TypeAttributes.Public.getModifier();

	private boolean isEnum;

	public CodeTypeDeclaration() {
	}

	public CodeTypeDeclaration(final String name) {
		this.setName(name);
	}


	public boolean isClass() {
		if ((attributes & TypeAttributes.Interface.getModifier()) != 0) return false;
		if (isEnum) return false;
		return true;
	}

	public void setIsClass(final boolean isClass) {
		if (isClass) {
			attributes &= ~TypeAttributes.Interface.getModifier();
			isEnum = false;
		}
	}


	public boolean isEnum() {
		return isEnum;
	}

	public void setIsEnum(final boolean isEnum) {
		if (isEnum) {
			this.isEnum = true;
		}
	}


	public boolean isInterface() {
		throw new NotImplementedException();
	}

	public void setIsInterface(final boolean isInterface) {
		throw new NotImplementedException();
	}


	public CodeTypeReferenceCollection getBaseTypes() {
		if (baseTypes == null) {
			baseTypes = new CodeTypeReferenceCollection();
			// PopulateBaseTypes
		}
		return baseTypes;
	}

	public CodeTypeMemberCollection getMembers() {
		if (members == null) {
			members = new CodeTypeMemberCollection();
			// PopulateMembers
		}
		return members;
	}
}
