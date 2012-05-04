package nextmethod.web.razor.generator;

import nextmethod.web.razor.parser.syntaxtree.Block;

/**
 *
 */
public interface IBlockCodeGenerator {

	void generateStartBlockCode(final Block target, final CodeGeneratorContext context);
	void generateEndBlockCode(final Block target, final CodeGeneratorContext context);
}
