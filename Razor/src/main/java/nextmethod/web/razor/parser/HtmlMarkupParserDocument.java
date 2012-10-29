package nextmethod.web.razor.parser;

import nextmethod.base.IDisposable;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
class HtmlMarkupParserDocument extends HtmlMarkupParserDelegate {

	protected HtmlMarkupParserDocument(final HtmlMarkupParser delegate) {
		super(delegate);
	}

	@Override
	public void parseDocument() {
		if (getContext() == null) {
			throw new UnsupportedOperationException(RazorResources().parserContextNotSet());
		}

		try (IDisposable ignored = pushSpanConfig(getBlockParser().defaultMarkupSpanDelegate)) {
			try (IDisposable ignored2 = getContext().startBlock(BlockType.Markup)) {
				nextToken();
				while (!isEndOfFile()) {
					skipToAndParseCode(HtmlSymbolType.OpenAngle);
					scanTagInDocumentContext();
				}
				addMarkerSymbolIfNecessary();
				output(SpanKind.Markup);
			}
		}
	}

	/**
	 * Reads the content of a tag (if present) in the MarkupDocument (or MarkupSection) context,
	 * where we don't care about maintaining a stack of tags.
	 *
	 * @return A boolean indicating if we scanned at least one tag.
	 */
	boolean scanTagInDocumentContext() {
		if (optional(HtmlSymbolType.OpenAngle) && !at(HtmlSymbolType.Solidus)) {
			final boolean scriptTag = at(HtmlSymbolType.Text) && "script".equalsIgnoreCase(getCurrentSymbol().getContent());
			optional(HtmlSymbolType.Text);
			getBlockParser().tagContent(); // Parse the tag, don't care about the content
			optional(HtmlSymbolType.Solidus);
			optional(HtmlSymbolType.CloseAngle);
			if (scriptTag) {
				getBlockParser().skipToEndScriptAndParseCode();
			}
			return true;
		}
		return false;
	}
}
