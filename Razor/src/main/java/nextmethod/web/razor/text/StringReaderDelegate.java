package nextmethod.web.razor.text;

import com.google.common.base.Throwables;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;

public class StringReaderDelegate extends TextReader {

	private final StringReader reader;

	public StringReaderDelegate(@Nonnull final String content) {
		this.reader = new StringReader(content);
	}

	@Override
	public int peek() {
		try {
			this.reader.mark(1);
			return this.reader.read();
		}
		catch (IOException e) {
			throw Throwables.propagate(e);
		}
		finally {
			try {
				this.reader.reset();
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}
	}

	@Override
	protected void dispose(boolean disposing) {
		if (disposing) {
			this.reader.close();
		}
		super.dispose(disposing);
	}

	@Override
	public void mark(int readAheadLimit) throws IOException {
		reader.mark(readAheadLimit);
	}

	@Override
	public boolean markSupported() {
		return reader.markSupported();
	}

	@Override
	public int read() {
		try {
			return reader.read();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public boolean ready() {
		try {
			return reader.ready();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public void reset() {
		try {
			reader.reset();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public long skip(long ns) {
		try {
			return reader.skip(ns);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public int read(char[] cbuf) {
		try {
			return reader.read(cbuf);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public int read(CharBuffer target) {
		try {
			return reader.read(target);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}
}
