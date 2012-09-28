package nextmethod.web.razor.parser;

import com.google.common.collect.ImmutableSet;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.KeyValue;
import nextmethod.web.razor.editor.SpanEditHandler;
import nextmethod.web.razor.generator.MarkupCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.HtmlTokenizer;
import nextmethod.web.razor.tokenizer.TokenizerView;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import nextmethod.web.razor.tokenizer.symbols.KnownSymbolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 *
 */
abstract class HtmlMarkupParserDelegate {

	protected final HtmlMarkupParser delegate;

	protected HtmlMarkupParserDelegate(final HtmlMarkupParser delegate) {
		this.delegate = delegate;
	}

	protected final HtmlMarkupParserBlock getBlockParser() {
		return this.delegate.blockParser;
	}

	protected final HtmlMarkupParserSection getSectionParser() {
		return this.delegate.sectionParser;
	}

	protected final HtmlMarkupParserDocument getDocumentParser() {
		return this.delegate.documentParser;
	}

	protected void defaultMarkupSpan(@Nonnull final SpanBuilder span) {
		span.setCodeGenerator(new MarkupCodeGenerator());
		span.setEditHandler(new SpanEditHandler(getLanguage().createTokenizeStringDelegate(), AcceptedCharacters.Any));
	}
	protected final Delegates.IAction1<SpanBuilder> defaultMarkupSpanDelegate = new Delegates.IAction1<SpanBuilder>() {
		@Override
		public void invoke(@Nullable final SpanBuilder input) {
			if (input != null) defaultMarkupSpan(input);
		}
	};

	public void buildSpan(@Nonnull final SpanBuilder span, @Nonnull final SourceLocation start, @Nonnull final String content) {
		delegate.buildSpan(span, start, content);
	}

	public void acceptUntil(@Nonnull final HtmlSymbolType htmlSymbolType) {
		delegate.acceptUntil(htmlSymbolType);
	}

	public ParserBase getOtherParser() {
		return delegate.getOtherParser();
	}

	public void acceptUntil(@Nonnull final HtmlSymbolType type1, @Nonnull final HtmlSymbolType type2, @Nonnull final HtmlSymbolType type3) {
		delegate.acceptUntil(type1, type2, type3);
	}

	public static Delegates.IFunc1<HtmlSymbol, Boolean> isSpacingToken(final boolean includeNewLines) {
		return HtmlMarkupParser.isSpacingToken(includeNewLines);
	}

	public void acceptWhile(@Nonnull final HtmlSymbolType... htmlSymbolTypes) {
		delegate.acceptWhile(htmlSymbolTypes);
	}

	public void initialize(@Nonnull final SpanBuilder span) {
		delegate.initialize(span);
	}

	public void skipToAndParseCode(@Nonnull final HtmlSymbolType type) {
		delegate.skipToAndParseCode(type);
	}

	public void acceptUntil(@Nonnull final HtmlSymbolType type1, @Nonnull final HtmlSymbolType type2) {
		delegate.acceptUntil(type1, type2);
	}

	public boolean optional(@Nonnull final HtmlSymbolType htmlSymbolType) {
		return delegate.optional(htmlSymbolType);
	}

	public void expected(@Nonnull final KnownSymbolType type) {
		delegate.expected(type);
	}

	public void putBack(@Nullable final HtmlSymbol htmlSymbol) {
		delegate.putBack(htmlSymbol);
	}

	public boolean nextIs(@Nonnull final HtmlSymbolType htmlSymbolType) {
		return delegate.nextIs(htmlSymbolType);
	}

	public void output() {
		delegate.output();
	}

	@Nullable
	public HtmlSymbol acceptSingleWhiteSpaceCharacter() {
		return delegate.acceptSingleWhiteSpaceCharacter();
	}

	public void addMarkerSymbolIfNecessary() {
		delegate.addMarkerSymbolIfNecessary();
	}

	public boolean isAtEmbeddedTransition(final boolean allowTemplatesAndComments, final boolean allowTransitions) {
		return delegate.isAtEmbeddedTransition(allowTemplatesAndComments, allowTransitions);
	}

	public void output(@Nonnull final SpanKind kind) {
		delegate.output(kind);
	}

	public void configureSpan(@Nullable final Delegates.IAction2<SpanBuilder, Delegates.IAction1<SpanBuilder>> config) {
		delegate.configureSpan(config);
	}

	public void expected(@Nonnull final HtmlSymbolType... htmlSymbolTypes) {
		delegate.expected(htmlSymbolTypes);
	}

	public boolean isEndOfFile() {
		return delegate.isEndOfFile();
	}

	public void acceptWhile(@Nonnull final Delegates.IFunc1<HtmlSymbol, Boolean> condition) {
		delegate.acceptWhile(condition);
	}

	public boolean nextToken() {
		return delegate.nextToken();
	}

	public boolean acceptAll(@Nonnull final HtmlSymbolType... htmlSymbolTypes) {
		return delegate.acceptAll(htmlSymbolTypes);
	}

	public HtmlSymbol getCurrentSymbol() {
		return delegate.getCurrentSymbol();
	}

	public void handleEmbeddedTransition() {
		delegate.handleEmbeddedTransition();
	}

	public Iterable<HtmlSymbol> readWhile(@Nonnull final Delegates.IFunc1<HtmlSymbol, Boolean> condition) {
		return delegate.readWhile(condition);
	}

	public void outputSpanBeforeRazorComment() {
		delegate.outputSpanBeforeRazorComment();
	}

	public HtmlSymbol getPreviousSymbol() {
		return delegate.getPreviousSymbol();
	}

	public boolean optional(@Nonnull final KnownSymbolType type) {
		return delegate.optional(type);
	}

	public void acceptWhile(@Nonnull final HtmlSymbolType type1, @Nonnull final HtmlSymbolType type2) {
		delegate.acceptWhile(type1, type2);
	}

	public boolean isMarkerParser() {
		return delegate.isMarkerParser();
	}

	public LanguageCharacteristics<HtmlTokenizer, HtmlSymbol, HtmlSymbolType> getLanguage() {
		return delegate.getLanguage();
	}

	public void setSpanConfig(@Nullable final Delegates.IAction1<SpanBuilder> spanConfig) {
		delegate.setSpanConfig(spanConfig);
	}

	public ImmutableSet<String> getVoidElements() {
		return delegate.getVoidElements();
	}

	public SpanBuilder getSpan() {
		return delegate.getSpan();
	}

	public void setContext(@Nonnull final ParserContext context) {
		delegate.setContext(context);
	}

	public void setSpan(@Nonnull final SpanBuilder span) {
		delegate.setSpan(span);
	}

	public boolean nextIs(@Nonnull final Delegates.IFunc1<HtmlSymbol, Boolean> condition) {
		return delegate.nextIs(condition);
	}

	public void output(@Nonnull final EnumSet<AcceptedCharacters> accepts) {
		delegate.output(accepts);
	}

	public void skipToAndParseCode(@Nonnull final Delegates.IFunc1<HtmlSymbol, Boolean> condition) {
		delegate.skipToAndParseCode(condition);
	}

	public boolean nextIs(@Nonnull final HtmlSymbolType... htmlSymbolTypes) {
		return delegate.nextIs(htmlSymbolTypes);
	}

	public boolean required(@Nonnull final HtmlSymbolType expected, final boolean errorIfNotFound, @Nonnull final String errorBase) {
		return delegate.required(expected, errorIfNotFound, errorBase);
	}

	public boolean at(@Nonnull final HtmlSymbolType htmlSymbolType) {
		return delegate.at(htmlSymbolType);
	}

	public boolean acceptAndMoveNext() {
		return delegate.acceptAndMoveNext();
	}

	public void doAssert(@Nonnull final HtmlSymbolType expectedType) {
		delegate.doAssert(expectedType);
	}

	public IDisposable pushSpanConfig(@Nullable final Delegates.IAction1<SpanBuilder> newConfig) {
		return delegate.pushSpanConfig(newConfig);
	}

	public SourceLocation getCurrentLocation() {
		return delegate.getCurrentLocation();
	}

	public boolean balance(@Nonnull final EnumSet<BalancingModes> mode, @Nonnull final HtmlSymbolType left, @Nonnull final HtmlSymbolType right, @Nonnull final SourceLocation start) {
		return delegate.balance(mode, left, right, start);
	}

	public void accept(@Nonnull final Iterable<HtmlSymbol> htmlSymbols) {
		delegate.accept(htmlSymbols);
	}

	public void parseDocument() {
		delegate.parseDocument();
	}

	public Delegates.IAction1<SpanBuilder> getSpanConfig() {
		return delegate.getSpanConfig();
	}

	public void putCurrentBack() {
		delegate.putCurrentBack();
	}

	public void configureSpan(@Nonnull final Delegates.IAction1<SpanBuilder> config) {
		delegate.configureSpan(config);
	}

	public boolean isCurrentSymbol(@Nonnull final HtmlSymbolType htmlSymbolType) {
		return delegate.isCurrentSymbol(htmlSymbolType);
	}

	@SuppressWarnings("SimplifiableIfStatement")
	public boolean ensureCurrent() {
		return delegate.ensureCurrent();
	}

	public IDisposable pushSpanConfig(@Nullable final Delegates.IAction2<SpanBuilder, Delegates.IAction1<SpanBuilder>> newConfig) {
		return delegate.pushSpanConfig(newConfig);
	}

	public TokenizerView<HtmlTokenizer, HtmlSymbol, HtmlSymbolType> getTokenizer() {
		return delegate.getTokenizer();
	}

	public boolean was(@Nonnull final HtmlSymbolType htmlSymbolType) {
		return delegate.was(htmlSymbolType);
	}

	public HtmlSymbol acceptWhiteSpaceInLines() {
		return delegate.acceptWhiteSpaceInLines();
	}

	public void acceptUntil(@Nonnull final HtmlSymbolType... htmlSymbolTypes) {
		delegate.acceptUntil(htmlSymbolTypes);
	}

	public Iterable<HtmlSymbol> readWhileLazy(@Nonnull final Delegates.IFunc1<HtmlSymbol, Boolean> condition) {
		return delegate.readWhileLazy(condition);
	}

	public void accept(@Nullable final HtmlSymbol htmlSymbol) {
		delegate.accept(htmlSymbol);
	}

	public void razorComment() {
		delegate.razorComment();
	}

	public void putBack(@Nullable final Iterable<HtmlSymbol> htmlSymbols) {
		delegate.putBack(htmlSymbols);
	}

	public void output(@Nonnull final SpanKind kind, @Nonnull final EnumSet<AcceptedCharacters> accepts) {
		delegate.output(kind, accepts);
	}

	public ParserContext getContext() {
		return delegate.getContext();
	}

	public void acceptWhile(@Nonnull final HtmlSymbolType type1, @Nonnull final HtmlSymbolType type2, @Nonnull final HtmlSymbolType type3) {
		delegate.acceptWhile(type1, type2, type3);
	}

	public void parseSection(@Nonnull final KeyValue<String, String> nestingSequence, final boolean caseSensitive) {
		delegate.parseSection(nestingSequence, caseSensitive);
	}

	public void acceptWhile(@Nonnull final HtmlSymbolType htmlSymbolType) {
		delegate.acceptWhile(htmlSymbolType);
	}

	public boolean balance(@Nonnull final EnumSet<BalancingModes> mode) {
		return delegate.balance(mode);
	}

	public void addMarkerSymbolIfNecessary(@Nonnull final SourceLocation location) {
		delegate.addMarkerSymbolIfNecessary(location);
	}

	public IDisposable pushSpanConfig() {
		return delegate.pushSpanConfig();
	}

	public boolean atIdentifier(final boolean allowKeywords) {
		return delegate.atIdentifier(allowKeywords);
	}

	protected void otherParserBlock() {
		delegate.otherParserBlock();
	}
}
