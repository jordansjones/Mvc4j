package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.generator.BlockCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;

import java.util.Arrays;
import java.util.Collection;

public class SectionBlock extends Block {

	private static final BlockType blockType = BlockType.Section;

	public SectionBlock(final IBlockCodeGenerator codeGenerator, final Collection<SyntaxTreeNode> children) {
		super(blockType, BlockExtensions.buildSpanConstructors(children), codeGenerator);
	}

	public SectionBlock(final IBlockCodeGenerator codeGenerator, final SyntaxTreeNode... nodes) {
		this(codeGenerator, Arrays.asList(nodes));
	}

	public SectionBlock(final SyntaxTreeNode... nodes) {
		this(BlockCodeGenerator.Null, nodes);
	}

	public SectionBlock(final Collection<SyntaxTreeNode> children) {
		this(BlockCodeGenerator.Null, children);
	}

}
