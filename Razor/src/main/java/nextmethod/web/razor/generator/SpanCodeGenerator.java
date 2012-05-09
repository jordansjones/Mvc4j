package nextmethod.web.razor.generator;

import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;

public abstract class SpanCodeGenerator extends CodeGeneratorBase implements ISpanCodeGenerator {

	public static final ISpanCodeGenerator Null = new NullSpanCodeGenerator();

	@Override
	public void generateCode(@Nonnull Span target, @Nonnull CodeGeneratorContext context) {
	}

	private static class NullSpanCodeGenerator implements ISpanCodeGenerator {

		@Override
		public void generateCode(@Nonnull Span target, @Nonnull CodeGeneratorContext context) {
		}

		public String toString() {
			return "None";
		}

	}
}
