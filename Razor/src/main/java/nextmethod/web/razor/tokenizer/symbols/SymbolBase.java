package nextmethod.web.razor.tokenizer.symbols;

import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class SymbolBase<TType> implements ISymbol {

	protected SourceLocation start;
	protected String content;
	protected TType type;
	protected Iterable<RazorError> errors;

	protected SymbolBase(@Nonnull final SourceLocation start, @Nonnull final String content, @Nonnull final TType type, final Iterable<RazorError> errors) {
		this.start = start;
		this.content = checkNotNull(content);
		this.type = checkNotNull(type);
		this.errors = errors;
	}

	@Override
	public void offsetStart(@Nonnull final SourceLocation documentStart) {
		this.start = SourceLocation.add(documentStart, start);
	}

	@Override
	public void changeStart(@Nonnull final SourceLocation newStart) {
		this.start = newStart;
	}

	public SourceLocation getStart() {
		return start;
	}

	public String getContent() {
		return content;
	}

	public TType getType() {
		return type;
	}

	public Iterable<RazorError> getErrors() {
		return errors;
	}

	@Override
	public String toString() {
		return String.format("%s %s - [%s]", this.start, this.type, this.content);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SymbolBase)) return false;

		SymbolBase that = (SymbolBase) o;

		return SourceLocation.isEqual(this.start, that.start)
			&& content.equals(that.content)
			&& type.equals(that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.start, this.content, this.type);
	}
}
