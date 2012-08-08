package nextmethod.codedom;

import nextmethod.annotations.TODO;

import java.io.Serializable;

@TODO
public class CodeTypeDeclaration extends CodeTypeMember implements Serializable {

	private static final long serialVersionUID = 16283811121931559L;

	private CodeTypeReferenceCollection baseTypes;
	private CodeTypeMemberCollection members;
	private boolean isEnum;

	public CodeTypeDeclaration() {
	}

	public CodeTypeDeclaration(final String name) {
		this.setName(name);
	}


	public boolean isClass() {
		return true;
	}

	public void setIsClass(final boolean isClass) {

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
