package nextmethod.web.razor.generator;

import nextmethod.base.Delegates;
import nextmethod.web.razor.parser.syntaxtree.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static nextmethod.base.TypeHelpers.typeAs;

public class SectionCodeGenerator extends BlockCodeGenerator {

	private final String sectionName;

	public SectionCodeGenerator(@Nonnull final String sectionName) {
		this.sectionName = sectionName;
	}

	@Override
	public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
		final String startBlock = context.buildCodeString(new Delegates.IAction1<CodeWriter>() {
			@Override
			public void invoke(@Nullable final CodeWriter input) {
				assert input != null;

				input.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getDefineSectionMethodName());
				input.writeStringLiteral(sectionName);
				input.writeParameterSeparator();
				input.writeStartLambdaDelegate();
			}
		});

		context.addStatement(startBlock);
	}

	@Override
	public void generateEndBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
		final String block = context.buildCodeString(new Delegates.IAction1<CodeWriter>() {
			@Override
			public void invoke(@Nullable final CodeWriter input) {
				assert input != null;

				input.writeEndLambdaDelegate();
				input.writeEndMethodInvoke();
				input.writeEndStatement();
			}
		});

		context.addStatement(block);
	}

	public String getSectionName() {
		return sectionName;
	}

	@Override
	public String toString() {
		return "Section:" + sectionName;
	}

	@Override
	public boolean equals(final Object obj) {
		final SectionCodeGenerator other = typeAs(obj, SectionCodeGenerator.class);
		return other != null
			&& super.equals(other)
			&& sectionName.equals(other.sectionName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			super.hashCode(),
			sectionName
		);
	}
}
