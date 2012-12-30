package nextmethod.web.razor.generator;

import nextmethod.base.Delegates;
import nextmethod.base.Strings;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static nextmethod.base.TypeHelpers.typeIs;

public class ResolveUrlCodeGenerator extends SpanCodeGenerator {

	@Override
	public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
		// Check if the host supports it
		if (Strings.isNullOrEmpty(context.getHost().getGeneratedClassContext().getResolveUrlMethodName())) {
			// Nope, just use the default MarkupCodeGenerator behavior
			new MarkupCodeGenerator().generateCode(target, context);
			return;
		}

		if (!context.getHost().isDesignTimeMode() && Strings.isNullOrEmpty(target.getContent())) {
			return;
		}

		if (context.getHost().enableInstrumentation() && context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
			// Add a non-literal context call (non-literal because the expanded URL will not match the source character-by-character)
			context.addContextCall(target, context.getHost().getGeneratedClassContext().getBeginContextMethodName(), false);
		}

		if (!Strings.isNullOrEmpty(target.getContent()) && !context.getHost().isDesignTimeMode()) {
			final String code = context.buildCodeString(new Delegates.IAction1<CodeWriter>() {
				@Override
				public void invoke(@Nullable final CodeWriter input) {
					assert input != null;

					if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
						if (!Strings.isNullOrEmpty(context.getTargetWriterName())) {
							input.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteLiteralMethodName());
							input.writeSnippet(context.getTargetWriterName());
							input.writeParameterSeparator();
						}
						else {
							input.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteLiteralMethodName());
						}
					}
					input.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getResolveUrlMethodName());
					input.writeStartMethodInvoke(target.getContent());
					input.writeEndMethodInvoke();

					if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
						input.writeEndMethodInvoke();
						input.writeEndStatement();
					}
					else {
						input.writeLineContinuation();
					}
				}
			});

			if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
				context.addStatement(code);
			}
			else {
				context.bufferStatementFragment(code);
			}
		}

		if (context.getHost().enableInstrumentation() && context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
			context.addContextCall(target, context.getHost().getGeneratedClassContext().getEndContextMethodName(), false);
		}
	}

	@Override
	public String toString() {
		return "VirtualPath";
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object obj) {
		return typeIs(obj, ResolveUrlCodeGenerator.class);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
