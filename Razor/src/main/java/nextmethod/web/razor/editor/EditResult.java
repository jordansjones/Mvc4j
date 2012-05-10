package nextmethod.web.razor.editor;

import nextmethod.web.razor.PartialParseResult;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class EditResult {

	private SpanBuilder editedSpan;
	private EnumSet<PartialParseResult> results;

	public EditResult(@Nonnull final SpanBuilder editedSpan, @Nonnull final PartialParseResult results) {
		this(editedSpan, EnumSet.of(results));
	}

	public EditResult(@Nonnull final SpanBuilder editedSpan, @Nonnull final PartialParseResult... results) {
		this(editedSpan, PartialParseResult.setOf(results));
	}

	public EditResult(@Nonnull final SpanBuilder editedSpan, @Nonnull final EnumSet<PartialParseResult> results) {
		this.results = results;
		this.editedSpan = editedSpan;
	}

	public SpanBuilder getEditedSpan() {
		return editedSpan;
	}

	public EnumSet<PartialParseResult> getResults() {
		return results;
	}
}
