package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.generator.IBlockCodeGenerator;
import nextmethod.web.razor.generator.TemplateBlockCodeGenerator;

import java.util.Arrays;

public class TemplateBlock extends Block {

	private static final BlockType blockType = BlockType.Template;

	public TemplateBlock(final IBlockCodeGenerator codeGenerator, final Iterable<SyntaxTreeNode> children) {
		super(blockType, children, codeGenerator);
	}

	public TemplateBlock(final IBlockCodeGenerator codeGenerator, final SyntaxTreeNode... nodes) {
		this(codeGenerator, Arrays.asList(nodes));
	}

	public TemplateBlock(final SyntaxTreeNode... nodes) {
		this(new TemplateBlockCodeGenerator(), nodes);
	}

	public TemplateBlock(final Iterable<SyntaxTreeNode> children) {
		this(new TemplateBlockCodeGenerator(), children);
	}

}
