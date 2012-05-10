package nextmethod.web.razor.editor;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.TextChange;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class SpanEditHandler {

	private EnumSet<AcceptedCharacters> acceptedCharacters;
	private Function<String, Iterable<ISymbol>> tokenizer;


	public SpanEditHandler(@Nonnull final Function<String, Iterable<ISymbol>> tokenizer) {
		this(tokenizer, AcceptedCharacters.Any);
	}

	public SpanEditHandler(@Nonnull final Function<String, Iterable<ISymbol>> tokenizer, @Nonnull final AcceptedCharacters accepted) {
		this(tokenizer, EnumSet.of(accepted));
	}

	public SpanEditHandler(@Nonnull final Function<String, Iterable<ISymbol>> tokenizer, @Nonnull final EnumSet<AcceptedCharacters> accepted) {
		this.tokenizer = tokenizer;
		this.acceptedCharacters = accepted;
	}

	public static SpanEditHandler createDefault() {
		return createDefault(new Function<String, Iterable<ISymbol>>() {
			@Override
			public Iterable<ISymbol> apply(@Nullable String input) {
				return Lists.newArrayList();
			}
		});
	}

	public static SpanEditHandler createDefault(@Nonnull final Function<String, Iterable<ISymbol>> tokenizer) {
		return new SpanEditHandler(tokenizer);
	}

	public EditResult applyChange(@Nonnull final Span target, @Nonnull final TextChange change) {
		return applyChange(target, change, false);
	}

	public EditResult applyChange(@Nonnull final Span target, @Nonnull final TextChange change, final boolean force) {
		// TODO
		return new EditResult();
	}

	public boolean ownsChange(@Nonnull final Span target, @Nonnull final TextChange change) {
		// TODO
		return false;
	}
}
