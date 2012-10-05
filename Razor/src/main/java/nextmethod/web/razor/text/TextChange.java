package nextmethod.web.razor.text;

import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.web.razor.common.Mvc4jCommonResources.CommonResources;

public class TextChange {

	private final int oldPosition;
	private final int newPosition;
	private final int oldLength;
	private final int newLength;
	private final ITextBuffer oldBuffer;
	private final ITextBuffer newBuffer;

	private String newText;
	private String oldText;

	public TextChange(final int position, final int oldLength, @Nonnull final ITextBuffer oldBuffer, final int newLength, @Nonnull final ITextBuffer newBuffer) {
		this(position, oldLength, oldBuffer, position, newLength, newBuffer);
	}

	public TextChange(final int oldPosition, final int oldLength, @Nonnull final ITextBuffer oldBuffer, final int newPosition, final int newLength, @Nonnull final ITextBuffer newBuffer) {
		checkArgument(oldPosition >= 0, CommonResources().getString("argument.must.be.greaterThanOrEqualTo"), "oldPosition", "0");
		checkArgument(newPosition >= 0, CommonResources().getString("argument.must.be.greaterThanOrEqualTo"), "newPosition", "0");
		checkArgument(oldLength >= 0, CommonResources().getString("argument.must.be.greaterThanOrEqualTo"), "oldLength", "0");
		checkArgument(newLength >= 0, CommonResources().getString("argument.must.be.greaterThanOrEqualTo"), "newLength", "0");
		checkNotNull(oldBuffer);
		checkNotNull(newBuffer);

		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
		this.oldLength = oldLength;
		this.newLength = newLength;
		this.oldBuffer = oldBuffer;
		this.newBuffer = newBuffer;
	}

	public String getOldText() {
		if (oldText == null && oldBuffer != null)
			oldText = getText(oldBuffer, oldPosition, oldLength);
		return oldText;
	}

	public String getNewText() {
		if (newText == null)
			newText = getText(newBuffer, newPosition, newLength);
		return newText;
	}

	public boolean isInsert() {
		return oldLength == 0 && newLength > 0;
	}

	public boolean isDelete() {
		return oldLength > 0 && newLength == 0;
	}

	public boolean isReplace() {
		return oldLength > 0 && newLength > 0;
	}

	public String applyChange(@Nonnull final String content, final int changeOffset) {
		final int relPos = oldPosition - changeOffset;
		assert relPos >= 0;
		return content.replace(
			content.substring(relPos, (relPos + oldLength)),
			getNewText()
		);
	}

	public String applyChange(@Nonnull final Span span) {
		return applyChange(span.getContent(), span.getStart().getAbsoluteIndex());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TextChange)) return false;

		TextChange that = (TextChange) o;

		if (newLength != that.newLength) return false;
		if (newPosition != that.newPosition) return false;
		if (oldLength != that.oldLength) return false;
		if (oldPosition != that.oldPosition) return false;
		if (newBuffer != null ? !newBuffer.equals(that.newBuffer) : that.newBuffer != null) return false;
		if (newText != null ? !newText.equals(that.newText) : that.newText != null) return false;
		if (oldBuffer != null ? !oldBuffer.equals(that.oldBuffer) : that.oldBuffer != null) return false;
		if (oldText != null ? !oldText.equals(that.oldText) : that.oldText != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(oldPosition, newPosition, oldLength, newLength, newBuffer, oldBuffer);
	}

	@Override
	public String toString() {
		return String.format(
			"(%1$d:%2$d) \"%4$s\" -> (%1$d:%3$d) \"%5$s\"",
			oldPosition,
			oldLength,
			newLength,
			getOldText(),
			getNewText()
		);
	}

	public TextChange normalize() {
		if (oldBuffer != null && isReplace() && newLength > oldLength && newText.startsWith(oldText) && newPosition == oldPosition) {
			// Normalize the change into an insertion of the uncommon suffix (i.e. stop out the common prefix)
			return new TextChange(
				oldPosition + oldLength,
				0,
				oldBuffer,
				oldPosition + oldLength,
				newLength - oldLength,
				newBuffer
			);
		}
		return this;
	}

	private String getText(@Nonnull final ITextBuffer buffer, final int position, final int length) {
		final int oldPosition = buffer.getPosition();
		try {
			buffer.setPosition(position);

			// Optimization for the common case of one char inserts
			if (newLength == 1) {
				return String.valueOf((char) buffer.read());
			} else {
				final StringBuilder sb = new StringBuilder();
				for (int i = 0; i < length; i++) {
					final char c = (char) buffer.read();
					sb.append(c);
					if (Character.isHighSurrogate(c))
						sb.append((char) buffer.read());
				}
				return sb.toString();
			}
		} finally {
			buffer.setPosition(oldPosition);
		}
	}

	public int getOldPosition() {
		return oldPosition;
	}

	public int getNewPosition() {
		return newPosition;
	}

	public int getOldLength() {
		return oldLength;
	}

	public int getNewLength() {
		return newLength;
	}

	public ITextBuffer getOldBuffer() {
		return oldBuffer;
	}

	@Nullable
	public ITextBuffer getNewBuffer() {
		return newBuffer;
	}
}
