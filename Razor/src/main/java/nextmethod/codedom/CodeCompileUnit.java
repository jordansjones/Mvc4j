package nextmethod.codedom;

import java.io.Serializable;

// TODO
public class CodeCompileUnit extends CodeObject implements Serializable {

	private static final long serialVersionUID = 6591474768063604478L;

	private CodePackageCollection packages;


	public CodePackageCollection getPackages() {
		if (packages == null) {
			packages = new CodePackageCollection();
		}
		return packages;
	}
}
