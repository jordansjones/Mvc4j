package nextmethod.web.razor.generator;

import nextmethod.base.Delegates;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.LocationTagged;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static nextmethod.base.TypeHelpers.typeAs;

public class LiteralAttributeCodeGenerator extends SpanCodeGenerator {

	private final LocationTagged<String> prefix;
	private LocationTagged<String> value;
	private LocationTagged<SpanCodeGenerator> valueGenerator;

	private LiteralAttributeCodeGenerator(@Nonnull final LocationTagged<String> prefix, @Nullable final LocationTagged<String> value, @Nullable final LocationTagged<SpanCodeGenerator> valueGenerator) {
		this.prefix = prefix;
		this.value = value;
		this.valueGenerator = valueGenerator;
	}

	@Override
	public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
		final RazorEngineHost host = context.getHost();
		if (host.isDesignTimeMode()) {
			return;
		}

		final ExpressionRenderingMode oldMode = context.getExpressionRenderingMode();
		context.bufferStatementFragment(context.buildCodeString(new Delegates.IAction1<CodeWriter>() {
			@Override
			public void invoke(@Nullable final CodeWriter input) {
				assert input != null;

				input.writeParameterSeparator();
				input.writeStartMethodInvoke("Tuple.Create");
				input.writeLocationTaggedString(prefix);
				input.writeParameterSeparator();
				if (valueGenerator != null) {
					input.writeStartMethodInvoke("Tuple.Create", "java.lang.Object", "java.lang.Integer");
					context.setExpressionRenderingMode(ExpressionRenderingMode.InjectCode);
				}
				else {
					input.writeLocationTaggedString(value);
					input.writeParameterSeparator();
					// This attribute value is a literal value
					input.writeBooleanLiteral(true);
					input.writeEndMethodInvoke();

					input.writeLineContinuation();
				}
			}
		}));

		if (valueGenerator != null) {
			valueGenerator.getValue().generateCode(target, context);
			context.flushBufferedStatement();
			context.setExpressionRenderingMode(oldMode);
			context.addStatement(context.buildCodeString(new Delegates.IAction1<CodeWriter>() {
				@Override
				public void invoke(@Nullable final CodeWriter input) {
					assert input != null;

					input.writeParameterSeparator();
					input.writeSnippet(String.valueOf(valueGenerator.getLocation().getAbsoluteIndex()));
					input.writeEndMethodInvoke();
					input.writeParameterSeparator();
					// This attribute value is not a literal value, it is dynamically generated
					input.writeBooleanLiteral(false);
					input.writeEndMethodInvoke();

					input.writeLineContinuation();
				}
			}));
		}
		else {
			context.flushBufferedStatement();
		}
	}

	public LocationTagged<String> getPrefix() {
		return prefix;
	}

	public LocationTagged<String> getValue() {
		return value;
	}

	public LocationTagged<SpanCodeGenerator> getValueGenerator() {
		return valueGenerator;
	}

	@Override
	public String toString() {
		if (valueGenerator == null) {
			return String.format("LitAttr:%s,%s", prefix, value);
		}
		return String.format("LitAttr:%s,<Sub:%s>", prefix, valueGenerator);
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object obj) {
		final LiteralAttributeCodeGenerator other = typeAs(obj, LiteralAttributeCodeGenerator.class);
		return other != null
			&& prefix.equals(other.prefix)
			&& Objects.equals(value, other.value)
			&& Objects.equals(valueGenerator, other.valueGenerator);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			prefix,
			value,
			valueGenerator
		);
	}

	public static LiteralAttributeCodeGenerator fromValue(@Nonnull final LocationTagged<String> prefix, @Nonnull final LocationTagged<String> value) {
		return new LiteralAttributeCodeGenerator(prefix, value, null);
	}

	public static LiteralAttributeCodeGenerator fromValueGenerator(@Nonnull final LocationTagged<String> prefix, @Nonnull final LocationTagged<SpanCodeGenerator> valueGenerator) {
		return new LiteralAttributeCodeGenerator(prefix, null, valueGenerator);
	}

}
