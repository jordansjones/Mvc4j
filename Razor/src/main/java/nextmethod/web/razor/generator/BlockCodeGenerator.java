package nextmethod.web.razor.generator;

import nextmethod.web.razor.parser.syntaxtree.Block;

import javax.annotation.Nonnull;

import static nextmethod.base.TypeHelpers.typeAs;

/**
 *
 */
public abstract class BlockCodeGenerator implements IBlockCodeGenerator {

	public static final IBlockCodeGenerator Null = new NullBlockCodeGenerator();

	@Override
	public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
	}

	@Override
	public void generateEndBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object obj) {
		return typeAs(obj, IBlockCodeGenerator.class) != null;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	private static class NullBlockCodeGenerator implements IBlockCodeGenerator {

		@Override
		public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
		}

		@Override
		public void generateEndBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
		}

		@Override
		public String toString() {
			return "None";
		}
	}
}
