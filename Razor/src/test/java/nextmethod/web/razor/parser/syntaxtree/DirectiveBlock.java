package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.generator.BlockCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;

import java.util.Arrays;

public class DirectiveBlock extends Block {

	private static final BlockType blockType = BlockType.Directive;

	public DirectiveBlock(final IBlockCodeGenerator codeGenerator, final Iterable<SyntaxTreeNode> children) {
		super(blockType, children, codeGenerator);
	}

	public DirectiveBlock(final IBlockCodeGenerator codeGenerator, final SyntaxTreeNode... nodes) {
		this(codeGenerator, Arrays.asList(nodes));
	}

	public DirectiveBlock(final SyntaxTreeNode... nodes) {
		this(BlockCodeGenerator.Null, nodes);
	}

	public DirectiveBlock(final Iterable<SyntaxTreeNode> children) {
		this(BlockCodeGenerator.Null, children);
	}

}
