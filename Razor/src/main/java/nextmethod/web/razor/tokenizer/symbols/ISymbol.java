package nextmethod.web.razor.tokenizer.symbols;

import nextmethod.web.razor.text.SourceLocation;

public interface ISymbol {

	SourceLocation getStart();
	String getContent();

	void offsetStart(SourceLocation documentStart);
	void changeStart(SourceLocation newStart);

}
