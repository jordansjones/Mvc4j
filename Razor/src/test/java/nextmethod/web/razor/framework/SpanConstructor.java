package nextmethod.web.razor.framework;

import com.google.common.base.Function;
import nextmethod.base.IVoidAction;
import nextmethod.web.razor.editor.AutoCompleteEditHandler;
import nextmethod.web.razor.editor.EditorHints;
import nextmethod.web.razor.editor.SpanEditHandler;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SpanConstructor {

	static Function<String, Iterable<ISymbol>> testTokenizer = new Function<String, Iterable<ISymbol>>() {
		@Override
		public Iterable<ISymbol> apply(@Nullable String input) {
//			return new RawTextSymbol();
			return null;
		}
	};

	public SpanBuilder builder;

	public SpanConstructor(final SpanKind kind, final Iterable<ISymbol> symbols) {
		builder = new SpanBuilder().setKind(kind).setEditHandler(SpanEditHandler.createDefault(testTokenizer));
		for (ISymbol symbol : symbols) {
			builder.accept(symbol);
		}
	}

	public Span build() {
		return builder.build();
	}

	public SpanConstructor with(final ISpanCodeGenerator generator) {
		builder.setCodeGenerator(generator);
		return this;
	}

	public SpanConstructor with(final SpanEditHandler handler) {
		builder.setEditHandler(handler);
		return this;
	}

	public SpanConstructor withGenerator(final IVoidAction<ISpanCodeGenerator> generatorConfigurer) {
		generatorConfigurer.invoke(builder.getCodeGenerator());
		return this;
	}

	public SpanConstructor withHandler(final IVoidAction<SpanEditHandler> handlerConfigurer) {
		handlerConfigurer.invoke(builder.getEditHandler());
		return this;
	}

	public SpanConstructor hidden() {
		builder.setCodeGenerator(SpanCodeGenerator.Null);
		return this;
	}

	public SpanBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(SpanBuilder builder) {
		this.builder = builder;
	}

	public SpanConstructor accepts(final AcceptedCharacters accepted) {
		return this.withHandler(new IVoidAction<SpanEditHandler>() {
			@Override
			public void invoke(SpanEditHandler input) {
				input.setAcceptedCharacters(EnumSet.of(accepted));
			}
		});
	}

	public SpanConstructor autoCompleteWith(final String autoCompleteString) {
		return this.autoCompleteWith(autoCompleteString, true);
	}

	public SpanConstructor autoCompleteWith(final String autoCompleteString, final boolean atEndOfSPan) {
		final AutoCompleteEditHandler autoCompleteEditHandler = new AutoCompleteEditHandler(SpanConstructor.testTokenizer);
		autoCompleteEditHandler.setAutoCompleteString(autoCompleteString);
		autoCompleteEditHandler.setAutoCompleteAtEndOfSpan(atEndOfSPan);
		return this.with(autoCompleteEditHandler);
	}

	public SpanConstructor withEditorHints(final EditorHints hints) {
		return this.withHandler(new IVoidAction<SpanEditHandler>() {
			@Override
			public void invoke(SpanEditHandler input) {
				input.setEditorHints(EnumSet.of(hints));
			}
		});
	}
}
