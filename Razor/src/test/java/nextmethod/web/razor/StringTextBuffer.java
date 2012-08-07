package nextmethod.web.razor;

import nextmethod.base.IDisposable;
import nextmethod.web.razor.text.ITextBuffer;

import static com.google.common.base.Preconditions.checkNotNull;

public class StringTextBuffer implements ITextBuffer, IDisposable {

	private final char[] buffer;
	private boolean disposed;
	private int position;

	public StringTextBuffer(final String buffer) {
		checkNotNull(buffer);
		this.buffer = buffer.toCharArray();
	}

	@Override
	public void close() {
		disposed = true;
	}

	@Override
	public int getLength() {
		return buffer.length;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public int read() {
		if (position >= buffer.length) {
			return -1;
		}
		return buffer[position++];
	}

	@Override
	public int peek() {
		if (position >= buffer.length) {
			return -1;
		}
		return buffer[position];
	}

	public boolean isDisposed() {
		return disposed;
	}

	public void setDisposed(boolean disposed) {
		this.disposed = disposed;
	}
}
