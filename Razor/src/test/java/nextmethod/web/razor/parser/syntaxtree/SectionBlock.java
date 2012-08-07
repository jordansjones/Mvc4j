package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.generator.BlockCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;

import java.util.Arrays;

public class SectionBlock extends Block {

	private static final BlockType blockType = BlockType.Section;

	public SectionBlock(final IBlockCodeGenerator codeGenerator, final Iterable<SyntaxTreeNode> children) {
		super(blockType, children, codeGenerator);
	}

	public SectionBlock(final IBlockCodeGenerator codeGenerator, final SyntaxTreeNode... nodes) {
		this(codeGenerator, Arrays.asList(nodes));
	}

	public SectionBlock(final SyntaxTreeNode... nodes) {
		this(BlockCodeGenerator.Null, nodes);
	}

	public SectionBlock(final Iterable<SyntaxTreeNode> children) {
		this(BlockCodeGenerator.Null, children);
	}

}
