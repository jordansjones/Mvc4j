package nextmethod.web.razor.parser.internal;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import nextmethod.annotations.Internal;
import nextmethod.base.Delegates;
import nextmethod.web.razor.parser.ParserHelpers;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.base.TypeHelpers.typeAs;

/**
 *
 */
@Internal
public class WhiteSpaceRewriter extends MarkupRewriter {

	public WhiteSpaceRewriter(@Nonnull Delegates.IAction3<SpanBuilder, SourceLocation, String> markupSpanFactory) {
		super(markupSpanFactory);
	}

	@Override
	protected boolean canRewrite(@Nonnull final Block block) {
		return checkNotNull(block).getType() == BlockType.Expression && getParent() != null;
	}

	@Override
	protected SyntaxTreeNode rewriteBlock(@Nonnull final BlockBuilder parent, @Nonnull final Block block) {
		final BlockBuilder newBlock = new BlockBuilder(checkNotNull(block));
		newBlock.getChildren().clear();
		final Span ws = typeAs(Iterables.getFirst(block.getChildren(), null), Span.class);
		Collection<SyntaxTreeNode> newNodes = block.getChildren();
		if (ws != null && ParserHelpers.isAllOfString(ws.getContent(), ParserHelpers.IsWhitespacePredicate)) {
			// Add this node to the parent
			final SpanBuilder builder = new SpanBuilder(ws);
			builder.clearSymbols();
			fillSpan(builder, ws.getStart(), ws.getContent());
			parent.getChildren().add(builder.build());

			// Remove the old whitespace node
			newNodes = Lists.newArrayList(Iterables.skip(block.getChildren(), 1));
		}

		for (SyntaxTreeNode node : newNodes) {
			newBlock.getChildren().add(node);
		}

		return newBlock.build();
	}
}
