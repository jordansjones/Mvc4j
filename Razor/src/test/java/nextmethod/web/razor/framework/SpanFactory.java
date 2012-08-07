package nextmethod.web.razor.framework;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import nextmethod.web.razor.generator.MarkupCodeGenerator;
import nextmethod.web.razor.parser.SyntaxConstants;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SeekableTextReader;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.SourceLocationTracker;
import nextmethod.web.razor.tokenizer.HtmlTokenizer;
import nextmethod.web.razor.tokenizer.ITokenizer;
import nextmethod.web.razor.tokenizer.JavaTokenizer;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class SpanFactory {

	public static SpanFactory createJavaHtml() {
		return new SpanFactory(
			new Function<ITextDocument, ITokenizer>() {

				@Override
				public ITokenizer apply(@Nullable ITextDocument input) {
					assert input != null;
					return new HtmlTokenizer(input);
				}
			},
			new Function<ITextDocument, ITokenizer>() {

				@Override
				public ITokenizer apply(@Nullable ITextDocument input) {
					assert input != null;
					return new JavaTokenizer(input);
				}
			}
		);
	}

	public Function<ITextDocument, ITokenizer> markupTokenizerFactory;
	public Function<ITextDocument, ITokenizer> codeTokenizerFactory;
	public SourceLocationTracker locationTracker;

	public SpanFactory() {
		this.locationTracker = new SourceLocationTracker();
	}

	public SpanFactory(final Function<ITextDocument, ITokenizer> markup, final Function<ITextDocument, ITokenizer> code) {
		this();
		this.markupTokenizerFactory = markup;
		this.codeTokenizerFactory = code;
	}

	public SpanConstructor span(final SpanKind kind, final String content, final JavaSymbolType type) {
		return createSymbolSpan(kind, content, new Function<SourceLocation, ISymbol>() {
			@Override
			public ISymbol apply(@Nullable SourceLocation input) {
				assert input != null;
				return new JavaSymbol(input, content, type);
			}
		});
	}

	public SpanConstructor span(final SpanKind kind, final String content, final HtmlSymbolType type) {
		return createSymbolSpan(kind, content, new Function<SourceLocation, ISymbol>() {
			@Override
			public ISymbol apply(@Nullable SourceLocation input) {
				assert input != null;
				return new HtmlSymbol(input, content, type);
			}
		});
	}

	public SpanConstructor span(final SpanKind kind, final String content, final boolean markup) {
		return span(kind, new String[] { content }, markup);
	}

	public SpanConstructor span(final SpanKind kind, final String[] content, final boolean markup) {
		return new SpanConstructor(kind, tokenize(content, markup));
	}

	public SpanConstructor span(final SpanKind kind, final ISymbol... symbols) {
		return new SpanConstructor(kind, Arrays.asList(symbols));
	}

	public void reset() {
		locationTracker.setCurrentLocation(SourceLocation.Zero);
	}

	private SpanConstructor createSymbolSpan(final SpanKind kind, final String content, final Function<SourceLocation, ISymbol> ctor) {
		final SourceLocation start = locationTracker.getCurrentLocation();
		locationTracker.updateLocation(content);
		return new SpanConstructor(kind, Lists.newArrayList(ctor.apply(start)));
	}

	private Iterable<ISymbol> tokenize(final String[] contentFragments, final boolean markup) {
		final List<ISymbol> values = Lists.newArrayList();
		for (String fragment : contentFragments) {
			Iterables.addAll(values, tokenize(fragment, markup));
		}
		return values;
	}

	private Iterable<ISymbol> tokenize(final String content, final boolean markup) {
		final List<ISymbol> values = Lists.newArrayList();

		final ITokenizer tok = makeTokenizer(markup, new SeekableTextReader(content));
		ISymbol sym;

		while ((sym = tok.nextSymbol()) != null) {
			offsetStart(sym, locationTracker.getCurrentLocation());
			values.add(sym);
		}
		locationTracker.updateLocation(content);
		return values;
	}

	private ITokenizer makeTokenizer(final boolean markup, final SeekableTextReader seekableTextReader) {
		if (markup) {
			return markupTokenizerFactory.apply(seekableTextReader);
		}
		return codeTokenizerFactory.apply(seekableTextReader);
	}

	private void offsetStart(final ISymbol sym, final SourceLocation location) {
		sym.offsetStart(location);
	}

	public Function<ITextDocument, ITokenizer> getCodeTokenizerFactory() {
		return codeTokenizerFactory;
	}

	public void setCodeTokenizerFactory(Function<ITextDocument, ITokenizer> codeTokenizerFactory) {
		this.codeTokenizerFactory = codeTokenizerFactory;
	}

	public SourceLocationTracker getLocationTracker() {
		return locationTracker;
	}

	public void setLocationTracker(SourceLocationTracker locationTracker) {
		this.locationTracker = locationTracker;
	}

	public Function<ITextDocument, ITokenizer> getMarkupTokenizerFactory() {
		return markupTokenizerFactory;
	}

	public void setMarkupTokenizerFactory(Function<ITextDocument, ITokenizer> markupTokenizerFactory) {
		this.markupTokenizerFactory = markupTokenizerFactory;
	}


	// Extensions

	public UnclassifiedCodeSpanConstructor emptyJava() {
		return new UnclassifiedCodeSpanConstructor(
			span(SpanKind.Code, new JavaSymbol(locationTracker.getCurrentLocation(), "", JavaSymbolType.Unknown))
		);
	}

	public SpanConstructor emptyHtml() {
		return span(SpanKind.Markup, new HtmlSymbol(locationTracker.getCurrentLocation(), "", HtmlSymbolType.Unknown))
			.with(new MarkupCodeGenerator());
	}

	public UnclassifiedCodeSpanConstructor code(final String content) {
		return new UnclassifiedCodeSpanConstructor(
			span(SpanKind.Code, content, false)
		);
	}

	public SpanConstructor codeTransition() {
		return span(SpanKind.Transition, SyntaxConstants.TransitionString, false).accepts(AcceptedCharacters.None);
	}

	public SpanConstructor codeTransition(final String content) {
		return span(SpanKind.Transition, content, false).accepts(AcceptedCharacters.None);
	}

	public SpanConstructor codeTransition(final JavaSymbolType type) {
		return span(SpanKind.Transition, SyntaxConstants.TransitionString, type).accepts(AcceptedCharacters.None);
	}

	public SpanConstructor codeTransition(final String content, final JavaSymbolType type) {
		return span(SpanKind.Transition, content, type).accepts(AcceptedCharacters.None);
	}

	public SpanConstructor markupTransition() {
		return span(SpanKind.Transition, SyntaxConstants.TransitionString, true).accepts(AcceptedCharacters.None);
	}

	public SpanConstructor markupTransition(final String content) {
		return span(SpanKind.Transition, content, true).accepts(AcceptedCharacters.None);
	}

	public SpanConstructor markupTransition(final HtmlSymbolType type) {
		return span(SpanKind.Transition, SyntaxConstants.TransitionString, type).accepts(AcceptedCharacters.None);
	}

	public SpanConstructor markupTransition(final String content, final HtmlSymbolType type) {
		return span(SpanKind.Transition, content, type).accepts(AcceptedCharacters.None);
	}

	public SpanConstructor metaCode(final String content) {
		return span(SpanKind.MetaCode, content, false);
	}

	public SpanConstructor metaCode(final String content, final JavaSymbolType type) {
		return span(SpanKind.MetaCode, content, type);
	}

	public SpanConstructor metaMarkup(final String content) {
		return span(SpanKind.MetaCode, content, true);
	}

	public SpanConstructor metaMarkup(final String content, final HtmlSymbolType type) {
		return span(SpanKind.MetaCode, content, type);
	}

	public SpanConstructor comment(final String content, final HtmlSymbolType type) {
		return span(SpanKind.Comment, content, type);
	}

	public SpanConstructor comment(final String content, final JavaSymbolType type) {
		return span(SpanKind.Comment, content, type);
	}

	public SpanConstructor markup(final String content) {
		return span(SpanKind.Markup, content, true).with(new MarkupCodeGenerator());
	}

	public SpanConstructor markup(final String... content) {
		return span(SpanKind.Markup, content, true).with(new MarkupCodeGenerator());
	}

}
