package nextmethod.web.razor.generator;

import nextmethod.base.Delegates;
import nextmethod.base.OutParam;
import nextmethod.base.Strings;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.SyntaxConstants;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;

import javax.annotation.Nonnull;

import static nextmethod.base.TypeHelpers.typeIs;

public class StatementCodeGenerator extends SpanCodeGenerator {

	@Override
	public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
		context.flushBufferedStatement();
		String generatedCode = context.buildCodeString(input -> input.writeSnippet(target.getContent()));
		final OutParam<Integer> startGeneratedCode = OutParam.of(target.getStart().getCharacterIndex());
		final OutParam<Integer> paddingCharCount = OutParam.of();
		generatedCode = CodeGeneratorPaddingHelper.padStatement(context.getHost(), generatedCode, target, startGeneratedCode, paddingCharCount);

		context.addStatement(generatedCode, context.generateLinePragma(target, paddingCharCount.value()));
	}

	@Override
	public String toString() {
		return "Stmt";
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object obj) {
		return (obj != null) && typeIs(obj, StatementCodeGenerator.class);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
