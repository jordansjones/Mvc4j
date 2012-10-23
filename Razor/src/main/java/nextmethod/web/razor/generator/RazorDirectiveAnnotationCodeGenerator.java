package nextmethod.web.razor.generator;

import com.google.common.base.Strings;
import nextmethod.base.KeyValue;
import nextmethod.codedom.CodeAnnotationArgument;
import nextmethod.codedom.CodeAnnotationDeclaration;
import nextmethod.codedom.CodePrimitiveExpression;
import nextmethod.codedom.CodeTypeReference;
import nextmethod.web.razor.RazorDirectiveAnnotation;
import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.web.razor.common.Mvc4jCommonResources.CommonResources;

public class RazorDirectiveAnnotationCodeGenerator extends SpanCodeGenerator {

	private final String name;
	private final String value;

	public RazorDirectiveAnnotationCodeGenerator(@Nonnull final String name, final String value) {
		checkArgument(!Strings.isNullOrEmpty(name), CommonResources().argumentCannotBeNullOrEmpty(), "name");

		this.name = name;
		this.value = Strings.nullToEmpty(value); // Coerce to empty string if it was null
	}

	@Override
	public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
		final CodeTypeReference attributeType = new CodeTypeReference(RazorDirectiveAnnotation.class);
		final CodeAnnotationDeclaration attributeDeclaration = new CodeAnnotationDeclaration(
			attributeType,
			new CodeAnnotationArgument(new CodePrimitiveExpression(name)),
			new CodeAnnotationArgument(new CodePrimitiveExpression(value))
		);
		context.getGeneratedClass().getCustomAttributes().add(attributeDeclaration);
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Directive: " + name + ", Value: " + value;
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object obj) {
		final RazorDirectiveAnnotationCodeGenerator other = typeAs(obj, RazorDirectiveAnnotationCodeGenerator.class);

		return other != null
			&& name.equalsIgnoreCase(other.name)
			&& value.equalsIgnoreCase(other.value);
	}

	@Override
	public int hashCode() {
		return KeyValue.of(name.toUpperCase(), value.toUpperCase()).hashCode();
	}
}
