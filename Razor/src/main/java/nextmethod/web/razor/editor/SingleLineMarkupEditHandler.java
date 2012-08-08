package nextmethod.web.razor.editor;

import nextmethod.base.Delegates;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class SingleLineMarkupEditHandler extends SpanEditHandler {

	public SingleLineMarkupEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer) {
		super(tokenizer);
	}

	public SingleLineMarkupEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer, @Nonnull final AcceptedCharacters accepted) {
		super(tokenizer, accepted);
	}

	public SingleLineMarkupEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer, @Nonnull final EnumSet<AcceptedCharacters> accepted) {
		super(tokenizer, accepted);
	}
}
