package nextmethod.codedom;

import nextmethod.base.Strings;

import java.io.Serializable;

// TODO
public class CodePackageImport extends CodeObject implements Serializable {

	private static final long serialVersionUID = 5451857111922184818L;

	private CodeLinePragma linePragma;
	private String packageName;

	public CodePackageImport() {
	}

	public CodePackageImport(final String packageName) {
		this.packageName = packageName;
	}

	public CodeLinePragma getLinePragma() {
		return linePragma;
	}

	public void setLinePragma(final CodeLinePragma linePragma) {
		this.linePragma = linePragma;
	}

	public String getPackage() {
		return packageName == null ? Strings.Empty : packageName;
	}

	public void setPackage(final String packageName) {
		this.packageName = packageName;
	}
}
