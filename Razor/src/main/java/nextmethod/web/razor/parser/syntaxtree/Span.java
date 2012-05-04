package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;

/**
 *
 */
public class Span extends SyntaxTreeNode {

	private SourceLocation start;
	private String content;

	public String getContent() {
		return content;
	}

	@Override
	public boolean isBlock() {
		return false;
	}

	@Override
	public int getLength() {
		return content == null ? 0 : content.length();
	}

	@Override
	public SourceLocation getStart() {
		return this.start;
	}

	@Override
	public void accept(@Nonnull final ParserVisitor visitor) {
	}

	@Override
	public boolean equivalentTo(@Nonnull final SyntaxTreeNode node) {
		return false;
	}
}
