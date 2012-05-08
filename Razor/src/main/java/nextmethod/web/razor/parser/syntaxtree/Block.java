package nextmethod.web.razor.parser.syntaxtree;

import com.google.common.collect.Iterables;
import nextmethod.web.razor.generator.IBlockCodeGenerator;
import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class Block extends SyntaxTreeNode {

	private final BlockType type;
	private final Iterable<SyntaxTreeNode> children;
	private final IBlockCodeGenerator codeGenerator;
	private final String name;

	public Block(@Nonnull final BlockBuilder source) {
		if (source.getType() == null) {
			throw new IllegalArgumentException(RazorResources().getString("block.type.not.specified"));
		}
		this.type = source.getType().get();
		this.children = source.getChildren();
		this.name = source.getName();
		this.codeGenerator = source.getCodeGenerator();

		source.reset();

		for (SyntaxTreeNode node : children) {
			node.setParent(this);
		}
	}

	Block(@Nonnull final BlockType type, @Nonnull final Iterable<SyntaxTreeNode> contents, @Nonnull final IBlockCodeGenerator generator) {
		this.type = type;
		this.name = type.name();
		this.children = contents;
		this.codeGenerator = generator;
	}

	public BlockType getType() {
		return type;
	}

	public Iterable<SyntaxTreeNode> getChildren() {
		return children;
	}

	public String getName() {
		return name;
	}

	public IBlockCodeGenerator getCodeGenerator() {
		return codeGenerator;
	}

	@Override
	public boolean isBlock() {
		return true;
	}

	@Override
	public int getLength() {
		int size = 0;
		for (SyntaxTreeNode child : children) {
			size += child.getLength();
		}
		return size;
	}

	@Override
	public SourceLocation getStart() {
		final SyntaxTreeNode child = Iterables.getFirst(this.children, null);
		if (child == null)
			return SourceLocation.Zero;

		return child.getStart();
	}

	@Override
	public void accept(@Nonnull final ParserVisitor visitor) {
		visitor.visitBlock(this);
	}

	@Override
	public boolean equivalentTo(@Nonnull final SyntaxTreeNode node) {
		return false;
	}

	@Override
	public String toString() {
		return String.format("%s Block at %s::%d (Gen:%s)", this.type, getStart(), getLength(), this.codeGenerator);
	}
}
