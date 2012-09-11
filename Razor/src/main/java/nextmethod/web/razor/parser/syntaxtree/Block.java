package nextmethod.web.razor.parser.syntaxtree;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import nextmethod.web.razor.generator.IBlockCodeGenerator;
import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.TextChange;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class Block extends SyntaxTreeNode {

	private final BlockType type;
	private final Collection<SyntaxTreeNode> children;
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

	Block(@Nonnull final BlockType type, @Nonnull final Collection<SyntaxTreeNode> contents, @Nullable final IBlockCodeGenerator generator) {
		this.type = type;
		this.name = type.name();
		this.children = contents;
		this.codeGenerator = generator;
	}

	public BlockType getType() {
		return type;
	}

	public Collection<SyntaxTreeNode> getChildren() {
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

	public Span findFirstDescendentSpan() {
		SyntaxTreeNode current = this;
		while(current != null && current.isBlock()) {
			current = Iterables.getFirst(((Block) current).children, null);
		}

		return typeAs(current, Span.class);
	}

	public Span findLastDescendentSpan() {
		SyntaxTreeNode current = this;
		while(current != null && current.isBlock()) {
			current = Iterables.getLast(((Block) current).children, null);
		}
		return typeAs(current, Span.class);
	}

	public Iterable<Span> flatten() {
		final List<Span> values = Lists.newArrayList();

		// Create an enumerable that flattens the tree for use by syntax highlighters, etc.
		for (SyntaxTreeNode element : children) {
			final Span span = typeAs(element, Span.class);
			if (span != null) {
				values.add(span);
			}
			else {
				final Block block = typeAs(element, Block.class);
				if (block != null) {
					for (Span childSpan : block.flatten()) {
						values.add(childSpan);
					}
				}
			}
		}
		return values;
	}

	public Span locateOwner(final TextChange change) {
		// Ask each child recursively
		Span owner = null;
		for (SyntaxTreeNode element : children) {
			final Span span = typeAs(element, Span.class);
			if (span == null) {
				owner = ((Block) element).locateOwner(change);
			}
			else {
				if (change.getOldPosition() < span.getStart().getAbsoluteIndex()) {
					// Early escape for cases when changes overlap multiple spans...
					break;
				}
				owner = span.getEditHandler().ownsChange(span, change) ? span : owner;
			}

			if (owner != null) {
				break;
			}
		}
		return owner;
	}

	@Override
	public boolean equivalentTo(@Nonnull final SyntaxTreeNode node) {
		final Block other = typeAs(node, Block.class);
		if (other == null || other.getType() != getType()) {
			return false;
		}
		return childrenEqual(getChildren(), other.getChildren());
	}

	@Override
	public String toString() {
		return String.format("%s Block at %s::%d (Gen:%s)", this.type, getStart(), getLength(), this.codeGenerator);
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object obj) {
		final Block other = typeAs(obj, Block.class);
		return other != null
			&& getType() == other.getType()
			&& Objects.equal(getCodeGenerator(), other.getCodeGenerator())
			&& childrenEqual(getChildren(), other.getChildren());
	}

	@Override
	public int hashCode() {
		return getType().hashCode();
	}

	private static boolean childrenEqual(final Iterable<SyntaxTreeNode> left, final Iterable<SyntaxTreeNode> right) {
		return Iterables.elementsEqual(left, right);
	}
}
