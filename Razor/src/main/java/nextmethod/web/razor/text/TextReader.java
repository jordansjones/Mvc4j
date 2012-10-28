package nextmethod.web.razor.text;

import nextmethod.base.IDisposable;

import java.io.Reader;

public class TextReader extends Reader implements IDisposable {

	public static final TextReader Null = new TextReader();

	public int read() {
		return -1;
	}

	public int peek() {
		return -1;
	}

	@Override
	public int read(char[] cbuf, int off, int len) {
		int c, i;
		for (i = 0; i < len; i++) {
			if ((c = read()) == -1)
				return i;
			cbuf[off + i] = (char) c;
		}

		return i;
	}

	public String readToEnd() {
		final StringBuilder sb = new StringBuilder();
		int c;
		while ((c = read()) != -1)
			sb.append((char) c);

		return sb.toString();
	}

	@Override
	public final void close() {
		dispose(true);
	}

	protected void dispose(final boolean disposing) {

	}
}
