package nextmethod.web.razor.parser;

import com.google.common.collect.Iterables;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.generator.AddImportCodeGenerator;
import nextmethod.web.razor.generator.ExpressionCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaKeyword;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import nextmethod.web.razor.tokenizer.symbols.SymbolExtensions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

// TODO
final class JavaCodeParserStatements {

	private final JavaCodeParser parser;

	JavaCodeParserStatements(@Nonnull final JavaCodeParser parser) {
		this.parser = parser;
		setupKeywords();
	}

	private void setupKeywords() {
		mapKeywords(conditionalBlockDelegate, JavaKeyword.For, JavaKeyword.Foreach, JavaKeyword.While, JavaKeyword.Switch, JavaKeyword.Lock);
		mapKeywords(caseStatementDelegate, false, JavaKeyword.Case, JavaKeyword.Default);
		mapKeywords(ifStatementDelegate, JavaKeyword.If);
		mapKeywords(tryStatementDelegate, JavaKeyword.Try);
		mapKeywords(usingKeywordDelegate, JavaKeyword.Using);
		mapKeywords(doStatementDelegate, JavaKeyword.Do);
		mapKeywords(reservedDirectiveDelegate, JavaKeyword.Package, JavaKeyword.Class);
	}

	private void mapKeywords(@Nonnull final Delegates.IAction1<Boolean> handler, final JavaKeyword... keywords) {
		mapKeywords(handler, true, keywords);
	}

	private void mapKeywords(@Nonnull final Delegates.IAction1<Boolean> handler, boolean topLevel, final JavaKeyword... keywords) {
		for (JavaKeyword keyword : keywords) {
			parser.keywordParsers.put(keyword, handler);
			if (topLevel) {
				parser.keywords.add(JavaLanguageCharacteristics.getKeyword(keyword));
			}
		}
	}

	protected void reservedDirective(final boolean topLevel) {
		parser.getContext().onError(parser.getCurrentLocation(), String.format(RazorResources().getString("parseError.reservedWord"), parser.getCurrentSymbol().getContent()));
		parser.acceptAndMoveNext();
		final SpanBuilder span = parser.getSpan();
		span.getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
		span.setCodeGenerator(SpanCodeGenerator.Null);
		parser.getContext().getCurrentBlock().setType(BlockType.Directive);
		parser.completeBlock();
		parser.output(SpanKind.MetaCode);
	}
	protected final Delegates.IAction1<Boolean> reservedDirectiveDelegate = new Delegates.IAction1<Boolean>() {
		@Override
		public void invoke(@Nullable final Boolean input) {
			if (input != null) reservedDirective(input);
		}
	};

	protected void keywordBlock(final boolean topLevel) {
		handleKeyword(topLevel, new Delegates.IAction1<Boolean>() {
			@Override
			public void invoke(@Nullable final Boolean input) {
				if (input != null) {
					final BlockBuilder currentBlock = parser.getContext().getCurrentBlock();
					currentBlock.setType(BlockType.Expression);
					currentBlock.setCodeGenerator(new ExpressionCodeGenerator());
					parser.implicitExpression();
				}
			}
		});
	}

	protected void caseStatement(final boolean topLevel) {
		parser.doAssert(JavaSymbolType.Keyword);
		if (Debug.isAssertEnabled()) {
			final JavaSymbol currentSymbol = parser.getCurrentSymbol();
			assert currentSymbol.getKeyword().isPresent()
				&& (currentSymbol.getKeyword().get() == JavaKeyword.Case
					|| currentSymbol.getKeyword().get() == JavaKeyword.Default);
		}
		parser.acceptUntil(JavaSymbolType.Colon);
		parser.optional(JavaSymbolType.Colon);
	}
	protected final Delegates.IAction1<Boolean> caseStatementDelegate = new Delegates.IAction1<Boolean>() {
		@Override
		public void invoke(@Nullable final Boolean input) {
			if (input != null) caseStatement(input);
		}
	};

	protected void doStatement(final boolean topLevel) {
		parser.doAssert(JavaKeyword.Do);
		unconditionalBlock();
		whileClause();
		if (topLevel) {
			parser.completeBlock();
		}
	}
	protected final Delegates.IAction1<Boolean> doStatementDelegate = new Delegates.IAction1<Boolean>() {
		@Override
		public void invoke(@Nullable final Boolean input) {
			if (input != null) doStatement(input);
		}
	};

	protected void whileClause() {
		parser.getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
		final Iterable<JavaSymbol> ws = skipToNextImportantToken();
		if (parser.at(JavaKeyword.While)) {
			parser.accept(ws);
			parser.doAssert(JavaKeyword.While);
			parser.acceptAndMoveNext();
			parser.acceptWhile(JavaCodeParser.isSpacingToken(true, true));
			if (acceptCondition() && parser.optional(JavaSymbolType.Semicolon)) {
				parser.getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
			}
		}
		else {
			parser.putCurrentBack();
			parser.putBack(ws);
		}
	}

	protected void usingKeyword(final boolean topLevel) {
		parser.doAssert(JavaKeyword.Using);
		final Block block = new Block(parser.getCurrentSymbol());
		parser.acceptAndMoveNext();
		parser.acceptWhile(JavaCodeParser.isSpacingToken(false, true));

		if (parser.at(JavaSymbolType.LeftParenthesis)) {
			// using ( ==> Using Statement
			usingStatement(block);
		}
		else if (parser.at(JavaSymbolType.Identifier)) {
			// using Identifier ==> Using Declaration
			if (!topLevel) {
				parser.getContext().onError(block.start, RazorResources().getString("parseError.packageImportAndTypeAlias.cannot.exist.within.codeBlock"));
				standardStatement();
			}
			else {
				usingDeclaration();
			}
		}

		if (topLevel) {
			parser.completeBlock();
		}
	}
	protected final Delegates.IAction1<Boolean> usingKeywordDelegate = new Delegates.IAction1<Boolean>() {
		@Override
		public void invoke(@Nullable final Boolean input) {
			if (input != null) usingKeyword(input);
		}
	};

	protected void usingDeclaration() {
		// Set block type to directive
		parser.getContext().getCurrentBlock().setType(BlockType.Directive);

		// Parse a type name
		parser.doAssert(JavaSymbolType.Identifier);
		packageOrTypeName();
		final Iterable<JavaSymbol> ws = parser.readWhile(JavaCodeParser.isSpacingToken(true, true));
		if (parser.at(JavaSymbolType.Assign)) {
			// Alias
			parser.accept(ws);
			parser.doAssert(JavaSymbolType.Assign);
			parser.acceptAndMoveNext();

			parser.acceptWhile(JavaCodeParser.isSpacingToken(true, true));

			// One more package or type name
			packageOrTypeName();
		}
		else {
			parser.putCurrentBack();
			parser.putBack(ws);
		}

		final SpanBuilder span = parser.getSpan();
		span.getEditHandler().setAcceptedCharacters(AcceptedCharacters.AnyExceptNewLine);
		span.setCodeGenerator(
			new AddImportCodeGenerator(
				SymbolExtensions.getContent(span, new Delegates.IFunc1<Iterable<ISymbol>, Iterable<ISymbol>>() {
					@Override
					public Iterable<ISymbol> invoke(@Nullable final Iterable<ISymbol> input1) {
						if (input1 == null) return null;
						return Iterables.skip(input1, 1);
					}
				}).toString(),
				SyntaxConstants.Java.UsingKeywordLength
			)
		);

		// Optional ";"
		if (parser.ensureCurrent()) {
			parser.optional(JavaSymbolType.Semicolon);
		}
	}

	protected boolean packageOrTypeName() {
		if (parser.optional(JavaSymbolType.Identifier) || parser.optional(JavaSymbolType.Keyword)) {
			parser.optional(JavaSymbolType.QuestionMark); // Nullable
			if (parser.optional(JavaSymbolType.DoubleColon)) {
				if (!parser.optional(JavaSymbolType.Identifier)) {
					parser.optional(JavaSymbolType.Keyword);
				}
			}
			if (parser.at(JavaSymbolType.LessThan)) {
				typeArgumentList();
			}
			if (parser.optional(JavaSymbolType.Dot)) {
				packageOrTypeName();
			}
			while(parser.at(JavaSymbolType.LeftBracket)) {
				parser.balance(BalancingModes.SetOfNone);
				parser.optional(JavaSymbolType.RightBracket);
			}
			return true;
		}
		return false;
	}

	protected void typeArgumentList() {
		parser.doAssert(JavaSymbolType.LessThan);
		parser.balance(BalancingModes.SetOfNone);
		parser.optional(JavaSymbolType.GreaterThan);
	}

	protected void usingStatement(@Nonnull final Block block) {
		parser.doAssert(JavaSymbolType.LeftParenthesis);

		// Parse condition
		if (acceptCondition()) {
			parser.acceptWhile(JavaCodeParser.isSpacingToken(true, true));

			// Parse Code block
			expectCodeBlock(block);
		}
	}

	protected void tryStatement(final boolean topLevel) {
		parser.doAssert(JavaKeyword.Try);
		unconditionalBlock();
		afterTryClause();
		if (topLevel) {
			parser.completeBlock();
		}
	}
	protected final Delegates.IAction1<Boolean> tryStatementDelegate = new Delegates.IAction1<Boolean>() {
		@Override
		public void invoke(@Nullable final Boolean input) {
			if (input != null) tryStatement(input);
		}
	};

	protected void ifStatement(final boolean topLevel) {
		parser.doAssert(JavaKeyword.If);
		conditionalBlock(false);
		afterIfClause();
		if (topLevel) {
			parser.completeBlock();
		}
	}
	protected final Delegates.IAction1<Boolean> ifStatementDelegate = new Delegates.IAction1<Boolean>() {
		@Override
		public void invoke(@Nullable final Boolean input) {
			if (input != null) ifStatement(input);
		}
	};

	protected void afterTryClause() {

	}

	protected void afterIfClause() {

	}

	protected void elseClause() {

	}

	protected void expectCodeBlock(@Nonnull final Block block) {

	}

	protected void unconditionalBlock() {

	}

	protected void conditionalBlock(final boolean topLevel) {

	}
	protected final Delegates.IAction1<Boolean> conditionalBlockDelegate = new Delegates.IAction1<Boolean>() {
		@Override
		public void invoke(@Nullable final Boolean input) {
			if (input != null) conditionalBlock(input);
		}
	};

	protected void conditionalBlock(@Nonnull final Block block) {

	}

	protected boolean acceptCondition() {
		throw new NotImplementedException();
	}

	protected void statement() {
		statement(null);
	}

	protected void statement(@Nullable final Block block) {

	}

	protected void embeddedExpression() {

	}

	protected void standardStatement() {

	}

	protected void codeBlock(@Nonnull final Block block) {

	}

	protected void codeBlock(final boolean acceptTerminatingBrace, @Nonnull final Block block) {

	}

	protected void handleKeyword(final boolean topLevel, @Nonnull final Delegates.IAction1<Boolean> fallback) {

	}

	protected Iterable<JavaSymbol> skipToNextImportantToken() {
		return null;
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

		public void setStart(final SourceLocation start) {
			this.start = start;
		}

	}

	private static String getSymbolName(@Nonnull final JavaSymbol sym) {
		if (sym.getType() == JavaSymbolType.Keyword && sym.getKeyword().isPresent()) {
			return JavaLanguageCharacteristics.getKeyword(sym.getKeyword().get());
		}
		return sym.getContent();
	}

}
