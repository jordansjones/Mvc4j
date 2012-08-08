package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.generator.BlockCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;

import java.util.Arrays;
import java.util.Collection;

public class StatementBlock extends Block {

	private static final BlockType blockType = BlockType.Statement;

	public StatementBlock(final IBlockCodeGenerator codeGenerator, final Collection<SyntaxTreeNode> children) {
		super(blockType, children, codeGenerator);
	}

	public StatementBlock(final IBlockCodeGenerator codeGenerator, final SyntaxTreeNode... nodes) {
		this(codeGenerator, Arrays.asList(nodes));
	}

	public StatementBlock(final SyntaxTreeNode... nodes) {
		this(BlockCodeGenerator.Null, nodes);
	}

	public StatementBlock(final Collection<SyntaxTreeNode> children) {
		this(BlockCodeGenerator.Null, children);
	}

}
