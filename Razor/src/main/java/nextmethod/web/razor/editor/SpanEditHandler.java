package nextmethod.web.razor.editor;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import nextmethod.base.Delegates;
import nextmethod.web.razor.PartialParseResult;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.SourceLocationTracker;
import nextmethod.web.razor.text.TextChange;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

public class SpanEditHandler {

	private EnumSet<AcceptedCharacters> acceptedCharacters;
	private Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer;
	private EnumSet<EditorHints> editorHints;


	public SpanEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer) {
		this(tokenizer, AcceptedCharacters.Any);
	}

	public SpanEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer, @Nonnull final AcceptedCharacters accepted) {
		this(tokenizer, EnumSet.of(accepted));
	}

	public SpanEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer, @Nonnull final EnumSet<AcceptedCharacters> accepted) {
		this.tokenizer = tokenizer;
		this.acceptedCharacters = accepted;
	}

	public static SpanEditHandler createDefault() {
		return createDefault(new Delegates.IFunc1<String, Iterable<ISymbol>>() {
			@Override
			public Iterable<ISymbol> invoke(@Nullable final String input1) {
				return Lists.newArrayList();
			}
		});
	}

	public static SpanEditHandler createDefault(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer) {
		return new SpanEditHandler(tokenizer);
	}

	public EditResult applyChange(@Nonnull final Span target, @Nonnull final TextChange change) {
		return applyChange(target, change, false);
	}

	public EditResult applyChange(@Nonnull final Span target, @Nonnull final TextChange change, final boolean force) {
		EnumSet<PartialParseResult> result = EnumSet.of(PartialParseResult.Accepted);
		final TextChange normalized = change.normalize();
		if (!force) {
			result = canAcceptChange(target, normalized);
		}

		// If the change is accepted then apply the change
		if (result.contains(PartialParseResult.Accepted)) {
			return new EditResult(updateSpan(target, normalized), result);
		}

		return new EditResult(new SpanBuilder(target), result);
	}

	public boolean ownsChange(@Nonnull final Span target, @Nonnull final TextChange change) {
		final int end = target.getStart().getAbsoluteIndex() + target.getLength();
		final int changeOldEnd = change.getOldPosition() + change.getOldLength();
		return change.getOldPosition() >= target.getStart().getAbsoluteIndex() &&
			(changeOldEnd < end || (changeOldEnd == end && acceptedCharacters != AcceptedCharacters.SetOfNone));
	}

	protected EnumSet<PartialParseResult> canAcceptChange(@Nonnull final Span target, @Nonnull final TextChange normalizedChange) {
		return EnumSet.of(PartialParseResult.Rejected);
	}

	protected SpanBuilder updateSpan(@Nonnull final Span target, @Nonnull final TextChange normalizedChange) {
		final String newContent = normalizedChange.applyChange(target);
		final SpanBuilder newSpan = new SpanBuilder(target);
		newSpan.clearSymbols();
		for (ISymbol symbol : Lists.newArrayList(tokenizer.invoke(newContent))) {
			symbol.offsetStart(target.getStart());
			newSpan.accept(symbol);
		}
		if (target.getNext() != null) {
			final SourceLocation newEnd = SourceLocationTracker.calculateNewLocation(target.getStart(), newContent);
			target.getNext().changeStart(newEnd);
		}
		return newSpan;
	}

	public EnumSet<AcceptedCharacters> getAcceptedCharacters() {
		return acceptedCharacters;
	}

	public void setAcceptedCharacters(@Nonnull final EnumSet<AcceptedCharacters> acceptedCharacters) {
		this.acceptedCharacters = acceptedCharacters;
	}

	public void setAcceptedCharacters(@Nonnull final AcceptedCharacters first, @Nullable final AcceptedCharacters... rest) {
		setAcceptedCharacters(
			rest == null || rest.length < 1
				? EnumSet.of(first)
				: EnumSet.of(first, rest)
		);
	}

	public EnumSet<EditorHints> getEditorHints() {
		return editorHints;
	}

	public void setEditorHints(@Nonnull final EnumSet<EditorHints> editorHints) {
		this.editorHints = editorHints;
	}

	public void setEditorHints(@Nonnull final EditorHints first, @Nullable final EditorHints... rest) {
		setEditorHints(
			rest == null || rest.length < 1
				? EnumSet.of(first)
				: EnumSet.of(first, rest)
		);
	}

	public Delegates.IFunc1<String, Iterable<ISymbol>> getTokenizer() {
		return tokenizer;
	}

	private static final Joiner joiner = Joiner.on(',');

	@Override
	public String toString() {
		return String.format(
			"%s;Accepts:%s%s",
			getClass().getSimpleName(),
			joiner.join(acceptedCharacters),
			editorHints == null ? "" : (editorHints == EnumSet.of(EditorHints.None)
				? ""
				: (";Hints:" + joiner.join(editorHints)))
		);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SpanEditHandler)) return false;

		SpanEditHandler that = (SpanEditHandler) o;
		return Objects.equals(getAcceptedCharacters(), that.getAcceptedCharacters())
			&& Objects.equals(getEditorHints(), that.getEditorHints());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			getAcceptedCharacters(),
			getEditorHints()
		);
	}

	protected static boolean isAtEndOfFirstLine(@Nonnull final Span target, @Nonnull final TextChange change) {
		final int endOfFirstLine = indexOfAny(target.getContent(), new char[]{(char) 0x000d, (char) 0x000a, (char) 0x2028, (char) 0x2029});
		return (endOfFirstLine == -1 || (change.getOldPosition() - target.getStart().getAbsoluteIndex()) <= endOfFirstLine);
	}

	protected static boolean isEndInsertion(@Nonnull final Span target, @Nonnull final TextChange change) {
		return change.isInsert() && isAtEndOfSpan(target, change);
	}

	protected static boolean isEndDeletion(@Nonnull final Span target, @Nonnull final TextChange change) {
		return change.isDelete() && isAtEndOfSpan(target, change);
	}

	protected static boolean isEndReplace(@Nonnull final Span target, @Nonnull final TextChange change) {
		return change.isReplace() && isAtEndOfSpan(target, change);
	}

	protected static boolean isAtEndOfSpan(@Nonnull final Span target, @Nonnull final TextChange change) {
		return (change.getOldPosition() + change.getOldLength()) == (target.getStart().getAbsoluteIndex() + target.getLength());
	}

	protected static boolean isAdjecentOnRight(@Nonnull final Span target, @Nonnull final Span other) {
		final int targetIdx = target.getStart().getAbsoluteIndex();
		final int otherIdx = other.getStart().getAbsoluteIndex();
		return targetIdx < otherIdx && targetIdx + target.getLength() == otherIdx;
	}

	protected static boolean isAdjecentOnLeft(@Nonnull final Span target, @Nonnull final Span other) {
		final int targetIdx = target.getStart().getAbsoluteIndex();
		final int otherIdx = other.getStart().getAbsoluteIndex();
		return otherIdx < targetIdx && otherIdx + other.getLength() == targetIdx;
	}

	protected static String getOldText(@Nonnull final Span target, @Nonnull final TextChange change) {
		final int offset = change.getOldPosition() - target.getStart().getAbsoluteIndex();
		return target.getContent().substring(offset, (offset + change.getOldLength()));
	}


	protected static int indexOfAny(@Nonnull final String target, final char[] anyOf) {
		int result = -1;
		if (Strings.isNullOrEmpty(target) || anyOf == null || anyOf.length < 1) return result;

		for (char c : anyOf) {
			result = target.indexOf(c);
			if (result != -1)
				break;
		}
		return result;
	}
}
