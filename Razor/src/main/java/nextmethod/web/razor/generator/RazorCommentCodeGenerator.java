package nextmethod.web.razor.generator;

import nextmethod.base.Delegates;
import nextmethod.base.Strings;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.syntaxtree.Block;

import javax.annotation.Nonnull;

public class RazorCommentCodeGenerator extends BlockCodeGenerator {

	@Override
	public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
		// Flush the buffered statement since we're interrupting it with a comment.
		if (!Strings.isNullOrEmpty(context.getCurrentBufferedStatement())) {
			context.markEndOfGeneratedCode();
			context.bufferStatementFragment(context.buildCodeString(CodeWriter::writeLineContinuation));
		}
		context.flushBufferedStatement();
	}


}
