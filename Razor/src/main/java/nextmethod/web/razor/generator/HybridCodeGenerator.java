package nextmethod.web.razor.generator;

import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;

public abstract class HybridCodeGenerator extends CodeGeneratorBase implements ISpanCodeGenerator, IBlockCodeGenerator {

	public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {}

	public void generateEndBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {}

	public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {}

}
