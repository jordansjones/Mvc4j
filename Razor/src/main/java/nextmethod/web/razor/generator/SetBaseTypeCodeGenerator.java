package nextmethod.web.razor.generator;

import nextmethod.base.Delegates;
import nextmethod.codedom.CodeSnippetStatement;
import nextmethod.codedom.CodeTypeReference;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static nextmethod.base.TypeHelpers.typeAs;

public class SetBaseTypeCodeGenerator extends SpanCodeGenerator {

	private String baseType;

	public SetBaseTypeCodeGenerator(@Nonnull final String baseType) {
		this.baseType = baseType;
	}

	@Override
	public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
		context.getGeneratedClass().getBaseTypes().clear();
		context.getGeneratedClass().getBaseTypes().add(new CodeTypeReference(resolveType(context, baseType.trim())));

		if (context.getHost().isDesignTimeMode()) {
			final AtomicInteger generatedCodeStart = new AtomicInteger(0);
			final String code = context.buildCodeString(new Delegates.IAction1<CodeWriter>() {
				@Override
				public void invoke(@Nullable final CodeWriter input) {
					assert input != null;

					generatedCodeStart.set(input.writeVariableDeclaration(target.getContent(), "__inheritsHelper", null));
					input.writeEndStatement();
				}
			});

			int padding = calculatePadding(target, generatedCodeStart.get());
			final CodeSnippetStatement statement = new CodeSnippetStatement(pad(code, target, generatedCodeStart.get()));
			statement.setLinePragma(context.generateLinePragma(target, generatedCodeStart.get() + padding));
			context.addDesignTypeHelperStatement(statement);
		}
	}

	protected String resolveType(@Nonnull final CodeGeneratorContext context, @Nonnull final String baseType) {
		return baseType;
	}

	public String getBaseType() {
		return baseType;
	}

	public void setBaseType(String baseType) {
		this.baseType = baseType;
	}

	@Override
	public String toString() {
		return "Base:" + baseType;
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object obj) {
		final SetBaseTypeCodeGenerator o = typeAs(obj, SetBaseTypeCodeGenerator.class);
		return o != null && Objects.equals(baseType, o.baseType);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
