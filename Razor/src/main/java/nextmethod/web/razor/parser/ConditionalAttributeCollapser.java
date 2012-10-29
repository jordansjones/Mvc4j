package nextmethod.web.razor.parser;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.web.razor.generator.AttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.generator.LiteralAttributeCodeGenerator;
import nextmethod.web.razor.generator.MarkupCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.internal.MarkupRewriter;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.base.TypeHelpers.typeIs;

/**
 *
 */
class ConditionalAttributeCollapser extends MarkupRewriter {

	public ConditionalAttributeCollapser(@Nonnull final Delegates.IAction3<SpanBuilder, SourceLocation, String> markupSpanFactory) {
		super(markupSpanFactory);
	}

	@Override
	protected boolean canRewrite(@Nonnull final Block block) {
		final AttributeBlockCodeGenerator gen = typeAs(block.getCodeGenerator(), AttributeBlockCodeGenerator.class);
		return gen != null && !block.getChildren().isEmpty() && Iterables.all(block.getChildren(), isLiteralAttributeValuePredicate);
	}

	@Override
	protected SyntaxTreeNode rewriteBlock(@Nonnull final BlockBuilder parent, @Nonnull final Block block) {
		return super.rewriteBlock(parent, block);
	}

	private final Predicate<SyntaxTreeNode> isLiteralAttributeValuePredicate = new Predicate<SyntaxTreeNode>() {
		@Override
		public boolean apply(@Nullable final SyntaxTreeNode node) {
			if (node == null) return false;
			if (node.isBlock()) return false;

			final Span span = typeAs(node, Span.class);
			if (Debug.isAssertEnabled()) assert span != null;

			if (span == null)
				return false;

			final ISpanCodeGenerator codeGen = span.getCodeGenerator();

			final LiteralAttributeCodeGenerator litGen = typeAs(codeGen, LiteralAttributeCodeGenerator.class);

			return (litGen != null && litGen.getValueGenerator() == null)
				|| codeGen == SpanCodeGenerator.Null
				|| typeIs(codeGen, MarkupCodeGenerator.class);
		}
	};
}
