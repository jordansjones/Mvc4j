package nextmethod.web.razor.text;

import javax.annotation.Nonnull;

public class TextDocumentReader extends TextReader implements ITextDocument, AutoCloseable {

	private final ITextDocument document;

	public TextDocumentReader(@Nonnull final ITextDocument document) {
		this.document = document;
	}

	@Override
	public SourceLocation getLocation() {
		return document.getLocation();
	}

	@Override
	public int getLength() {
		return document.getLength();
	}

	@Override
	public int getPosition() {
		return document.getPosition();
	}

	@Override
	public void setPosition(int position) {
		document.setPosition(position);
	}

	@Override
	public int read() {
		return document.read();
	}

	@Override
	public int peek() {
		return document.peek();
	}

}
