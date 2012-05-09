package nextmethod.web.razor.generator;

import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;

public interface ISpanCodeGenerator {

	void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context);
}
