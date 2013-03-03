package nextmethod.web.razor.parser.syntaxtree;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import nextmethod.web.razor.generator.BlockCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class BlockBuilder {

	private BlockType type;
	private String name;
	private List<SyntaxTreeNode> children;
	private IBlockCodeGenerator codeGenerator;

	public BlockBuilder() {
		reset();
	}

	public BlockBuilder(@Nonnull final Block original) {
		this.type = original.getType();
		this.children = Lists.newArrayList(original.getChildren());
		this.name = original.getName();
		this.codeGenerator = original.getCodeGenerator();
	}

	public Block build() {
		return new Block(this);
	}

	public void reset() {
		this.type = null;
		this.name = null;
		this.children = Lists.newArrayList();
		this.codeGenerator = BlockCodeGenerator.Null;
	}

	public Optional<BlockType> getType() {
		return type != null ? Optional.of(type) : Optional.<BlockType>absent();
	}

	public BlockBuilder setType(@Nullable final BlockType type) {
		this.type = type;
		return this;
	}

	public String getName() {
		return name;
	}

	public BlockBuilder setName(@Nullable final String name) {
		this.name = name;
		return this;
	}

	public Collection<SyntaxTreeNode> getChildren() {
		return children;
	}

	public IBlockCodeGenerator getCodeGenerator() {
		return codeGenerator;
	}

	public BlockBuilder setCodeGenerator(@Nullable final IBlockCodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
		return this;
	}

}
