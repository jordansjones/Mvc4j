package nextmethod.web.razor.parser.syntaxtree;

import java.util.Collections;

/**
 *
 */
public class IgnoreOutputBlock extends Block {

	public IgnoreOutputBlock() {
		super(BlockType.Template, Collections.<SyntaxTreeNode>emptyList(), null);
	}
}
