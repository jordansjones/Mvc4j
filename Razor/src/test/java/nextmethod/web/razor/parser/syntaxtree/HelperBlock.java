package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.generator.BlockCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;

import java.util.Arrays;
import java.util.Collection;

public class HelperBlock extends Block {

	private static final BlockType blockType = BlockType.Helper;

	public HelperBlock(final IBlockCodeGenerator codeGenerator, final Collection<SyntaxTreeNode> children) {
		super(blockType, children, codeGenerator);
	}

	public HelperBlock(final IBlockCodeGenerator codeGenerator, final SyntaxTreeNode... nodes) {
		this(codeGenerator, Arrays.asList(nodes));
	}

	public HelperBlock(final SyntaxTreeNode... nodes) {
		this(BlockCodeGenerator.Null, nodes);
	}

	public HelperBlock(final Collection<SyntaxTreeNode> children) {
		this(BlockCodeGenerator.Null, children);
	}

}