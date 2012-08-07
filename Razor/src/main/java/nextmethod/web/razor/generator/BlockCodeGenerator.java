package nextmethod.web.razor.generator;

import nextmethod.web.razor.parser.syntaxtree.Block;

import javax.annotation.Nonnull;

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

	private static class NullBlockCodeGenerator implements IBlockCodeGenerator {

		@Override
		public void generateStartBlockCode(final Block target, final CodeGeneratorContext context) {
		}

		@Override
		public void generateEndBlockCode(final Block target, final CodeGeneratorContext context) {
		}

		@Override
		public String toString() {
			return "None";
		}
	}
}
