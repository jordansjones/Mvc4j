package nextmethod.codedom;

import java.io.Serializable;

// TODO
public class CodePackage extends CodeObject implements Serializable {

	private static final long serialVersionUID = 7922125490921546204L;

	private CodePackageImportCollection imports;
	private CodeTypeDeclarationCollection classes;
	private String name;

	public CodePackage() {
	}

	public CodePackage(final String name) {
		this.name = name;
	}

	public CodePackageImportCollection getImports() {
		if (imports == null) {
			imports = new CodePackageImportCollection();
			// PopulateImports
		}
		return imports;
	}

	public CodeTypeDeclarationCollection getTypes() {
		if (classes == null) {
			classes = new CodeTypeDeclarationCollection();
			// PopulateTypes
		}
		return classes;
	}

	public String getName() {
		return name == null ? "" : name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
