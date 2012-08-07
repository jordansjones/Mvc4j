package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.generator.ExpressionCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;

import java.util.Arrays;

public class ExpressionBlock extends Block {

	private static final BlockType blockType = BlockType.Expression;

	public ExpressionBlock(final IBlockCodeGenerator codeGenerator, final Iterable<SyntaxTreeNode> children) {
		super(blockType, children, codeGenerator);
	}

	public ExpressionBlock(final IBlockCodeGenerator codeGenerator, final SyntaxTreeNode... nodes) {
		this(codeGenerator, Arrays.asList(nodes));
	}

	public ExpressionBlock(final SyntaxTreeNode... nodes) {
		this(new ExpressionCodeGenerator(), nodes);
	}

	public ExpressionBlock(final Iterable<SyntaxTreeNode> children) {
		this(new ExpressionCodeGenerator(), children);
	}

}
