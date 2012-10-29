package nextmethod.web.razor.framework;

import com.google.common.collect.Lists;
import nextmethod.base.Delegates;
import nextmethod.web.razor.editor.AutoCompleteEditHandler;
import nextmethod.web.razor.editor.EditorHints;
import nextmethod.web.razor.editor.SpanEditHandler;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;

public class SpanConstructor {

	static Delegates.IFunc1<String, Iterable<ISymbol>> testTokenizer = new Delegates.IFunc1<String, Iterable<ISymbol>>() {
		@Override
		public Iterable<ISymbol> invoke(@Nullable final String input) {
			assert input != null;
			return Lists.<ISymbol>newArrayList(new RawTextSymbol(SourceLocation.Zero, input));
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

	public Span acceptsNoneAndBuild() {
		return accepts(AcceptedCharacters.None).build();
	}

	public SpanConstructor with(final ISpanCodeGenerator generator) {
		builder.setCodeGenerator(generator);
		return this;
	}

	public SpanConstructor with(final SpanEditHandler handler) {
		builder.setEditHandler(handler);
		return this;
	}

	public SpanConstructor withGenerator(final Delegates.IAction1<ISpanCodeGenerator> generatorConfigurer) {
		generatorConfigurer.invoke(builder.getCodeGenerator());
		return this;
	}

	public SpanConstructor withHandler(final Delegates.IAction1<SpanEditHandler> handlerConfigurer) {
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

	public SpanConstructor accepts(final AcceptedCharacters... accepted) {
		final EnumSet<AcceptedCharacters> s = EnumSet.noneOf(AcceptedCharacters.class);
		if (accepted != null && accepted.length > 0) {
			Collections.addAll(s, accepted);
		}
		return accepts(s);
	}

	public SpanConstructor accepts(final EnumSet<AcceptedCharacters> acceptedCharacters) {
		return this.withHandler(new Delegates.IAction1<SpanEditHandler>() {
			@Override
			public void invoke(SpanEditHandler input) {
				input.setAcceptedCharacters(acceptedCharacters);
			}
		});
	}

	public SpanConstructor autoCompleteWith(final String autoCompleteString) {
		return this.autoCompleteWith(autoCompleteString, false);
	}

	public SpanConstructor autoCompleteWith(final String autoCompleteString, final boolean atEndOfSPan) {
		final AutoCompleteEditHandler autoCompleteEditHandler = new AutoCompleteEditHandler(SpanConstructor.testTokenizer);
		autoCompleteEditHandler.setAutoCompleteString(autoCompleteString);
		autoCompleteEditHandler.setAutoCompleteAtEndOfSpan(atEndOfSPan);
		return this.with(autoCompleteEditHandler);
	}

	public SpanConstructor withEditorHints(final EditorHints hints) {
		return this.withHandler(new Delegates.IAction1<SpanEditHandler>() {
			@Override
			public void invoke(SpanEditHandler input) {
				input.setEditorHints(EnumSet.of(hints));
			}
		});
	}
}
