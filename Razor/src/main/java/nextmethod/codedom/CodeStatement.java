package nextmethod.codedom;

import java.io.Serializable;

// TODO
public class CodeStatement extends CodeObject implements Serializable {

	private static final long serialVersionUID = -5647665217729816196L;

	private CodeLinePragma linePragma;

	public CodeLinePragma getLinePragma() {
		return linePragma;
	}

	public void setLinePragma(final CodeLinePragma linePragma) {
		this.linePragma = linePragma;
	}
}
