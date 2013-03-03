package nextmethod.web.razor.tokenizer.symbols;

import com.google.common.collect.Lists;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;

public class HtmlSymbol extends SymbolBase<HtmlSymbolType> {

	public HtmlSymbol(final int offset, final int line, final int column, @Nonnull final String content, @Nonnull final HtmlSymbolType type) {
		this(new SourceLocation(offset, line, column), content, type, Lists.<RazorError>newArrayList());
	}

	public HtmlSymbol(@Nonnull final SourceLocation start, @Nonnull final String content, @Nonnull final HtmlSymbolType type) {
		this(start, content, type, Lists.<RazorError>newArrayList());
	}

	public HtmlSymbol(@Nonnull final SourceLocation start, @Nonnull final String content, @Nonnull final HtmlSymbolType htmlSymbolType, final Iterable<RazorError> errors) {
		super(start, content, htmlSymbolType, errors);
	}

	public HtmlSymbol(final int offset, final int line, final int column, @Nonnull final String content, @Nonnull final HtmlSymbolType type, final Iterable<RazorError> errors) {
		this(new SourceLocation(offset, line, column), content, type, errors);
	}
}

