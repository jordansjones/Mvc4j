package nextmethod.web.razor.generator;

import com.google.common.base.Optional;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.web.razor.common.Mvc4jCommonResources.CommonResources;

@SuppressWarnings("UnusedDeclaration")
public final class GeneratedCodeMapping {

	private Optional<Integer> startOffset;
	private int codeLength;
	private int startColumn;
	private int startGeneratedColumn;
	private int startLine;

	public GeneratedCodeMapping(final int startLine, final int startColumn, final int startGeneratedColumn, final int codeLength) {
		this(Optional.<Integer>absent(), startLine, startColumn, startGeneratedColumn, codeLength);
	}

	public GeneratedCodeMapping(final int startOffset, final int startLine, final int startColumn, final int startGeneratedColumn, final int codeLength) {
		this(Optional.of(startOffset), startLine, startColumn, startGeneratedColumn, codeLength);
	}

	public GeneratedCodeMapping(@Nonnull final Optional<Integer> startOffset, final int startLine, final int startColumn, final int startGeneratedColumn, final int codeLength) {
		checkArgument(startLine < 0, CommonResources().argumentMustBeGreaterThanOrEqualTo("0"));
		checkArgument(startColumn < 0, CommonResources().argumentMustBeGreaterThanOrEqualTo("0"));
		checkArgument(startGeneratedColumn < 0, CommonResources().argumentMustBeGreaterThanOrEqualTo("0"));
		checkArgument(codeLength < 0, CommonResources().argumentMustBeGreaterThanOrEqualTo("0"));

		this.startOffset = startOffset;
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.startGeneratedColumn = startGeneratedColumn;
		this.codeLength = codeLength;
	}

	public Optional<Integer> getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(@Nonnull final Optional<Integer> startOffset) {
		this.startOffset = startOffset;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(final int startLine) {
		this.startLine = startLine;
	}

	public int getStartColumn() {
		return startColumn;
	}

	public void setStartColumn(final int startColumn) {
		this.startColumn = startColumn;
	}

	public int getStartGeneratedColumn() {
		return startGeneratedColumn;
	}

	public void setStartGeneratedColumn(final int startGeneratedColumn) {
		this.startGeneratedColumn = startGeneratedColumn;
	}

	public int getCodeLength() {
		return codeLength;
	}

	public void setCodeLength(final int codeLength) {
		this.codeLength = codeLength;
	}

	@Override
	public String toString() {
		return String.format(
			"(%s, %d, %d) -> (?, %d) [%d]",
			!startOffset.isPresent() ? "?" : startOffset.get(),
			startLine,
			startColumn,
			startGeneratedColumn,
			codeLength
		);
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object obj) {
		final GeneratedCodeMapping other = typeAs(obj, GeneratedCodeMapping.class);
		return other != null
			&& codeLength == other.codeLength
			&& startColumn == other.startColumn
			&& startGeneratedColumn == other.startGeneratedColumn
			&& startLine == other.startLine
			// Null means it matches the other no matter what
			&& (!startOffset.isPresent() || !other.startOffset.isPresent() || startOffset.equals(other.startOffset));
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			codeLength,
			startColumn,
			startGeneratedColumn,
			startLine,
			startOffset
		);
	}

	public static boolean isEqualTo(final GeneratedCodeMapping left, final GeneratedCodeMapping right) {
		return left.equals(right);
	}

	public static boolean isNotEqualTo(final GeneratedCodeMapping left, final GeneratedCodeMapping right) {
		return !isEqualTo(left, right);
	}
}
