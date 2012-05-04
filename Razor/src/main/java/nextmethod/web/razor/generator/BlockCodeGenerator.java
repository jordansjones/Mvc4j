package nextmethod.web.razor.generator;

import nextmethod.web.razor.parser.syntaxtree.Block;

/**
 *
 */
public abstract class BlockCodeGenerator implements IBlockCodeGenerator {

	public static final IBlockCodeGenerator Null = new NullBlockCodeGenerator();


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
