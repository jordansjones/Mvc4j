package nextmethod.web.razor.parser;

import nextmethod.web.razor.tokenizer.HtmlTokenizer;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;

/**
 *
 */
public class HtmlMarkupParser extends TokenizerBackedParser<HtmlTokenizer, HtmlSymbol, HtmlSymbolType> {

	@Override
	protected LanguageCharacteristics<HtmlTokenizer, HtmlSymbol, HtmlSymbolType> getLanguage() {
		return null;
	}

	@Override
	protected ParserBase getOtherParser() {
		return null;
	}

	@Override
	public void parseBlock() {
	}
}
