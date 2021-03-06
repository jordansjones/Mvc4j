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

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.KeyValue;
import nextmethod.base.OutParam;
import nextmethod.base.Strings;
import nextmethod.web.razor.editor.AutoCompleteEditHandler;
import nextmethod.web.razor.editor.ImplicitExpressionEditorHandler;
import nextmethod.web.razor.editor.SpanEditHandler;
import nextmethod.web.razor.generator.ExpressionCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.generator.StatementCodeGenerator;
import nextmethod.web.razor.generator.TemplateBlockCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.tokenizer.JavaTokenizer;
import nextmethod.web.razor.tokenizer.symbols.JavaKeyword;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaCodeParser extends TokenizerBackedParser<JavaTokenizer, JavaSymbol, JavaSymbolType> {

    public static final int UsingKeywordLength = 5;
    public static final ImmutableSet<String> DefaultKeywords = ImmutableSet.<String>builder()
                                                                           .add("if")
                                                                           .add("do")
                                                                           .add("try")
                                                                           .add("for")
                                                                           .add("foreach")
                                                                           .add("while")
                                                                           .add("switch")
                                                                           .add("lock")
                                                                           .add("synchronized")
                                                                           .add("using")
                                                                           .add("section")
                                                                           .add("inherits")
                                                                           .add("helper")
                                                                           .add("functions")
                                                                           .add("package")
                                                                           .add("namespace")
                                                                           .add("class")
                                                                           .add("layout")
                                                                           .add("sessionstate")
                                                                           .build();

    protected final Map<String, Delegates.IAction> directiveParsers = Maps.newHashMap();
    protected final Map<JavaKeyword, Delegates.IAction1<Boolean>> keywordParsers = Maps.newHashMap();

    protected final Set<String> keywords;
    private boolean isNested;

    protected final JavaCodeParserStatements parserStatements;
    protected final JavaCodeParserDirectives parserDirectives;

    public JavaCodeParser() {
        super();
        this.keywords = Sets.newHashSet();
        this.parserStatements = new JavaCodeParserStatements(this);
        this.parserDirectives = new JavaCodeParserDirectives(this);
    }

    protected Set<String> getKeywords() {
        return keywords;
    }

    public boolean isNested() {
        return isNested;
    }

    @Override
    protected ParserBase getOtherParser() {
        return getContext().getMarkupParser();
    }

    @Override
    protected LanguageCharacteristics<JavaTokenizer, JavaSymbol, JavaSymbolType> getLanguage() {
        return JavaLanguageCharacteristics.Instance;
    }

    protected boolean tryGetDirectiveHandler(final String directive, @Nonnull final OutParam<Delegates.IAction> handler
                                            ) {
        if (directiveParsers.containsKey(directive)) {
            handler.set(directiveParsers.get(directive));
            return true;
        }
        return false;
    }

    void doAssert(@Nonnull final JavaKeyword expectedKeyword) {
        if (Debug.isAssertEnabled()) {
            final JavaSymbol currentSymbol = getCurrentSymbol();
            assert currentSymbol.getType() == JavaSymbolType.Keyword && currentSymbol.getKeyword().isPresent() &&
                   currentSymbol.getKeyword().get() == expectedKeyword;
        }
    }

    protected boolean at(@Nonnull final JavaKeyword keyword) {
        return at(JavaSymbolType.Keyword) && getCurrentSymbol().getKeyword().isPresent() &&
               getCurrentSymbol().getKeyword().get() == keyword;
    }

    protected boolean acceptIf(@Nonnull final JavaKeyword keyword) {
        if (at(keyword)) {
            acceptAndMoveNext();
            return true;
        }
        return false;
    }

    protected static Delegates.IFunc1<JavaSymbol, Boolean> isSpacingToken(final boolean includeNewLines,
                                                                          final boolean includeComments
                                                                         ) {
        return symbol -> {
            if (symbol == null) return false;
            final JavaSymbolType type = symbol.getType();
            return type == JavaSymbolType.WhiteSpace
                   || (includeNewLines && type == JavaSymbolType.NewLine)
                   || (includeComments && type == JavaSymbolType.Comment);
        };
    }

    @Override
    public void parseBlock() {
        try (final IDisposable disposable1 = pushSpanConfig(defaultSpanConfigDelegate)) {
            if (getContext() == null) {
                throw new UnsupportedOperationException(RazorResources().parserContextNotSet());
            }

            // Unless changed, the block is a statement block
            try (final IDisposable disposable2 = getContext().startBlock(BlockType.Statement)) {
                nextToken();

                acceptWhile(isSpacingToken(true, true));

                JavaSymbol currentSymbol = getCurrentSymbol();
                if (at(JavaSymbolType.StringLiteral) && getCurrentSymbol().getContent().length() > 0 &&
                    getCurrentSymbol().getContent().charAt(0) == SyntaxConstants.TransitionCharacter) {
                    final KeyValue<JavaSymbol, JavaSymbol> split = getLanguage().splitSymbol(
                                                                                                getCurrentSymbol(), 1,
                                                                                                JavaSymbolType.Transition
                                                                                            );
                    currentSymbol = split.getKey();
                    getContext().getSource().setPosition(split.getValue().getStart().getAbsoluteIndex());
                    nextToken();
                }
                else if (at(JavaSymbolType.Transition)) {
                    nextToken();
                }

                // Accept "@" if we see it, but if we don't, that's OK. We assume we were started for a good reason
                if (currentSymbol != null && (currentSymbol.getType() == JavaSymbolType.Transition)) {
                    if (getSpan().getSymbols().size() > 0) {
                        output(SpanKind.Code);
                    }
                    atTransition(currentSymbol);
                }
                else {
                    // No "@" => Jump straight to afterTransition
                    afterTransition();
                }
                output(SpanKind.Code);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void defaultSpanConfig(@Nonnull final SpanBuilder span) {
        span.setEditHandler(SpanEditHandler.createDefault(getLanguage().createTokenizeStringDelegate()));
        span.setCodeGenerator(new StatementCodeGenerator());
    }

    protected final Delegates.IAction1<SpanBuilder> defaultSpanConfigDelegate = input -> {
        if (input != null) {
            defaultSpanConfig(input);
        }
    };

    private void atTransition(@Nonnull final JavaSymbol currentSymbol) {
        if (Debug.isAssertEnabled()) assert (currentSymbol.getType() == JavaSymbolType.Transition);

        accept(currentSymbol);
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);

        // Output the "@" span and continue here
        output(SpanKind.Transition);
        afterTransition();
    }

    @SuppressWarnings("unchecked")
    private void afterTransition() {
        try (final IDisposable ignored = pushSpanConfig(defaultSpanConfigDelegate)) {
            ensureCurrent();
            try {
                // What type of block is this?
                if (!isEndOfFile()) {
                    if (getCurrentSymbol().getType() == JavaSymbolType.LeftParenthesis) {
                        final BlockBuilder currentBlock = getContext().getCurrentBlock();
                        currentBlock.setType(BlockType.Expression);
                        currentBlock.setCodeGenerator(new ExpressionCodeGenerator());
                        explicitExpression();
                        return;
                    }
                    else if (getCurrentSymbol().getType() == JavaSymbolType.Identifier) {
                        final OutParam<Delegates.IAction> handler = OutParam.<Delegates.IAction>of();
                        if (tryGetDirectiveHandler(getCurrentSymbol().getContent(), handler)) {
                            getSpan().setCodeGenerator(SpanCodeGenerator.Null);
                            handler.value().invoke();
                            return;
                        }
                        else {
                            final BlockBuilder currentBlock = getContext().getCurrentBlock();
                            currentBlock.setType(BlockType.Expression);
                            currentBlock.setCodeGenerator(new ExpressionCodeGenerator());
                            implicitExpression();
                            return;
                        }
                    }
                    else if (getCurrentSymbol().getType() == JavaSymbolType.Keyword) {
                        parserStatements.keywordBlock(true);
                        return;
                    }
                    else if (getCurrentSymbol().getType() == JavaSymbolType.LeftBrace) {
                        verbatimBlock();
                        return;
                    }
                }

                // Invalid character
                getContext().getCurrentBlock().setType(BlockType.Expression);
                getContext().getCurrentBlock().setCodeGenerator(new ExpressionCodeGenerator());

                addMarkerSymbolIfNecessary();

                // Code Generator
                getSpan().setCodeGenerator(new ExpressionCodeGenerator());
                // Edit Handler
                final ImplicitExpressionEditorHandler handler = new ImplicitExpressionEditorHandler(
                                                                                                       getLanguage().createTokenizeStringDelegate(),
                                                                                                       DefaultKeywords,
                                                                                                       isNested()
                );
                handler.setAcceptedCharacters(EnumSet.of(AcceptedCharacters.NonWhiteSpace));
                getSpan().setEditHandler(handler);

                if (at(JavaSymbolType.WhiteSpace) || at(JavaSymbolType.NewLine)) {
                    getContext().onError(
                                            getCurrentLocation(),
                                            RazorResources().parseErrorUnexpectedWhiteSpaceAtStartOfCodeBlock()
                                        );
                }
                else if (isEndOfFile()) {
                    getContext().onError(
                                            getCurrentLocation(),
                                            RazorResources().parseErrorUnexpectedEndOfFileAtStartOfCodeBlock()
                                        );
                }
                else {
                    getContext().onError(
                                            getCurrentLocation(),
                                            RazorResources().parseErrorUnexpectedCharacterAtStartOfCodeBlock(
                                                                                                                getCurrentSymbol()
                                                                                                                    .getContent()
                                                                                                            )
                                        );
                }
            }
            finally {
                // Always put current character back in the buffer for the next parser.
                putCurrentBack();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void verbatimBlock() {
        doAssert(JavaSymbolType.LeftBrace);
        final JavaCodeParserStatements.Block block = new JavaCodeParserStatements.Block(
                                                                                           RazorResources().blockNameCode(),
                                                                                           getCurrentLocation()
        );
        acceptAndMoveNext();

        // Set up the "{" span and output
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        output(SpanKind.MetaCode);

        // Setup auto-complete and parse the code block
        final AutoCompleteEditHandler editHandler = new AutoCompleteEditHandler(getLanguage().createTokenizeStringDelegate());
        getSpan().setEditHandler(editHandler);
        parserStatements.codeBlock(false, block);

        getSpan().setCodeGenerator(new StatementCodeGenerator());
        addMarkerSymbolIfNecessary();
        if (!at(JavaSymbolType.RightBrace)) {
            editHandler.setAutoCompleteString("}");
        }
        output(SpanKind.Code);

        if (optional(JavaSymbolType.RightBrace)) {
            // Set up the "}" span
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
            getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        }

        if (!at(JavaSymbolType.WhiteSpace) && !at(JavaSymbolType.NewLine)) {
            putCurrentBack();
        }

        completeBlock(false);
        output(SpanKind.MetaCode);
    }

    void implicitExpression() {
        final BlockBuilder currentBlock = getContext().getCurrentBlock();
        currentBlock.setType(BlockType.Expression);
        currentBlock.setCodeGenerator(new ExpressionCodeGenerator());

        try (
                IDisposable disposable = pushSpanConfig(
                                                           input -> {
                                                               if (input != null) {
                                                                   input.setEditHandler(
                                                                                           new ImplicitExpressionEditorHandler(
                                                                                                                                  getLanguage()
                                                                                                                                      .createTokenizeStringDelegate(),
                                                                                                                                  getKeywords(),
                                                                                                                                  isNested()
                                                                                           )
                                                                                       );
                                                                   input.getEditHandler()
                                                                        .setAcceptedCharacters(EnumSet.of(AcceptedCharacters.NonWhiteSpace));
                                                                   input.setCodeGenerator(new ExpressionCodeGenerator());
                                                               }
                                                           }
                                                       )
        ) {
            do {
                if (atIdentifier(true)) {
                    acceptAndMoveNext();
                }
            }
            while (methodCallOrArrayIndex());

            putCurrentBack();
            output(SpanKind.Code);
        }
    }

    private boolean methodCallOrArrayIndex() {
        if (!isEndOfFile()) {
            final JavaSymbolType type = getCurrentSymbol().getType();
            if (type == JavaSymbolType.LeftParenthesis || type == JavaSymbolType.LeftBracket) {
                // If we end within "(", whitespace is fine
                getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);

                JavaSymbolType right;
                boolean success;

                try (
                        IDisposable disposable = pushSpanConfig(
                                                                   (span, prev) -> {
                                                                       if (prev != null) prev.invoke(span);
                                                                       if (span != null) {
                                                                           span.getEditHandler()
                                                                               .setAcceptedCharacters(AcceptedCharacters.Any);
                                                                       }
                                                                   }
                                                               )
                ) {
                    right = getLanguage().flipBracket(getCurrentSymbol().getType());
                    success = balance(
                                         EnumSet.of(
                                                       BalancingModes.BacktrackOnFailure,
                                                       BalancingModes.AllowCommentsAndTemplates
                                                   )
                                     );
                }

                if (!success) {
                    acceptUntil(JavaSymbolType.LessThan);
                }
                if (at(right)) {
                    acceptAndMoveNext();
                    getSpan().getEditHandler().setAcceptedCharacters(EnumSet.of(AcceptedCharacters.NonWhiteSpace));
                }
                return methodCallOrArrayIndex();
            }
            if (type == JavaSymbolType.Dot) {
                final JavaSymbol dot = getCurrentSymbol();
                if (nextToken()) {
                    if (at(JavaSymbolType.Identifier) || at(JavaSymbolType.Keyword)) {
                        // Accept the dot and return to the start
                        accept(dot);
                        return true; // continue
                    }
                    // Put the symbol back
                    putCurrentBack();
                }
                if (!isNested()) {
                    // Put the "." back
                    putBack(dot);
                }
                else {
                    accept(dot);
                }
            }
            else if (!at(JavaSymbolType.WhiteSpace) && !at(JavaSymbolType.NewLine)) {
                putCurrentBack();
            }
        }

        // Implicit Expression is complete
        return false;
    }

    void completeBlock() {
        completeBlock(true);
    }

    void completeBlock(final boolean insertMarkerIfNecessary) {
        completeBlock(insertMarkerIfNecessary, insertMarkerIfNecessary);
    }

    void completeBlock(final boolean insertMarkerIfNecessary, final boolean captureWhitespaceToEndOfLine) {
        if (insertMarkerIfNecessary && !getContext().getLastAcceptedCharacters().equals(AcceptedCharacters.Any)) {
            addMarkerSymbolIfNecessary();
        }

        ensureCurrent();

        // Read whitespace, but not newlines
        // If we're not inserting a marker span, we don't need to capture whitespace
        if (
            !getContext().isWhiteSpaceIsSignificantToAncestorBlock()
            && (getContext().getCurrentBlock().getType().isPresent() &&
                getContext().getCurrentBlock().getType().get() != BlockType.Expression)
            && captureWhitespaceToEndOfLine
            && !getContext().isDesignTimeMode()
            && !isNested()
            ) {
            captureWhitespaceAtEndOfCodeOnlyLine();
        }
        else {
            putCurrentBack();
        }
    }

    private void captureWhitespaceAtEndOfCodeOnlyLine() {
        final Iterable<JavaSymbol> ws = readWhile(
                                                     symbol -> symbol != null &&
                                                               symbol.getType() == JavaSymbolType.WhiteSpace
                                                 );

        if (at(JavaSymbolType.NewLine)) {
            accept(ws);
            acceptAndMoveNext();
            putCurrentBack();
        }
        else {
            putCurrentBack();
            putBack(ws);
        }
    }

    private void configureExplicitExpressionSpan(@Nonnull final SpanBuilder sb) {
        sb.setEditHandler(SpanEditHandler.createDefault(getLanguage().createTokenizeStringDelegate()));
        sb.setCodeGenerator(new ExpressionCodeGenerator());
    }

    private final Delegates.IAction1<SpanBuilder> configureExplicitExpressionSpanDelegate = input -> {
        if (input != null) {
            configureExplicitExpressionSpan(input);
        }
    };

    private void explicitExpression() {
        final JavaCodeParserStatements.Block block = new JavaCodeParserStatements.Block(
                                                                                           RazorResources().blockNameExplicitExpression(),
                                                                                           getCurrentLocation()
        );
        doAssert(JavaSymbolType.LeftParenthesis);
        acceptAndMoveNext();
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        output(SpanKind.MetaCode);

        try (IDisposable disp = pushSpanConfig(configureExplicitExpressionSpanDelegate)) {
            final boolean success = balance(
                                               EnumSet.of(
                                                             BalancingModes.BacktrackOnFailure,
                                                             BalancingModes.NoErrorOnFailure,
                                                             BalancingModes.AllowCommentsAndTemplates
                                                         ),
                                               JavaSymbolType.LeftParenthesis,
                                               JavaSymbolType.RightParenthesis,
                                               block.getStart()
                                           );

            if (!success) {
                acceptUntil(JavaSymbolType.LessThan);
                getContext().onError(
                                        block.getStart(), RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                                                    block
                                                                                                                        .getName(),
                                                                                                                    ")",
                                                                                                                    "("
                                                                                                                )
                                    );
            }

            // If necessary, put an empty-content marker symbol here
            if (getSpan().getSymbols().isEmpty()) {
                accept(new JavaSymbol(getCurrentLocation(), Strings.Empty, JavaSymbolType.Unknown));
            }

            // Output the content span and then capture the ")"
            output(SpanKind.Code);
        }
        optional(JavaSymbolType.RightParenthesis);
        if (!isEndOfFile()) {
            putCurrentBack();
        }
        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);
        completeBlock(false);
        output(SpanKind.MetaCode);
    }

    protected void template() {
        if (getContext().isWithin(BlockType.Template)) {
            getContext().onError(getCurrentLocation(), RazorResources().parseErrorInlineMarkupBlocksCannotBeNested());
        }
        output(SpanKind.Code);
        try (IDisposable disposable = getContext().startBlock(BlockType.Template)) {
            getContext().getCurrentBlock().setCodeGenerator(new TemplateBlockCodeGenerator());
            putCurrentBack();
            otherParserBlock();
        }
    }

    protected void otherParserBlock() {
        parseWithOtherParser(
                                input -> {
                                    if (input != null) input.parseBlock();
                                }
                            );
    }

    void sectionBlock(@Nonnull final String left, @Nonnull final String right, final boolean caseSensitive) {
        parseWithOtherParser(
                                input -> {
                                    if (input != null) {
                                        input.parseSection(KeyValue.<String, String>of(left, right), caseSensitive);
                                    }
                                }
                            );
    }

    protected void nestedBlock() {
        output(SpanKind.Code);
        final boolean wasNested = isNested();
        this.isNested = true;
        try (IDisposable disp = pushSpanConfig()) {
            parseBlock();
        }
        initialize(getSpan());
        this.isNested = wasNested;
        nextToken();
    }

    @Override
    protected boolean isAtEmbeddedTransition(boolean allowTemplatesAndComments, boolean allowTransitions) {
        // No embedded transitions in Java, so ignore that param
        return allowTemplatesAndComments
               && (
                      (
                          getLanguage().isTransition(getCurrentSymbol())
                          && nextIs(JavaSymbolType.LessThan, JavaSymbolType.Colon)
                      )
                      || getLanguage().isCommentStart(getCurrentSymbol())
        );
    }

    @Override
    protected void handleEmbeddedTransition() {
        if (getLanguage().isTransition(getCurrentSymbol())) {
            putCurrentBack();
            template();
        }
        else if (getLanguage().isCommentStart(getCurrentSymbol())) {
            razorComment();
        }
    }

    @Override
    public void outputSpanBeforeRazorComment() {
        addMarkerSymbolIfNecessary();
        output(SpanKind.Code);
    }

    private void parseWithOtherParser(@Nonnull final Delegates.IAction1<ParserBase> parserAction) {
        try (IDisposable ignored = pushSpanConfig()) {
            getContext().switchActiveParser();
            parserAction.invoke(getContext().getMarkupParser());
            getContext().switchActiveParser();
        }
        initialize(getSpan());
        nextToken();
    }

}
