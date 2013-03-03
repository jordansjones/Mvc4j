package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.generator.IBlockCodeGenerator;
import nextmethod.web.razor.generator.TemplateBlockCodeGenerator;

import java.util.Arrays;
import java.util.Collection;

public class TemplateBlock extends Block {

	private static final BlockType blockType = BlockType.Template;

	public TemplateBlock(final IBlockCodeGenerator codeGenerator, final Collection<SyntaxTreeNode> children) {
		super(blockType, BlockExtensions.buildSpanConstructors(children), codeGenerator);
	}

	public TemplateBlock(final IBlockCodeGenerator codeGenerator, final SyntaxTreeNode... nodes) {
		this(codeGenerator, Arrays.asList(nodes));
	}

	public TemplateBlock(final SyntaxTreeNode... nodes) {
		this(new TemplateBlockCodeGenerator(), nodes);
	}

	public TemplateBlock(final Collection<SyntaxTreeNode> children) {
		this(new TemplateBlockCodeGenerator(), children);
	}

}
