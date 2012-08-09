package nextmethod.web.razor.parser;

import com.google.common.base.Optional;
import nextmethod.threading.CancellationToken;
import nextmethod.threading.OperationCanceledException;
import nextmethod.web.razor.ParserResults;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 */
public abstract class ParserVisitor {

	private Optional<CancellationToken> cancelToken;

	public void visitBlock(@Nonnull final Block block) {
		visitStartBlock(block);
		for (SyntaxTreeNode node : block.getChildren()) {
			node.accept(this);
		}
		visitEndBlock(block);
	}

	public void visitStartBlock(@Nonnull final Block block) {
		throwIfCanceled();
	}

	public void visitSpan(@Nonnull final Span span) {
		throwIfCanceled();
	}

	public void visitEndBlock(@Nonnull final Block block) {
		throwIfCanceled();
	}

	public void visitError(@Nonnull final RazorError error) {
		throwIfCanceled();
	}

	public void onComplete() {
		throwIfCanceled();
	}

	public void throwIfCanceled() {
		if (cancelToken.isPresent() && cancelToken.get().isCancellationRequested()) {
			throw new OperationCanceledException();
		}
	}

	public void visit(@Nonnull final ParserResults result) {
		result.getDocument().accept(this);
		for (RazorError error : result.getParserErrors()) {
			visitError(error);
		}
		onComplete();
	}

	@Nullable
	public Optional<CancellationToken> getCancelToken() {
		return cancelToken;
	}

	public void setCancelToken(@Nullable final Optional<CancellationToken> cancelToken) {
		this.cancelToken = cancelToken;
	}
}
