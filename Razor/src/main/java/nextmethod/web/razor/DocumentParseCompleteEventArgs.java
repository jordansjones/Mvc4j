package nextmethod.web.razor;

import nextmethod.base.EventArgs;
import nextmethod.web.razor.text.TextChange;

/**
 * Arguments for the DocumentParseComplete event in RazorEditorParser
 */
public class DocumentParseCompleteEventArgs extends EventArgs {

	private boolean treeStructureChanged;
	private GeneratorResults generatorResults;
	private TextChange sourceChange;

	public DocumentParseCompleteEventArgs() {
	}

	public DocumentParseCompleteEventArgs(final boolean treeStructureChanged, final GeneratorResults generatorResults, final TextChange sourceChange) {
		this.treeStructureChanged = treeStructureChanged;
		this.generatorResults = generatorResults;
		this.sourceChange = sourceChange;
	}

	/**
	 * Indicates if the tree structure has actually changed since the previous reparse.
	 * @return true if the tree structure has changed
	 */
	public boolean isTreeStructureChanged() {
		return treeStructureChanged;
	}

	/**
	 * The results of the code generation and parsing
	 * @return code generation/parsing results
	 */
	public GeneratorResults getGeneratorResults() {
		return generatorResults;
	}

	/**
	 * The TextChange which triggered the reparse
	 * @return
	 */
	public TextChange getSourceChange() {
		return sourceChange;
	}
}
