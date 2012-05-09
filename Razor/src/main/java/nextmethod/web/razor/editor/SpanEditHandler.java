package nextmethod.web.razor.editor;

import com.google.common.base.Function;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import javax.annotation.Nonnull;

public class SpanEditHandler {

	public static SpanEditHandler createDefault() {
//		return createDefault(Functions.)
		return null;
	}

	public static SpanEditHandler createDefault(@Nonnull final Function<String, Iterable<ISymbol>> tokenizer) {
		return null;
	}
}
