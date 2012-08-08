package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.generator.BlockCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;

import java.util.Arrays;
import java.util.Collection;

public class MarkupBlock extends Block {

	private static final BlockType blockType = BlockType.Markup;

	public MarkupBlock(final IBlockCodeGenerator codeGenerator, final Collection<SyntaxTreeNode> children) {
		super(blockType, children, codeGenerator);
	}

	public MarkupBlock(final IBlockCodeGenerator codeGenerator, final SyntaxTreeNode... nodes) {
		this(codeGenerator, Arrays.asList(nodes));
	}

	public MarkupBlock(final SyntaxTreeNode... nodes) {
		this(BlockCodeGenerator.Null, nodes);
	}

	public MarkupBlock(final Collection<SyntaxTreeNode> children) {
		this(BlockCodeGenerator.Null, children);
	}

}
