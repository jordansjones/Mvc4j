package nextmethod.web.razor.framework;

import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.SourceLocationTracker;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import javax.annotation.Nonnull;
import java.util.Objects;

import static nextmethod.base.TypeHelpers.typeAs;

class RawTextSymbol implements ISymbol {

	private SourceLocation start;
	private String content;

	RawTextSymbol(@Nonnull final SourceLocation start, @Nonnull final String content) {
		this.start = start;
		this.content = content;
	}

	@Override
	public SourceLocation getStart() {
		return start;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void offsetStart(final SourceLocation documentStart) {
		this.start = SourceLocation.add(documentStart, this.start);
	}

	@Override
	public void changeStart(final SourceLocation newStart) {
		this.start = newStart;
	}

	void calculateStart(final Span prev) {
		if (prev == null) {
			this.start = SourceLocation.Zero;
		}
		else {
			this.start = new SourceLocationTracker(prev.getStart()).updateLocation(prev.getContent()).getCurrentLocation();
		}
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object obj) {
		final RawTextSymbol other = typeAs(obj, RawTextSymbol.class);
		return other != null && Objects.equals(start, other.start) && Objects.equals(content, other.content);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			start,
			content
		);
	}
}
