package nextmethod.codedom;

import nextmethod.annotations.TODO;

@TODO
public class CodeMemberMethod extends CodeTypeMember {

	private CodeStatementCollection statements;

	public CodeStatementCollection getStatements() {
		if (statements == null) {
			statements = new CodeStatementCollection();
			// PopulateStatements
		}
		return statements;
	}
}
