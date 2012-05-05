package nextmethod.web.razor.text;

import com.google.common.primitives.Ints;

import javax.annotation.Nonnull;

/**
 *
 */
public class SourceLocation implements Comparable<SourceLocation> {

	public static final SourceLocation Undefined = createSimple(-1);
	public static final SourceLocation Zero = createSimple(0);


	private final int absoluteIndex;
	private final int lineIndex;
	private final int characterIndex;

	public SourceLocation(int absoluteIndex, int lineIndex, int characterIndex) {
		this.absoluteIndex = absoluteIndex;
		this.lineIndex = lineIndex;
		this.characterIndex = characterIndex;
	}

	public int getAbsoluteIndex() {
		return absoluteIndex;
	}

	public int getCharacterIndex() {
		return characterIndex;
	}

	public int getLineIndex() {
		return lineIndex;
	}

	@Override
	public String toString() {
		return String.format("(%d:%d,%d)", absoluteIndex, lineIndex, characterIndex);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SourceLocation)) return false;

		SourceLocation that = (SourceLocation) o;

		if (absoluteIndex != that.absoluteIndex) return false;
		if (characterIndex != that.characterIndex) return false;
		if (lineIndex != that.lineIndex) return false;

		return true;
	}

	@Override
	public int hashCode() {
		// LineIndex and CharacterIndex can be calculated form AbsoluteIndex and the document content.
		return absoluteIndex;
	}

	@Override
	public int compareTo(SourceLocation o) {
		return Ints.compare(absoluteIndex, o.absoluteIndex);
	}

	public static SourceLocation advance(final SourceLocation left, final String text) {
		return new SourceLocationTracker(left).updateLocation(text).getCurrentLocation();
	}

	public static SourceLocation add(@Nonnull final SourceLocation left, @Nonnull final SourceLocation right) {
		if (right.lineIndex > 0) {
			// Column index doesn't matter
			return new SourceLocation(left.absoluteIndex + right.absoluteIndex, left.lineIndex + right.lineIndex, right.characterIndex);
		}
		return new SourceLocation(left.absoluteIndex + right.absoluteIndex, left.lineIndex + right.lineIndex, left.characterIndex + right.characterIndex);
	}

	public static SourceLocation subtract(@Nonnull final SourceLocation left, @Nonnull final SourceLocation right) {
		return new SourceLocation(
			left.absoluteIndex - right.absoluteIndex,
			left.lineIndex - right.lineIndex,
			left.lineIndex != right.lineIndex ? left.characterIndex : left.characterIndex - right.characterIndex
		);
	}

	private static SourceLocation createSimple(final int val) {
		return new SourceLocation(val, val, val);
	}

	public static boolean isLessThan(final SourceLocation left, final SourceLocation right) {
		return left.compareTo(right) < 0;
	}

	public static boolean isGreaterThan(final SourceLocation left, final SourceLocation right) {
		return left.compareTo(right) > 0;
	}

	public static boolean isEqual(final SourceLocation left, final SourceLocation right) {
		return left.equals(right);
	}

	public static boolean isNotEqual(final SourceLocation left, final SourceLocation right) {
		return !left.equals(right);
	}
}
