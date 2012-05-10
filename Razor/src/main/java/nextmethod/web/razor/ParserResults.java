package nextmethod.web.razor;

import com.google.common.collect.Lists;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.RazorError;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents the results of parsing a Razor document.
 */
public class ParserResults {

	private final boolean success;
	private final Block document;
	private final List<RazorError> parserErrors;

	public ParserResults(@Nonnull final Block document, @Nullable final List<RazorError> parserErrors) {
		this(parserErrors == null || parserErrors.isEmpty(), document, parserErrors);
	}

	protected ParserResults(final boolean success, @Nonnull final Block document, @Nullable final List<RazorError> parserErrors) {
		this.success = success;
		this.document = document;
		this.parserErrors = parserErrors != null ? parserErrors : Lists.<RazorError>newArrayList();
	}

	/**
	 * Indicates if parsing was successful (no errors).
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * The root node in the document's syntax tree.
	 */
	public Block getDocument() {
		return document;
	}

	/**
	 * The list of errors which occurred during parsing.
	 */
	public List<RazorError> getParserErrors() {
		return parserErrors;
	}
}
