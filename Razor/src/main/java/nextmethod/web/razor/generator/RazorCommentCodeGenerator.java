package nextmethod.web.razor.generator;

import com.google.common.base.Strings;
import nextmethod.base.IVoidAction;
import nextmethod.web.razor.parser.syntaxtree.Block;

import javax.annotation.Nonnull;

public class RazorCommentCodeGenerator extends BlockCodeGenerator {

	@Override
	public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
		// Flush the buffered statement since we're interrupting it with a comment.
		if (!Strings.isNullOrEmpty(context.getCurrentBufferedStatement())) {
			context.markEndOfGeneratedCode();
			context.bufferStatementFragment(context.buildCodeString(new IVoidAction<CodeWriter>() {
				@Override
				public void invoke(final CodeWriter input) {
					input.writeLineContinuation();
				}
			}));
		}
		context.flushBufferedStatement();
	}
}
