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

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final CodePackageImport that = (CodePackageImport) o;

		if (packageName != null ? !packageName.equals(that.packageName) : that.packageName != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return packageName != null ? packageName.hashCode() : 0;
	}
}
