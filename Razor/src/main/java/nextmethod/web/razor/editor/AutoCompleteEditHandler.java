package nextmethod.web.razor.editor;

import com.google.common.base.Strings;
import nextmethod.base.Delegates;
import nextmethod.web.razor.PartialParseResult;
import nextmethod.web.razor.parser.ParserHelpers;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.TextChange;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import javax.annotation.Nonnull;
import java.util.EnumSet;

import static com.google.common.base.Strings.nullToEmpty;

public class AutoCompleteEditHandler extends SpanEditHandler {

	private boolean autoCompleteAtEndOfSpan;
	private String autoCompleteString;

	public AutoCompleteEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer) {
		super(tokenizer);
	}

	public AutoCompleteEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer, @Nonnull final AcceptedCharacters accepted) {
		super(tokenizer, accepted);
	}

	public AutoCompleteEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer, @Nonnull final EnumSet<AcceptedCharacters> accepted) {
		super(tokenizer, accepted);
	}

	@Override
	protected EnumSet<PartialParseResult> canAcceptChange(@Nonnull final Span target, @Nonnull final TextChange normalizedChange) {
		if (((autoCompleteAtEndOfSpan && isAtEndOfSpan(target, normalizedChange)) || isAtEndOfFirstLine(target, normalizedChange))
			&& normalizedChange.isInsert()
			&& ParserHelpers.isNewLine(normalizedChange.getNewText())
			&& autoCompleteString != null
			) {
			return PartialParseResult.setOf(PartialParseResult.Rejected, PartialParseResult.AutoCompleteBlock);
		}
		return EnumSet.of(PartialParseResult.Rejected);
	}

	public boolean isAutoCompleteAtEndOfSpan() {
		return autoCompleteAtEndOfSpan;
	}

	public void setAutoCompleteAtEndOfSpan(boolean autoCompleteAtEndOfSpan) {
		this.autoCompleteAtEndOfSpan = autoCompleteAtEndOfSpan;
	}

	public String getAutoCompleteString() {
		return autoCompleteString;
	}

	public void setAutoCompleteString(String autoCompleteString) {
		this.autoCompleteString = autoCompleteString;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AutoCompleteEditHandler)) return false;
		final AutoCompleteEditHandler that = (AutoCompleteEditHandler) o;

		return super.equals(that)
			&& nullToEmpty(autoCompleteString).equals(nullToEmpty(that.autoCompleteString))
			&& autoCompleteAtEndOfSpan == that.autoCompleteAtEndOfSpan;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (autoCompleteString != null ? autoCompleteString.hashCode() : 0);
		return result;
	}
}
