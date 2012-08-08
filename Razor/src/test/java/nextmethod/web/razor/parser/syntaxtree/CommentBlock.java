package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.generator.IBlockCodeGenerator;
import nextmethod.web.razor.generator.RazorCommentCodeGenerator;

import java.util.Arrays;
import java.util.Collection;

public class CommentBlock extends Block {

	private static final BlockType blockType = BlockType.Comment;

	public CommentBlock(final IBlockCodeGenerator codeGenerator, final Collection<SyntaxTreeNode> children) {
		super(blockType, children, codeGenerator);
	}

	public CommentBlock(final IBlockCodeGenerator codeGenerator, final SyntaxTreeNode... nodes) {
		this(codeGenerator, Arrays.asList(nodes));
	}

	public CommentBlock(final SyntaxTreeNode... nodes) {
		this(new RazorCommentCodeGenerator(), nodes);
	}

	public CommentBlock(final Collection<SyntaxTreeNode> children) {
		this(new RazorCommentCodeGenerator(), children);
	}
}
