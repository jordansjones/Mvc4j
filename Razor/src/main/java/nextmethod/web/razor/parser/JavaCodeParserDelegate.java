package nextmethod.web.razor.parser;

import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.KeyValue;
import nextmethod.base.OutParam;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.JavaTokenizer;
import nextmethod.web.razor.tokenizer.TokenizerView;
import nextmethod.web.razor.tokenizer.symbols.JavaKeyword;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import nextmethod.web.razor.tokenizer.symbols.KnownSymbolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 */
abstract class JavaCodeParserDelegate {

	protected final JavaCodeParser delegate;

	protected JavaCodeParserDelegate(final JavaCodeParser delegate) {
		this.delegate = delegate;
	}

	private static String getSymbolName(@Nonnull final JavaSymbol sym) {
		if (sym.getType() == JavaSymbolType.Keyword && sym.getKeyword().isPresent()) {
			return JavaLanguageCharacteristics.getKeyword(sym.getKeyword().get());
		}
		return sym.getContent();
	}

	public boolean acceptIf(@Nonnull JavaKeyword keyword) {
		return delegate.acceptIf(keyword);
	}

	public SpanBuilder getSpan() {
		return delegate.getSpan();
	}

	public void nestedBlock() {
		delegate.nestedBlock();
	}

	public void acceptWhile(@Nonnull JavaSymbolType javaSymbolType) {
		delegate.acceptWhile(javaSymbolType);
	}

	public void setSpan(@Nonnull SpanBuilder span) {
		delegate.setSpan(span);
	}

	public boolean optional(@Nonnull KnownSymbolType type) {
		return delegate.optional(type);
	}

	public boolean was(@Nonnull JavaSymbolType javaSymbolType) {
		return delegate.was(javaSymbolType);
	}

	public boolean isCurrentSymbol(@Nonnull JavaSymbolType javaSymbolType) {
		return delegate.isCurrentSymbol(javaSymbolType);
	}

	public boolean nextIs(@Nonnull JavaSymbolType... javaSymbolTypes) {
		return delegate.nextIs(javaSymbolTypes);
	}

	public boolean at(@Nonnull JavaSymbolType javaSymbolType) {
		return delegate.at(javaSymbolType);
	}

	public void completeBlock(boolean insertMarkerIfNecessary, boolean captureWhitespaceToEndOfLine) {
		delegate.completeBlock(insertMarkerIfNecessary, captureWhitespaceToEndOfLine);
	}

	public void putCurrentBack() {
		delegate.putCurrentBack();
	}

	public void accept(@Nonnull Iterable<JavaSymbol> javaSymbols) {
		delegate.accept(javaSymbols);
	}

	public boolean isEndOfFile() {
		return delegate.isEndOfFile();
	}

	public JavaSymbol getCurrentSymbol() {
		return delegate.getCurrentSymbol();
	}

	public IDisposable pushSpanConfig(@Nullable Delegates.IAction1<SpanBuilder> newConfig) {
		return delegate.pushSpanConfig(newConfig);
	}

	public Set<String> getKeywords() {
		return delegate.getKeywords();
	}

	@Nullable
	public JavaSymbol acceptSingleWhiteSpaceCharacter() {
		return delegate.acceptSingleWhiteSpaceCharacter();
	}

	public IDisposable pushSpanConfig() {
		return delegate.pushSpanConfig();
	}

	public void acceptUntil(@Nonnull JavaSymbolType type1, @Nonnull JavaSymbolType type2, @Nonnull JavaSymbolType type3) {
		delegate.acceptUntil(type1, type2, type3);
	}

	public void accept(@Nullable JavaSymbol javaSymbol) {
		delegate.accept(javaSymbol);
	}

	public void parseSection(@Nonnull KeyValue<String, String> nestingSequence, boolean caseSensitive) {
		delegate.parseSection(nestingSequence, caseSensitive);
	}

	public boolean isNested() {
		return delegate.isNested();
	}

	public void acceptWhile(@Nonnull JavaSymbolType type1, @Nonnull JavaSymbolType type2) {
		delegate.acceptWhile(type1, type2);
	}

	public void buildSpan(@Nonnull SpanBuilder span, @Nonnull SourceLocation start, @Nonnull String content) {
		delegate.buildSpan(span, start, content);
	}

	public void acceptUntil(@Nonnull JavaSymbolType... javaSymbolTypes) {
		delegate.acceptUntil(javaSymbolTypes);
	}

	public void output(@Nonnull SpanKind kind, @Nonnull EnumSet<AcceptedCharacters> accepts) {
		delegate.output(kind, accepts);
	}

	public boolean acceptAll(@Nonnull JavaSymbolType... javaSymbolTypes) {
		return delegate.acceptAll(javaSymbolTypes);
	}

	public boolean balance(@Nonnull final BalancingModes mode, @Nonnull JavaSymbolType left, @Nonnull JavaSymbolType right, @Nonnull SourceLocation start) {
		return balance(EnumSet.of(mode), left, right, start);
	}

	public boolean balance(@Nonnull EnumSet<BalancingModes> mode, @Nonnull JavaSymbolType left, @Nonnull JavaSymbolType right, @Nonnull SourceLocation start) {
		return delegate.balance(mode, left, right, start);
	}

	public void output(@Nonnull SpanKind kind) {
		delegate.output(kind);
	}

	public Delegates.IAction1<SpanBuilder> getSpanConfig() {
		return delegate.getSpanConfig();
	}

	public static Delegates.IFunc1<JavaSymbol, Boolean> isSpacingToken(boolean includeNewLines, boolean includeComments) {
		return JavaCodeParser.isSpacingToken(includeNewLines, includeComments);
	}

	public boolean tryGetDirectiveHandler(String directive, @Nonnull OutParam<Delegates.IAction> handler) {
		return delegate.tryGetDirectiveHandler(directive, handler);
	}

	public void configureSpan(@Nullable Delegates.IAction2<SpanBuilder, Delegates.IAction1<SpanBuilder>> config) {
		delegate.configureSpan(config);
	}

	public TokenizerView<JavaTokenizer, JavaSymbol, JavaSymbolType> getTokenizer() {
		return delegate.getTokenizer();
	}

	public void initialize(@Nonnull SpanBuilder span) {
		delegate.initialize(span);
	}

	public void acceptWhile(@Nonnull JavaSymbolType type1, @Nonnull JavaSymbolType type2, @Nonnull JavaSymbolType type3) {
		delegate.acceptWhile(type1, type2, type3);
	}

	public void putBack(@Nullable Iterable<JavaSymbol> javaSymbols) {
		delegate.putBack(javaSymbols);
	}

	public Delegates.IAction createParseDocumentDelegate() {
		return delegate.createParseDocumentDelegate();
	}

	public JavaSymbol acceptWhiteSpaceInLines() {
		return delegate.acceptWhiteSpaceInLines();
	}

	public void outputSpanBeforeRazorComment() {
		delegate.outputSpanBeforeRazorComment();
	}

	@SuppressWarnings("SimplifiableIfStatement")
	public boolean ensureCurrent() {
		return delegate.ensureCurrent();
	}

	public void addMarkerSymbolIfNecessary() {
		delegate.addMarkerSymbolIfNecessary();
	}

	public JavaSymbol getPreviousSymbol() {
		return delegate.getPreviousSymbol();
	}

	public Iterable<JavaSymbol> readWhile(@Nonnull Delegates.IFunc1<JavaSymbol, Boolean> condition) {
		return delegate.readWhile(condition);
	}

	public void acceptUntil(@Nonnull JavaSymbolType type1, @Nonnull JavaSymbolType type2) {
		delegate.acceptUntil(type1, type2);
	}

	public void putBack(@Nullable JavaSymbol javaSymbol) {
		delegate.putBack(javaSymbol);
	}

	public boolean at(@Nonnull JavaKeyword keyword) {
		return delegate.at(keyword);
	}

	public void razorComment() {
		delegate.razorComment();
	}

	public void acceptWhile(@Nonnull Delegates.IFunc1<JavaSymbol, Boolean> condition) {
		delegate.acceptWhile(condition);
	}

	public void parseDocument() {
		delegate.parseDocument();
	}

	public boolean nextIs(@Nonnull Delegates.IFunc1<JavaSymbol, Boolean> condition) {
		return delegate.nextIs(condition);
	}

	public boolean isAtEmbeddedTransition(boolean allowTemplatesAndComments, boolean allowTransitions) {
		return delegate.isAtEmbeddedTransition(allowTemplatesAndComments, allowTransitions);
	}

	public void otherParserBlock() {
		delegate.otherParserBlock();
	}

	public boolean balance(@Nonnull EnumSet<BalancingModes> mode) {
		return delegate.balance(mode);
	}

	public ParserBase getOtherParser() {
		return delegate.getOtherParser();
	}

	public void parseBlock() {
		delegate.parseBlock();
	}

	public void setContext(@Nonnull ParserContext context) {
		delegate.setContext(context);
	}

	public void expected(@Nonnull JavaSymbolType... javaSymbolTypes) {
		delegate.expected(javaSymbolTypes);
	}

	public void completeBlock(boolean insertMarkerIfNecessary) {
		delegate.completeBlock(insertMarkerIfNecessary);
	}

	public void acceptWhile(@Nonnull JavaSymbolType... javaSymbolTypes) {
		delegate.acceptWhile(javaSymbolTypes);
	}

	public boolean atIdentifier(boolean allowKeywords) {
		return delegate.atIdentifier(allowKeywords);
	}

	public void acceptUntil(@Nonnull JavaSymbolType javaSymbolType) {
		delegate.acceptUntil(javaSymbolType);
	}

	public SourceLocation getCurrentLocation() {
		return delegate.getCurrentLocation();
	}

	public void template() {
		delegate.template();
	}

	public void expected(@Nonnull KnownSymbolType type) {
		delegate.expected(type);
	}

	public void implicitExpression() {
		delegate.implicitExpression();
	}

	public boolean nextIs(@Nonnull JavaSymbolType javaSymbolType) {
		return delegate.nextIs(javaSymbolType);
	}

	public void handleEmbeddedTransition() {
		delegate.handleEmbeddedTransition();
	}

	public LanguageCharacteristics<JavaTokenizer, JavaSymbol, JavaSymbolType> getLanguage() {
		return delegate.getLanguage();
	}

	public void setSpanConfig(@Nullable Delegates.IAction1<SpanBuilder> spanConfig) {
		delegate.setSpanConfig(spanConfig);
	}

	public boolean required(@Nonnull JavaSymbolType expected, boolean errorIfNotFound, @Nonnull String errorBase) {
		return delegate.required(expected, errorIfNotFound, errorBase);
	}

	public void completeBlock() {
		delegate.completeBlock();
	}

	public boolean acceptAndMoveNext() {
		return delegate.acceptAndMoveNext();
	}

	public boolean optional(@Nonnull JavaSymbolType javaSymbolType) {
		return delegate.optional(javaSymbolType);
	}

	public void output() {
		delegate.output();
	}

	public Delegates.IAction createParseBlockDelegate() {
		return delegate.createParseBlockDelegate();
	}

	public ParserContext getContext() {
		return delegate.getContext();
	}

	public void doAssert(@Nonnull JavaSymbolType expectedType) {
		delegate.doAssert(expectedType);
	}

	public Iterable<JavaSymbol> readWhileLazy(@Nonnull Delegates.IFunc1<JavaSymbol, Boolean> condition) {
		return delegate.readWhileLazy(condition);
	}

	public void configureSpan(@Nonnull Delegates.IAction1<SpanBuilder> config) {
		delegate.configureSpan(config);
	}

	public void output(@Nonnull EnumSet<AcceptedCharacters> accepts) {
		delegate.output(accepts);
	}

	public IDisposable pushSpanConfig(@Nullable Delegates.IAction2<SpanBuilder, Delegates.IAction1<SpanBuilder>> newConfig) {
		return delegate.pushSpanConfig(newConfig);
	}

	public void addMarkerSymbolIfNecessary(@Nonnull SourceLocation location) {
		delegate.addMarkerSymbolIfNecessary(location);
	}

	public boolean isMarkerParser() {
		return delegate.isMarkerParser();
	}

	public void doAssert(@Nonnull JavaKeyword expectedKeyword) {
		delegate.doAssert(expectedKeyword);
	}

	public boolean nextToken() {
		return delegate.nextToken();
	}

	void sectionBlock(@Nonnull String left, @Nonnull String right, boolean caseSensitive) {
		delegate.sectionBlock(left, right, caseSensitive);
	}

	static class Block {
		private String name;
		private SourceLocation start;

		public Block(@Nonnull final String name, @Nonnull final SourceLocation start) {
			this.name = name;
			this.start = start;
		}

		public Block(@Nonnull final JavaSymbol symbol) {
			this(getSymbolName(symbol), symbol.getStart());
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public SourceLocation getStart() {
			return start;
		}

	}
}
