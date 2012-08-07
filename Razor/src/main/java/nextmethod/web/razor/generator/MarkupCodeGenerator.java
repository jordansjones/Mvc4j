package nextmethod.web.razor.generator;

import com.google.common.base.Strings;
import nextmethod.base.IVoidAction;
import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;

public class MarkupCodeGenerator extends SpanCodeGenerator {

	@Override
	public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
		if (!context.getHost().isDesignTimeMode() && Strings.isNullOrEmpty(target.getContent())) {
			return;
		}

		if (context.getHost().enableInstrumentation()) {
			context.addContextCall(target, context.getHost().getGeneratedClassContext().getBeginContextMethodName(), true);
		}

		if (!Strings.isNullOrEmpty(target.getContent()) && !context.getHost().isDesignTimeMode()) {
			final String code = context.buildCodeString(new IVoidAction<CodeWriter>() {
				@Override
				public void invoke(final CodeWriter input) {
					if (!Strings.isNullOrEmpty(context.getTargetWriterName())) {
						input.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteLiteralToMethodName());
						input.writeSnippet(context.getTargetWriterName());
						input.writeParameterSeparator();
					}
					else {
						input.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteLiteralMethodName());
					}
					input.writeStringLiteral(target.getContent());
					input.writeEndMethodInvoke();
					input.writeEndStatement();
				}
			});
			context.addStatement(code);
		}

		if (context.getHost().enableInstrumentation()) {
			context.addContextCall(target, context.getHost().getGeneratedClassContext().getEndContextMethodName(), true);
		}
	}

	@Override
	public String toString() {
		return "Markup";
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof MarkupCodeGenerator;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
