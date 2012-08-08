package nextmethod.web.razor.generator;

import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;

import static nextmethod.base.TypeHelpers.typeIs;

public abstract class SpanCodeGenerator extends CodeGeneratorBase implements ISpanCodeGenerator {

	public static final ISpanCodeGenerator Null = new NullSpanCodeGenerator();

	@Override
	public void generateCode(@Nonnull Span target, @Nonnull CodeGeneratorContext context) {
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object obj) {
		return typeIs(obj, ISpanCodeGenerator.class);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
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
