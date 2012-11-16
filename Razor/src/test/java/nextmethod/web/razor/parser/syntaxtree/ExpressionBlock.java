package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.generator.ExpressionCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;

import java.util.Arrays;
import java.util.Collection;

public class ExpressionBlock extends Block {

	private static final BlockType blockType = BlockType.Expression;

	public ExpressionBlock(final IBlockCodeGenerator codeGenerator, final Collection<SyntaxTreeNode> children) {
		super(blockType, BlockExtensions.buildSpanConstructors(children), codeGenerator);
	}

	public ExpressionBlock(final IBlockCodeGenerator codeGenerator, final SyntaxTreeNode... nodes) {
		this(codeGenerator, Arrays.asList(nodes));
	}

	public ExpressionBlock(final SyntaxTreeNode... nodes) {
		this(new ExpressionCodeGenerator(), nodes);
	}

	public ExpressionBlock(final Collection<SyntaxTreeNode> children) {
		this(new ExpressionCodeGenerator(), children);
	}

}
