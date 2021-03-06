/*
 * Copyright 2014 Jordan S. Jones <jordansjones@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextmethod.web.razor.parser;

import java.util.Collections;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import nextmethod.web.razor.tokenizer.symbols.JavaKeyword;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import nextmethod.web.razor.tokenizer.symbols.SymbolExtensions;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

final class JavaCodeParserStatements extends JavaCodeParserDelegate {


    JavaCodeParserStatements(@Nonnull final JavaCodeParser parser) {
        super(parser);
        setupKeywords();
    }

    private void setupKeywords() {
        mapKeywords(
                       conditionalBlockDelegate, JavaKeyword.For, JavaKeyword.Foreach, JavaKeyword.While,
                       JavaKeyword.Switch, JavaKeyword.Lock, JavaKeyword.Synchronized
                   );
        mapKeywords(caseStatementDelegate, false, JavaKeyword.Case, JavaKeyword.Default);
        mapKeywords(ifStatementDelegate, JavaKeyword.If);
        mapKeywords(tryStatementDelegate, JavaKeyword.Try);
        mapKeywords(usingKeywordDelegate, JavaKeyword.Using);
        mapKeywords(doStatementDelegate, JavaKeyword.Do);
        mapKeywords(reservedDirectiveDelegate, JavaKeyword.Namespace, JavaKeyword.Package, JavaKeyword.Class);
    }

    private void mapKeywords(@Nonnull final Delegates.IAction1<Boolean> handler, final JavaKeyword... keywords) {
        mapKeywords(handler, true, keywords);
    }

    private void mapKeywords(@Nonnull final Delegates.IAction1<Boolean> handler, boolean topLevel,
                             final JavaKeyword... keywords
                            ) {
        for (JavaKeyword keyword : keywords) {
            delegate.keywordParsers.put(keyword, handler);
            if (topLevel) {
                delegate.keywords.add(JavaLanguageCharacteristics.getKeyword(keyword));
            }
        }
    }

    protected void reservedDirective(@SuppressWarnings("UnusedParameters") final boolean topLevel) {
        getContext().onError(
                                getCurrentLocation(),
                                RazorResources().parseErrorReservedWord(getCurrentSymbol().getContent())
                            );
        acceptAndMoveNext();
        final SpanBuilder span = getSpan();
        span.getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
        span.setCodeGenerator(SpanCodeGenerator.Null);
        getContext().getCurrentBlock().setType(BlockType.Directive);
        completeBlock();
        output(SpanKind.MetaCode);
    }

    protected final Delegates.IAction1<Boolean> reservedDirectiveDelegate = input -> {
        if (input != null) reservedDirective(input);
    };

    protected void keywordBlock(final boolean topLevel) {
        handleKeyword(
                         topLevel, () -> {
                final BlockBuilder currentBlock = getContext().getCurrentBlock();
                currentBlock.setType(BlockType.Expression);
                currentBlock.setCodeGenerator(new ExpressionCodeGenerator());
                implicitExpression();
            }
                     );
    }

    protected void caseStatement(@SuppressWarnings("UnusedParameters") final boolean topLevel) {
        doAssert(JavaSymbolType.Keyword);
        if (Debug.isAssertEnabled()) {
            final JavaSymbol currentSymbol = getCurrentSymbol();
            assert currentSymbol.getKeyword().isPresent()
                   && (currentSymbol.getKeyword().get() == JavaKeyword.Case
                       || currentSymbol.getKeyword().get() == JavaKeyword.Default);
        }
        acceptUntil(JavaSymbolType.Colon);
        optional(JavaSymbolType.Colon);
    }

    protected final Delegates.IAction1<Boolean> caseStatementDelegate = input -> {
        if (input != null) caseStatement(input);
    };

    protected void doStatement(final boolean topLevel) {
        doAssert(JavaKeyword.Do);
        unconditionalBlock();
        whileClause();
        if (topLevel) {
            completeBlock();
        }
    }

    protected final Delegates.IAction1<Boolean> doStatementDelegate = input -> {
        if (input != null) doStatement(input);
    };

    protected void whileClause() {
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
        final Iterable<JavaSymbol> ws = skipToNextImportantToken();
        if (at(JavaKeyword.While)) {
            accept(ws);
            doAssert(JavaKeyword.While);
            acceptAndMoveNext();
            acceptWhile(isSpacingToken(true, true));
            if (acceptCondition() && optional(JavaSymbolType.Semicolon)) {
                getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
            }
        }
        else {
            putCurrentBack();
            putBack(ws);
        }
    }

    protected void usingKeyword(final boolean topLevel) {
        doAssert(JavaKeyword.Using);
        final Block block = new Block(getCurrentSymbol());
        acceptAndMoveNext();
        acceptWhile(isSpacingToken(false, true));

        if (at(JavaSymbolType.LeftParenthesis)) {
            // using ( ==> Using Statement
            usingStatement(block);
        }
        else if (at(JavaSymbolType.Identifier)) {
            // using Identifier ==> Using Declaration
            if (!topLevel) {
                getContext().onError(
                                        block.getStart(),
                                        RazorResources().parseErrorNamespaceImportAndTypeAliasCannotExistWithinCodeBlock()
                                    );
                standardStatement();
            }
            else {
                usingDeclaration();
            }
        }

        if (topLevel) {
            completeBlock();
        }
    }

    protected final Delegates.IAction1<Boolean> usingKeywordDelegate = input -> {
        if (input != null) usingKeyword(input);
    };

    protected void usingDeclaration() {
        // Set block type to directive
        getContext().getCurrentBlock().setType(BlockType.Directive);

        // Parse a type name
        doAssert(JavaSymbolType.Identifier);
        packageOrTypeName();
        final Iterable<JavaSymbol> ws = readWhile(isSpacingToken(true, true));
        if (at(JavaSymbolType.Assign)) {
            // Alias
            accept(ws);
            doAssert(JavaSymbolType.Assign);
            acceptAndMoveNext();

            acceptWhile(isSpacingToken(true, true));

            // One more package or type name
            packageOrTypeName();
        }
        else {
            putCurrentBack();
            putBack(ws);
        }

        final SpanBuilder span = getSpan();
        span.getEditHandler().setAcceptedCharacters(AcceptedCharacters.AnyExceptNewLine);
        span.setCodeGenerator(
                                 new AddImportCodeGenerator(
                                                               SymbolExtensions.getContent(
                                                                                              span, input1 -> {
                                                                       if (input1 == null) return null;
                                                                       return Iterables.skip(input1, 1);
                                                                   }
                                                                                          ).toString(),
                                                               SyntaxConstants.Java.UsingKeywordLength
                                 )
                             );

        // Optional ";"
        if (ensureCurrent()) {
            optional(JavaSymbolType.Semicolon);
        }
    }

    protected boolean packageOrTypeName() {
        if (optional(JavaSymbolType.Identifier) || optional(JavaSymbolType.Keyword)) {
            optional(JavaSymbolType.QuestionMark); // Nullable
            if (optional(JavaSymbolType.DoubleColon)) {
                if (!optional(JavaSymbolType.Identifier)) {
                    optional(JavaSymbolType.Keyword);
                }
            }
            if (at(JavaSymbolType.LessThan)) {
                typeArgumentList();
            }
            if (optional(JavaSymbolType.Dot)) {
                packageOrTypeName();
            }
            while (at(JavaSymbolType.LeftBracket)) {
                balance(BalancingModes.SetOfNone);
                optional(JavaSymbolType.RightBracket);
            }
            return true;
        }
        return false;
    }

    protected void typeArgumentList() {
        doAssert(JavaSymbolType.LessThan);
        balance(BalancingModes.SetOfNone);
        optional(JavaSymbolType.GreaterThan);
    }

    protected void usingStatement(@Nonnull final Block block) {
        doAssert(JavaSymbolType.LeftParenthesis);

        // Parse condition
        if (acceptCondition()) {
            acceptWhile(isSpacingToken(true, true));

            // Parse Code block
            expectCodeBlock(block);
        }
    }

    protected void tryStatement(final boolean topLevel) {
        doAssert(JavaKeyword.Try);
        unconditionalBlock();
        afterTryClause();
        if (topLevel) {
            completeBlock();
        }
    }

    protected final Delegates.IAction1<Boolean> tryStatementDelegate = input -> {
        if (input != null) tryStatement(input);
    };

    protected void ifStatement(final boolean topLevel) {
        doAssert(JavaKeyword.If);
        conditionalBlock(false);
        afterIfClause();
        if (topLevel) {
            completeBlock();
        }
    }

    protected final Delegates.IAction1<Boolean> ifStatementDelegate = input -> {
        if (input != null) ifStatement(input);
    };

    protected void afterTryClause() {
        // Grab whitespace
        final Iterable<JavaSymbol> ws = skipToNextImportantToken();

        // Check for a catch or finally part
        if (at(JavaKeyword.Catch)) {
            accept(ws);
            doAssert(JavaKeyword.Catch);
            conditionalBlock(false);
            afterTryClause();
        }
        else if (at(JavaKeyword.Finally)) {
            accept(ws);
            doAssert(JavaKeyword.Finally);
            unconditionalBlock();
        }
        else {
            // Return whitespace and end the block
            putCurrentBack();
            putBack(ws);
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
        }
    }

    protected void afterIfClause() {
        // Grab whitespace and razor comments
        final Iterable<JavaSymbol> ws = skipToNextImportantToken();

        // Check for an else part
        if (at(JavaKeyword.Else)) {
            accept(ws);
            doAssert(JavaKeyword.Else);
            elseClause();
        }
        else {
            // No else, return whitespace
            putCurrentBack();
            putBack(ws);
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
        }
    }

    protected void elseClause() {
        if (!at(JavaKeyword.Else)) {
            return;
        }

        final Block block = new Block(getCurrentSymbol());

        acceptAndMoveNext();
        acceptWhile(isSpacingToken(true, true));
        if (at(JavaKeyword.If)) {
            // ElseIf
            block.setName(SyntaxConstants.Java.ElseIfKeyword);
            conditionalBlock(block);
            afterIfClause();
        }
        else if (!isEndOfFile()) {
            // Else
            expectCodeBlock(block);
        }
    }

    protected void expectCodeBlock(@Nonnull final Block block) {
        if (!isEndOfFile()) {
            // Check for "{" to make sure we're at a block
            if (!at(JavaSymbolType.LeftBrace)) {
                getContext().onError(
                                        getCurrentLocation(),
                                        RazorResources().parseErrorSingleLineControlFlowStatementsNotAllowed(
                                                                                                                getLanguage()
                                                                                                                    .getSample(JavaSymbolType.LeftBrace),
                                                                                                                getCurrentSymbol()
                                                                                                                    .getContent()
                                                                                                            )
                                    );
            }

            // Parse the statement and then we're done
            statement(block);
        }
    }

    protected void unconditionalBlock() {
        doAssert(JavaSymbolType.Keyword);
        final Block block = new Block(getCurrentSymbol());
        acceptAndMoveNext();
        acceptWhile(isSpacingToken(true, true));
        expectCodeBlock(block);
    }

    protected void conditionalBlock(final boolean topLevel) {
        doAssert(JavaSymbolType.Keyword);
        final Block block = new Block(getCurrentSymbol());
        conditionalBlock(block);
        if (topLevel) {
            completeBlock();
        }
    }

    protected final Delegates.IAction1<Boolean> conditionalBlockDelegate = input -> {
        if (input != null) conditionalBlock(input);
    };

    protected void conditionalBlock(@Nonnull final Block block) {
        acceptAndMoveNext();
        acceptWhile(isSpacingToken(true, true));

        // Parse the condition, if present (if not preset, we'll let the java compiler complain)
        if (acceptCondition()) {
            acceptWhile(isSpacingToken(true, true));
            expectCodeBlock(block);
        }
    }

    protected boolean acceptCondition() {
        if (at(JavaSymbolType.LeftParenthesis)) {
            final boolean complete = balance(
                                                EnumSet.of(
                                                              BalancingModes.BacktrackOnFailure,
                                                              BalancingModes.AllowCommentsAndTemplates
                                                          )
                                            );
            if (!complete) {
                acceptUntil(JavaSymbolType.NewLine);
            }
            else {
                optional(JavaSymbolType.RightParenthesis);
            }
            return complete;
        }
        return true;
    }

    protected void statement() {
        statement(null);
    }

    protected void statement(@Nullable final Block block) {
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);

        // Accept whitespace but always keep the last whitespace node so we can put it back if necessary
        final JavaSymbol lastWs = acceptWhiteSpaceInLines();
        if (Debug.isAssertEnabled()) {
            assert lastWs == null || (lastWs.getStart().getAbsoluteIndex() + lastWs.getContent().length() ==
                                      getCurrentLocation().getAbsoluteIndex());
        }

        if (isEndOfFile()) {
            if (lastWs != null) {
                accept(lastWs);
            }
            return;
        }

        final JavaSymbolType type = getCurrentSymbol().getType();
        final SourceLocation loc = getCurrentLocation();
        final boolean isSingleLineMarkup = type == JavaSymbolType.Transition && nextIs(JavaSymbolType.Colon);
        final boolean isMarkup = isSingleLineMarkup
                                 || type == JavaSymbolType.LessThan
                                 || (type == JavaSymbolType.Transition && nextIs(JavaSymbolType.LessThan));

        if (getContext().isDesignTimeMode() || !isMarkup) {
            // CODE owns whitespace, MARKUP owns it ONLY in DesignTimeMode
            if (lastWs != null) {
                accept(lastWs);
            }
        }
        else {
            // MARKUP owns whitespace EXCEPT in DesignTimeMode.
            putCurrentBack();
            putBack(lastWs);
        }

        if (isMarkup) {
            if (type == JavaSymbolType.Transition && !isSingleLineMarkup) {
                getContext().onError(
                                        loc,
                                        RazorResources().parseErrorAtInCodeMustBeFollowedByColonParenOrIdentifierStart()
                                    );
            }

            // Markup block
            output(SpanKind.Code);
            if (getContext().isDesignTimeMode() && getCurrentSymbol() != null &&
                (getCurrentSymbol().getType() == JavaSymbolType.LessThan ||
                 getCurrentSymbol().getType() == JavaSymbolType.Transition)) {
                putCurrentBack();
            }
            otherParserBlock();
        }
        else {
            // What kind of statement is this?
            handleStatement(block, type);
        }
    }

    protected void handleStatement(@Nullable final Block block, @Nonnull final JavaSymbolType type) {
        switch (type) {
            case RazorCommentTransition:
                output(SpanKind.Code);
                razorComment();
                statement(block);
                break;

            case LeftBrace:
                // Verbatim Block
                acceptAndMoveNext();
                codeBlock(
                             block != null
                             ? block
                             : new Block(RazorResources().blockNameCode(), getCurrentLocation())
                         );
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
                acceptAndMoveNext();
                break;

            default:
                // Other statement
                standardStatement();
                break;
        }
    }

    protected void embeddedExpression() {
        // First, verify the type of the block
        doAssert(JavaSymbolType.Transition);
        final JavaSymbol transition = getCurrentSymbol();
        nextToken();

        if (at(JavaSymbolType.Transition)) {
            // Escaped "@"
            output(SpanKind.Code);

            // Output "@" as hidden span
            accept(transition);
            getSpan().setCodeGenerator(SpanCodeGenerator.Null);
            output(SpanKind.Code);

            doAssert(JavaSymbolType.Transition);
            acceptAndMoveNext();
            standardStatement();
        }
        else {
            // Throw errors as necessary, but continue parsing
            if (at(JavaSymbolType.Keyword)) {
                getContext().onError(
                                        getCurrentLocation(),
                                        RazorResources().parseErrorUnexpectedKeywordAfterAt(
                                                                                               JavaLanguageCharacteristics
                                                                                                   .getKeyword(
                                                                                                                  getCurrentSymbol()
                                                                                                                      .getKeyword()
                                                                                                                      .get()
                                                                                                              )
                                                                                           )
                                    );
            }
            else if (at(JavaSymbolType.LeftBrace)) {
                getContext().onError(
                                        getCurrentLocation(),
                                        RazorResources().parseErrorUnexpectedNestedCodeBlock()
                                    );
            }

            // @( or @foo - Nested expression, parser a child block
            putCurrentBack();
            putBack(transition);

            // Before exiting, add a marker span if necessary
            addMarkerSymbolIfNecessary();

            nestedBlock();
        }
    }

    protected void standardStatement() {
        while (!isEndOfFile()) {
            final int bookmark = getCurrentLocation().getAbsoluteIndex();
            final Iterable<JavaSymbol> read = readWhile(
                                                           sym -> sym != null
                                                                  && sym.getType() != JavaSymbolType.Semicolon
                                                                  &&
                                                                  sym.getType() != JavaSymbolType.RazorCommentTransition
                                                                  && sym.getType() != JavaSymbolType.Transition
                                                                  && sym.getType() != JavaSymbolType.LeftBrace
                                                                  && sym.getType() != JavaSymbolType.LeftParenthesis
                                                                  && sym.getType() != JavaSymbolType.LeftBracket
                                                                  && sym.getType() != JavaSymbolType.RightBrace
                                                       );

            if (at(JavaSymbolType.LeftBrace) || at(JavaSymbolType.LeftParenthesis) || at(JavaSymbolType.LeftBracket)) {
                accept(read);
                if (balance(EnumSet.of(BalancingModes.AllowCommentsAndTemplates, BalancingModes.BacktrackOnFailure))) {
                    optional(JavaSymbolType.RightBrace);
                }
                else {
                    // Recovery
                    acceptUntil(JavaSymbolType.LessThan, JavaSymbolType.RightBrace);
                    return;
                }
            }
            else if (at(JavaSymbolType.Transition) && (nextIs(JavaSymbolType.LessThan, JavaSymbolType.Colon))) {
                accept(read);
                output(SpanKind.Code);
                template();
            }
            else if (at(JavaSymbolType.RazorCommentTransition)) {
                accept(read);
                razorComment();
            }
            else if (at(JavaSymbolType.Semicolon)) {
                accept(read);
                acceptAndMoveNext();
                return;
            }
            else if (at(JavaSymbolType.RightBrace)) {
                accept(read);
                return;
            }
            else {
                getContext().getSource().setPosition(bookmark);
                nextToken();
                acceptUntil(JavaSymbolType.LessThan, JavaSymbolType.RightBrace);
                return;
            }
        }
    }

    protected final Delegates.IAction standardStatementDelegate = this::standardStatement;

    protected void codeBlock(@Nonnull final Block block) {
        codeBlock(true, block);
    }

    protected void codeBlock(final boolean acceptTerminatingBrace, @Nonnull final Block block) {
        ensureCurrent();
        while (!isEndOfFile() && !at(JavaSymbolType.RightBrace)) {
            // Parse a statement, then return here
            statement();
            ensureCurrent();
        }

        if (isEndOfFile()) {
            getContext().onError(
                                    block.getStart(), RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                                block.getName(),
                                                                                                                "}", "{"
                                                                                                            )
                                );
        }
        else if (acceptTerminatingBrace) {
            doAssert(JavaSymbolType.RightBrace);
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
            acceptAndMoveNext();
        }
    }

    protected void handleKeyword(final boolean topLevel, @Nonnull final Delegates.IAction fallback) {
        if (Debug.isAssertEnabled()) {
            assert (getCurrentSymbol().getType() == JavaSymbolType.Keyword &&
                    getCurrentSymbol().getKeyword().isPresent());
        }

        if (getCurrentSymbol().getKeyword().isPresent() &&
            delegate.keywordParsers.containsKey(getCurrentSymbol().getKeyword().get())) {
            final Delegates.IAction1<Boolean> handler = delegate.keywordParsers.get(
                                                                                       getCurrentSymbol().getKeyword()
                                                                                                         .get()
                                                                                   );
            handler.invoke(topLevel);
        }
        else {
            fallback.invoke();
        }
    }

    protected Iterable<JavaSymbol> skipToNextImportantToken() {
        while (!isEndOfFile()) {
            final Iterable<JavaSymbol> ws = readWhile(isSpacingToken(true, true));
            if (at(JavaSymbolType.RazorCommentTransition)) {
                accept(ws);
                getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
                razorComment();
            }
            else {
                return ws;
            }
        }
        return Collections.emptyList();
    }

}
