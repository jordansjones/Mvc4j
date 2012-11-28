package nextmethod.codedom;

import nextmethod.base.NotImplementedException;

import java.io.Serializable;

// TODO
public class CodeTypeDeclaration extends CodeTypeMember implements Serializable {

	private static final long serialVersionUID = 16283811121931559L;

	private CodeTypeReferenceCollection baseTypes;
	private CodeTypeMemberCollection members;

	public CodeTypeDeclaration() {
	}

	public CodeTypeDeclaration(final String name) {
		this.setName(name);
	}


	public boolean isClass() {
		throw new NotImplementedException();
	}

	public void setIsClass(final boolean isClass) {
		throw new NotImplementedException();
	}


	public boolean isEnum() {
		throw new NotImplementedException();
	}

	public void setIsEnum(final boolean isEnum) {
		throw new NotImplementedException();
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
