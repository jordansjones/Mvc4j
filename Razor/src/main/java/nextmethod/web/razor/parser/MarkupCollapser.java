package nextmethod.web.razor.parser;

import com.google.common.collect.Iterables;
import nextmethod.base.Delegates;
import nextmethod.web.razor.generator.MarkupCodeGenerator;
import nextmethod.web.razor.parser.internal.MarkupRewriter;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;

import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.base.TypeHelpers.typeIs;

/**
 *
 */
class MarkupCollapser extends MarkupRewriter {

	public MarkupCollapser(@Nonnull final Delegates.IAction3<SpanBuilder, SourceLocation, String> markupSpanFactory) {
		super(markupSpanFactory);
	}

	@Override
	protected boolean canRewrite(@Nonnull final Span span) {
		return span.getKind() == SpanKind.Markup && typeIs(span.getCodeGenerator(), MarkupCodeGenerator.class);
	}

	@Override
	protected SyntaxTreeNode rewriteSpan(@Nonnull final BlockBuilder parent, @Nonnull final Span span) {
		// Only rewrite if we have a previous that is also markup (canRewrite does this check for us!)
		final Span previous = typeAs(Iterables.getLast(parent.getChildren(), null), Span.class);
		if (previous == null || !canRewrite(previous)) {
			return span;
		}

		// Merge spans
		parent.getChildren().remove(previous);
		final SpanBuilder merged = new SpanBuilder();
		fillSpan(merged, previous.getStart(), previous.getContent() + span.getContent());
		return merged.build();
	}
}
