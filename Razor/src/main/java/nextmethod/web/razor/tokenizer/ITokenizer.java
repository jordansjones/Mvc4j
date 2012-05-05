package nextmethod.web.razor.tokenizer;

import nextmethod.web.razor.tokenizer.symbols.ISymbol;

public interface ITokenizer<TSymbol extends ISymbol> {

	TSymbol nextSymbol();


}
