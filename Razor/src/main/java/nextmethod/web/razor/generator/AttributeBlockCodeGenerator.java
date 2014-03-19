package nextmethod.web.razor.generator;

import nextmethod.base.Delegates;
import nextmethod.base.Strings;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.text.LocationTagged;

import javax.annotation.Nonnull;
import java.util.Objects;

import static nextmethod.base.TypeHelpers.typeAs;

public class AttributeBlockCodeGenerator extends BlockCodeGenerator {

	private final String name;
	private final LocationTagged<String> prefix;
	private final LocationTagged<String> suffix;

	public AttributeBlockCodeGenerator(@Nonnull final String name, @Nonnull final LocationTagged<String> prefix, @Nonnull final LocationTagged<String> suffix) {
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	@Override
	public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
		final RazorEngineHost host = context.getHost();
		if (host.isDesignTimeMode()) {
			return;
		}

		context.flushBufferedStatement();
		context.addStatement(context.buildCodeString(input -> {
			if (!Strings.isNullOrEmpty(context.getTargetWriterName())) {
				input.writeStartMethodInvoke(host.getGeneratedClassContext().getWriteAttributeToMethodName());
				input.writeSnippet(context.getTargetWriterName());
				input.writeParameterSeparator();
			}
			else {
				input.writeStartMethodInvoke(host.getGeneratedClassContext().getWriteAttributeMethodName());
			}
			input.writeStringLiteral(name);
			input.writeParameterSeparator();
			input.writeLocationTaggedString(prefix);
			input.writeParameterSeparator();
			input.writeLocationTaggedString(suffix);
			input.writeLineContinuation();
		}));
	}

	@Override
	public void generateEndBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
		if (context.getHost().isDesignTimeMode()) {
			return;
		}

		context.flushBufferedStatement();
		context.addStatement(context.buildCodeString(input -> {
			input.writeEndMethodInvoke();
			input.writeEndStatement();
		}));
	}

	@Override
	public String toString() {
		return String.format("Attr:%s,%s,%s", this.name, this.prefix, this.suffix);
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object obj) {
		final AttributeBlockCodeGenerator other = typeAs(obj, AttributeBlockCodeGenerator.class);
		return other != null
			&& Objects.equals(other.name, name)
			&& Objects.equals(other.prefix, prefix)
			&& Objects.equals(other.suffix, suffix);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			this.name,
			this.prefix,
			this.suffix
		);
	}
}
