package nextmethod.web.razor.generator;

import nextmethod.annotations.TODO;
import nextmethod.base.IVoidAction;
import nextmethod.web.razor.parser.syntaxtree.Block;

import javax.annotation.Nonnull;

public class TemplateBlockCodeGenerator extends BlockCodeGenerator {

	private static final String TemplateWriterName = "__razor_template_writer";
	private static final String ItemParameterName = "item";

	private String oldTargetWriter;

	@Override
	public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
		final String generatedCode = context.buildCodeString(new IVoidAction<CodeWriter>() {
			@Override
			public void invoke(final CodeWriter input) {
				input.writeStartLambdaExpression(ItemParameterName);
				input.writeStartConstructor(context.getHost().getGeneratedClassContext().getTemplateTypeName());
				input.writeStartLambdaDelegate(TemplateWriterName);
			}
		});

		context.markEndOfGeneratedCode();
		context.bufferStatementFragment(generatedCode);
		context.flushBufferedStatement();

		oldTargetWriter = context.getTargetWriterName();
		context.setTargetWriterName(TemplateWriterName);
	}

	@Override
	public void generateEndBlockCode(@Nonnull Block target, @Nonnull CodeGeneratorContext context) {
		final String generatedCode = context.buildCodeString(new IVoidAction<CodeWriter>() {
			@Override
			public void invoke(final CodeWriter input) {
				input.writeEndLambdaDelegate();
				input.writeEndConstructor();
				input.writeEndLambdaExpression();
			}
		});

		context.bufferStatementFragment(generatedCode);
		context.setTargetWriterName(oldTargetWriter);
	}
}
