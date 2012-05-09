package nextmethod.web.razor.parser.syntaxtree;

import nextmethod.base.IAction;
import nextmethod.web.razor.editor.SpanEditHandler;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import javax.annotation.Nonnull;

/**
 *
 */
public class Span extends SyntaxTreeNode {

	private SourceLocation start;
	private String content;
	private SpanKind kind;
	private Iterable<ISymbol> symbols;
	private Span previous;
	private Span next;
	private SpanEditHandler editHandler;
	private ISpanCodeGenerator codeGenerator;

	public Span(@Nonnull final SpanBuilder builder) {
		replaceWith(builder);
	}

	public void change(@Nonnull final IAction<SpanBuilder> changes) {
		final SpanBuilder builder = new SpanBuilder(this);
		// TODO invoke changes function
		replaceWith(builder);
	}

	public void replaceWith(@Nonnull final SpanBuilder builder) {

	}

	public SpanKind getKind() {
		return kind;
	}

	public Iterable<ISymbol> getSymbols() {
		return symbols;
	}

	public Span getPrevious() {
		return previous;
	}

	public Span getNext() {
		return next;
	}

	public SpanEditHandler getEditHandler() {
		return editHandler;
	}

	public ISpanCodeGenerator getCodeGenerator() {
		return codeGenerator;
	}

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
