package nextmethod.web.razor.parser;

import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

// TODO
public class ParserContext {

	public ParserContext(@Nonnull final ITextDocument source, @Nonnull final ParserBase codeParser, @Nonnull final ParserBase markupParser, @Nonnull final ParserBase activeParser) {
		checkNotNull(source, "source");
		checkNotNull(codeParser, "codeParser");
		checkNotNull(markupParser, "markupParser");
		checkNotNull(activeParser, "activeParser");
	}

	// TODO
	public ITextDocument getSource() {
		return null;
	}

	void assertOnOwnerTask() {
		// TODO
	}

	public ParserBase getMarkupParser() {
		return null;
	}

	public List<RazorError> getErrors() {
		return null;
	}

	public EnumSet<AcceptedCharacters> getLastAcceptedCharacters() {
		return null;
	}

	public void addSpan(@Nonnull final Span span) {

	}

	public void onError(@Nonnull final SourceLocation location, @Nonnull final String message, @Nonnull Object... args) {

	}

}
