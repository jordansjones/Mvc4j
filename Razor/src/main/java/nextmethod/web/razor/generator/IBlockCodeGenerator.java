package nextmethod.web.razor.generator;

import nextmethod.web.razor.parser.syntaxtree.Block;

import javax.annotation.Nonnull;

/**
 *
 */
public interface IBlockCodeGenerator {

	void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context);
	void generateEndBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context);
}
