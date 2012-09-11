package nextmethod.web.razor.parser;

import com.google.common.collect.Iterables;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
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
import java.util.Collections;
import java.util.EnumSet;

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
		handleKeyword(topLevel, new Delegates.IAction() {
			@Override
			public void invoke() {
				final BlockBuilder currentBlock = parser.getContext().getCurrentBlock();
				currentBlock.setType(BlockType.Expression);
				currentBlock.setCodeGenerator(new ExpressionCodeGenerator());
				parser.implicitExpression();
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
		// Grab whitespace
		final Iterable<JavaSymbol> ws = skipToNextImportantToken();

		// Check for a catch or finally part
		if (parser.at(JavaKeyword.Catch)) {
			parser.accept(ws);
			parser.doAssert(JavaKeyword.Catch);
			conditionalBlock(true);
			afterTryClause();
		}
		else if (parser.at(JavaKeyword.Finally)) {
			parser.accept(ws);
			parser.doAssert(JavaKeyword.Finally);
			unconditionalBlock();
		}
		else {
			// Return whitespace and end the block
			parser.putCurrentBack();
			parser.putBack(ws);
			parser.getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
		}
	}

	protected void afterIfClause() {
		// Grab whitespace and razor comments
		final Iterable<JavaSymbol> ws = skipToNextImportantToken();

		// Check for an else part
		if (parser.at(JavaKeyword.Else)) {
			parser.accept(ws);
			parser.doAssert(JavaKeyword.Else);
			elseClause();
		}
		else {
			// No else, return whitespace
			parser.putCurrentBack();
			parser.putBack(ws);
			parser.getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
		}
	}

	protected void elseClause() {
		if (!parser.at(JavaKeyword.Else)) {
			return;
		}

		final Block block = new Block(parser.getCurrentSymbol());

		parser.acceptAndMoveNext();
		parser.acceptWhile(JavaCodeParser.isSpacingToken(true, true));
		if (parser.at(JavaKeyword.If)) {
			// ElseIf
			block.setName(SyntaxConstants.Java.ElseIfKeyword);
			conditionalBlock(block);
			afterIfClause();
		}
		else if (!parser.isEndOfFile()) {
			// Else
			expectCodeBlock(block);
		}
	}

	protected void expectCodeBlock(@Nonnull final Block block) {
		if (!parser.isEndOfFile()) {
			// Check for "{" to make sure we're at a block
			if (!parser.at(JavaSymbolType.LeftBrace)) {
				parser.getContext().onError(
					parser.getCurrentLocation(),
					RazorResources().getString("parseError.singleLine.controlFlowStatements.not.allowed"),
					parser.getLanguage().getSample(JavaSymbolType.LeftBrace),
					parser.getCurrentSymbol().getContent()
				);
			}

			// Parse the statement and then we're done
			statement(block);
		}
	}

	protected void unconditionalBlock() {
		parser.doAssert(JavaSymbolType.Keyword);
		final Block block = new Block(parser.getCurrentSymbol());
		parser.acceptAndMoveNext();
		parser.acceptWhile(JavaCodeParser.isSpacingToken(true, true));
		expectCodeBlock(block);
	}

	protected void conditionalBlock(final boolean topLevel) {
		parser.doAssert(JavaSymbolType.Keyword);
		final Block block = new Block(parser.getCurrentSymbol());
		conditionalBlock(block);
		if (topLevel) {
			parser.completeBlock();
		}
	}
	protected final Delegates.IAction1<Boolean> conditionalBlockDelegate = new Delegates.IAction1<Boolean>() {
		@Override
		public void invoke(@Nullable final Boolean input) {
			if (input != null) conditionalBlock(input);
		}
	};

	protected void conditionalBlock(@Nonnull final Block block) {
		parser.acceptAndMoveNext();
		parser.acceptWhile(JavaCodeParser.isSpacingToken(true, true));

		// Parse the condition, if present (if not preset, we'll let the java compiler complain)
		if (acceptCondition()) {
			parser.acceptWhile(JavaCodeParser.isSpacingToken(true, true));
			expectCodeBlock(block);
		}
	}

	protected boolean acceptCondition() {
		if (parser.at(JavaSymbolType.LeftParenthesis)) {
			final boolean complete = parser.balance(EnumSet.of(BalancingModes.BacktrackOnFailure, BalancingModes.AllowCommentsAndTemplates));
			if (!complete) {
				parser.acceptUntil(JavaSymbolType.NewLine);
			}
			else {
				parser.optional(JavaSymbolType.RightParenthesis);
			}
			return complete;
		}
		return true;
	}

	protected void statement() {
		statement(null);
	}

	protected void statement(@Nullable final Block block) {
		parser.getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);

		// Accept whitespace but always keep the last whitespace node so we can put it back if necessary
		final JavaSymbol lastWs = parser.acceptWhiteSpaceInLines();
		if (Debug.isAssertEnabled()) {
			assert lastWs == null || (lastWs.getStart().getAbsoluteIndex() + lastWs.getContent().length() == parser.getCurrentLocation().getAbsoluteIndex());
		}

		if (parser.isEndOfFile()) {
			if (lastWs != null) {
				parser.accept(lastWs);
			}
			return;
		}

		final JavaSymbolType type = parser.getCurrentSymbol().getType();
		final SourceLocation loc = parser.getCurrentLocation();
		final boolean isSingleLineMarkup = type == JavaSymbolType.Transition && parser.nextIs(JavaSymbolType.Colon);
		final boolean isMarkup = isSingleLineMarkup
			|| type == JavaSymbolType.LessThan
			|| (type == JavaSymbolType.Transition && parser.nextIs(JavaSymbolType.LessThan));

		if (parser.getContext().isDesignTimeMode() || !isMarkup) {
			// CODE owns whitespace, MARKUP owns it ONLY in DesignTimeMode
			if (lastWs != null) {
				parser.accept(lastWs);
			}
		}
		else {
			// MARKUP owns whitespace EXCEPT in DesignTimeMode.
			parser.putCurrentBack();
			parser.putBack(lastWs);
		}

		if (isMarkup) {
			if (type == JavaSymbolType.Transition && !isSingleLineMarkup) {
				parser.getContext().onError(loc, RazorResources().getString("parseError.atInCode.must.be.followed.by.colon.paren.or.identifier.start"));
			}

			// Markup block
			parser.output(SpanKind.Code);
			if (parser.getContext().isDesignTimeMode() && parser.getCurrentSymbol() != null && (parser.getCurrentSymbol().getType() == JavaSymbolType.LessThan || parser.getCurrentSymbol().getType() == JavaSymbolType.Transition)) {
				parser.putCurrentBack();
			}
			parser.otherParserBlock();
		}
		else {
			// What kind of statement is this?
			handleStatement(block, type);
		}
	}

	protected void handleStatement(@Nullable final Block block, @Nonnull final JavaSymbolType type) {
		switch (type) {
			case RazorCommentTransition:
				parser.output(SpanKind.Code);
				parser.razorComment();
				statement(block);
				break;

			case LeftBrace:
				// Verbatim Block
				parser.acceptAndMoveNext();
				codeBlock(block != null ? block : new Block(RazorResources().getString("blockName.code"), parser.getCurrentLocation()));
				break;

			case Keyword:
				// Keyword block
				handleKeyword(false, standardStatementDelegate);
				break;

			case Transition:
				// Embedded Expression block
				embeddedExpression();
				break;

			case RightBrace:
				// Possible end of Code Block, just run the continuation
				break;

			case Comment:
				parser.acceptAndMoveNext();
				break;

			default:
				// Other statement
				standardStatement();
				break;
		}
	}

	protected void embeddedExpression() {
		// First, verify the type of the block
		parser.doAssert(JavaSymbolType.Transition);
		final JavaSymbol transition = parser.getCurrentSymbol();
		parser.nextToken();

		if (parser.at(JavaSymbolType.Transition)) {
			// Escaped "@"
			parser.output(SpanKind.Code);

			// Output "@" as hidden span
			parser.accept(transition);
			parser.getSpan().setCodeGenerator(SpanCodeGenerator.Null);
			parser.output(SpanKind.Code);

			parser.doAssert(JavaSymbolType.Transition);
			parser.acceptAndMoveNext();
			standardStatement();
		}
		else {
			// Throw errors as necessary, but continue parsing
			if (parser.at(JavaSymbolType.Keyword)) {
				parser.getContext().onError(
					parser.getCurrentLocation(),
					RazorResources().getString("parseError.unexpected.keyword.after.at"),
					JavaLanguageCharacteristics.getKeyword(parser.getCurrentSymbol().getKeyword().get())
				);
			}
			else if (parser.at(JavaSymbolType.LeftBrace)) {
				parser.getContext().onError(
					parser.getCurrentLocation(),
					RazorResources().getString("parseError.unexpected.nested.codeBlock")
				);
			}

			// @( or @foo - Nested expression, parser a child block
			parser.putCurrentBack();
			parser.putBack(transition);

			// Before exiting, add a marker span if necessary
			parser.addMarkerSymbolIfNecessary();

			parser.nestedBlock();
		}
	}

	protected void standardStatement() {
		while (!parser.isEndOfFile()) {
			final int bookmark = parser.getCurrentLocation().getAbsoluteIndex();
			final Iterable<JavaSymbol> read = parser.readWhile(new Delegates.IFunc1<JavaSymbol, Boolean>() {
				@Override
				public Boolean invoke(@Nullable final JavaSymbol sym) {
					return sym != null
						&& sym.getType() != JavaSymbolType.Semicolon
						&& sym.getType() != JavaSymbolType.RazorCommentTransition
						&& sym.getType() != JavaSymbolType.Transition
						&& sym.getType() != JavaSymbolType.LeftBrace
						&& sym.getType() != JavaSymbolType.LeftParenthesis
						&& sym.getType() != JavaSymbolType.LeftBracket
						&& sym.getType() != JavaSymbolType.RightBrace;
				}
			});

			if (parser.at(JavaSymbolType.LeftBrace) || parser.at(JavaSymbolType.LeftParenthesis) || parser.at(JavaSymbolType.LeftBracket)) {
				parser.accept(read);
				if (parser.balance(EnumSet.of(BalancingModes.AllowCommentsAndTemplates, BalancingModes.BacktrackOnFailure))) {
					parser.optional(JavaSymbolType.RightBrace);
				}
				else {
					// Recovery
					parser.acceptUntil(JavaSymbolType.LessThan, JavaSymbolType.RightBrace);
					return;
				}
			}
			else if (parser.at(JavaSymbolType.Transition) && (parser.nextIs(JavaSymbolType.LessThan, JavaSymbolType.Colon))) {
				parser.accept(read);
				parser.output(SpanKind.Code);
				parser.template();
			}
			else if (parser.at(JavaSymbolType.RazorCommentTransition)) {
				parser.accept(read);
				parser.razorComment();
			}
			else if (parser.at(JavaSymbolType.Semicolon)) {
				parser.accept(read);
				parser.acceptAndMoveNext();
				return;
			}
			else if (parser.at(JavaSymbolType.RightBrace)) {
				parser.accept(read);
				return;
			}
			else {
				parser.getContext().getSource().setPosition(bookmark);
				parser.nextToken();
				parser.acceptUntil(JavaSymbolType.LessThan, JavaSymbolType.RightBrace);
			}
		}
	}
	protected final Delegates.IAction standardStatementDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { standardStatement(); }
	};

	protected void codeBlock(@Nonnull final Block block) {
		codeBlock(true, block);
	}

	protected void codeBlock(final boolean acceptTerminatingBrace, @Nonnull final Block block) {
		parser.ensureCurrent();
		while (!parser.isEndOfFile() && !parser.at(JavaSymbolType.RightBrace)) {
			// Parse a statement, then return here
			statement();
			parser.ensureCurrent();
		}

		if (!parser.isEndOfFile()) {
			parser.getContext().onError(block.getStart(), RazorResources().getString("parseError.expected.endOfBlock.before.eof"), '}', '{');
		}
		else if (acceptTerminatingBrace) {
			parser.doAssert(JavaSymbolType.RightBrace);
			parser.getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
			parser.acceptAndMoveNext();
		}
	}

	protected void handleKeyword(final boolean topLevel, @Nonnull final Delegates.IAction fallback) {
		if (Debug.isAssertEnabled())
			assert (parser.getCurrentSymbol().getType() == JavaSymbolType.Keyword && parser.getCurrentSymbol().getKeyword().isPresent());

		if (parser.getCurrentSymbol().getKeyword().isPresent() && parser.keywordParsers.containsKey(parser.getCurrentSymbol().getKeyword().get())) {
			final Delegates.IAction1<Boolean> handler = parser.keywordParsers.get(parser.getCurrentSymbol().getKeyword().get());
			handler.invoke(topLevel);
		}
		else {
			fallback.invoke();
		}
	}

	protected Iterable<JavaSymbol> skipToNextImportantToken() {
		while (!parser.isEndOfFile()) {
			final Iterable<JavaSymbol> ws = parser.readWhile(JavaCodeParser.isSpacingToken(true, true));
			if (parser.at(JavaSymbolType.RazorCommentTransition)) {
				parser.accept(ws);
				parser.getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
				parser.razorComment();
			}
			else {
				return ws;
			}
		}
		return Collections.emptyList();
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
