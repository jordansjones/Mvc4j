package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;

/**
 *
 */
public abstract class SyntaxTreeNode {

	protected Block parent;

	public Block getParent() {
		return this.parent;
	}

	protected void setParent(final Block parent) {
		this.parent = parent;
	}

	/**
	 * Returns true if this element is a block (to avoid casting)
	 */
	public abstract boolean isBlock();

	/**
	 * The length of all the content contained in this node
	 */
	public abstract int getLength();

	/**
	 * The start point of this node
	 */
	public abstract SourceLocation getStart();

	/**
	 * Accepts a parser visitor, calling the appropriate visit method and passing in this instance
	 * @param visitor The visitor to accept
	 */
	public abstract void accept(@Nonnull final ParserVisitor visitor);

	/**
	 * Determines if the specified node is equivalent to this node
	 * @param node The node to compare this node with
	 * @return true if the provided node has all the same content and metadata, though the specific quantity and type of symbols may be different.
	 */
	public abstract boolean equivalentTo(@Nonnull final SyntaxTreeNode node);

}
