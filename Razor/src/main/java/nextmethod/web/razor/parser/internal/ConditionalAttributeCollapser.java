package nextmethod.web.razor.parser.internal;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import nextmethod.annotations.Internal;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.web.razor.editor.SpanEditHandler;
import nextmethod.web.razor.generator.AttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.generator.LiteralAttributeCodeGenerator;
import nextmethod.web.razor.generator.MarkupCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.HtmlTokenizer;
import nextmethod.web.razor.tokenizer.symbols.SymbolExtensions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collection;

import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.base.TypeHelpers.typeIs;

/**
 *
 */
@Internal
public class ConditionalAttributeCollapser extends MarkupRewriter {

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
		// Collect the content of this node
		final String content = concatNodeContent(block.getChildren());

		final SyntaxTreeNode first = Iterables.getFirst(parent.getChildren(), null);
		assert first != null;

		// Create a new span containing this content
		final SpanBuilder span = new SpanBuilder();
		span.setEditHandler(new SpanEditHandler(HtmlTokenizer.createTokenizeDelegate()));
		fillSpan(span, first.getStart(), content);
		return span.build();
	}

	private final String concatNodeContent(final Collection<SyntaxTreeNode> nodes) {
		final StringBuilder stringBuilder = new StringBuilder();
		for (SyntaxTreeNode node : nodes) {
			final Span span = typeAs(node, Span.class);
			if (span != null) {
				stringBuilder.append(span.getContent());
			}
		}
		return stringBuilder.toString();
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
