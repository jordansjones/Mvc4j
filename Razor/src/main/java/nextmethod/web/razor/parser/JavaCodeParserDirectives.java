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

import java.util.Objects;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableCollection;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.Strings;
import nextmethod.web.razor.editor.AutoCompleteEditHandler;
import nextmethod.web.razor.editor.EditorHints;
import nextmethod.web.razor.generator.HelperCodeGenerator;
import nextmethod.web.razor.generator.RazorDirectiveAnnotationCodeGenerator;
import nextmethod.web.razor.generator.SectionCodeGenerator;
import nextmethod.web.razor.generator.SetBaseTypeCodeGenerator;
import nextmethod.web.razor.generator.SetLayoutCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.generator.TypeMemberCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import nextmethod.web.razor.tokenizer.symbols.SymbolExtensions;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

final class JavaCodeParserDirectives extends JavaCodeParserDelegate {

    JavaCodeParserDirectives(final JavaCodeParser parser) {
        super(parser);
        setupDirectives();
    }

    private void setupDirectives() {
        mapDirectives(inheritsDirectiveDelegate, SyntaxConstants.Java.InheritsKeyword);
        mapDirectives(functionsDirectiveDelegate, SyntaxConstants.Java.FunctionsKeyword);
        mapDirectives(sectionDirectiveDelegate, SyntaxConstants.Java.SectionKeyword);
        mapDirectives(helperDirectiveDelegate, SyntaxConstants.Java.HelperKeyword);
        mapDirectives(layoutDirectiveDelegate, SyntaxConstants.Java.LayoutKeyword);
        mapDirectives(sessionStateDirectiveDelegate, SyntaxConstants.Java.SessionStateKeyword);
    }

    protected void mapDirectives(final Delegates.IAction handler, final String... directives) {
        for (String directive : directives) {
            delegate.directiveParsers.put(directive, handler);
            delegate.keywords.add(directive);
        }
    }

    protected void layoutDirective() {
        assertDirective(SyntaxConstants.Java.LayoutKeyword);
        acceptAndMoveNext();
        getContext().getCurrentBlock().setType(BlockType.Directive);

        // Accept spaces, but not newlines
        final boolean foundSomeWhitespace = at(JavaSymbolType.WhiteSpace);
        acceptWhile(JavaSymbolType.WhiteSpace);
        output(
                  SpanKind.MetaCode, foundSomeWhitespace
                                     ? AcceptedCharacters.SetOfNone
                                     : AcceptedCharacters.Any
              );

        // First non-whitespace character starts the Layout Page, then newline ends it
        acceptUntil(JavaSymbolType.NewLine);
        getSpan().setCodeGenerator(new SetLayoutCodeGenerator(SymbolExtensions.getContent(getSpan()).toString()));
        getSpan().getEditHandler().setEditorHints(EditorHints.LayoutPage, EditorHints.VirtualPath);
        final boolean foundNewline = optional(JavaSymbolType.NewLine);
        addMarkerSymbolIfNecessary();
        output(
                  SpanKind.MetaCode, foundNewline
                                     ? AcceptedCharacters.SetOfNone
                                     : AcceptedCharacters.Any
              );
    }

    protected final Delegates.IAction layoutDirectiveDelegate = this::layoutDirective;

    protected void sessionStateDirective() {
        assertDirective(SyntaxConstants.Java.SessionStateKeyword);
        acceptAndMoveNext();
        sessionStateDirectiveCore();
    }

    protected final Delegates.IAction sessionStateDirectiveDelegate = this::sessionStateDirective;

    protected void sessionStateDirectiveCore() {
        sessionStateTypeDirective(
                                     RazorResources().parserErrorSessionDirectiveMissingValue(), (key, value) -> {
                assert key != null;
                assert value != null;
                return new RazorDirectiveAnnotationCodeGenerator(key, value);
            }
                                 );
    }

    protected void sessionStateTypeDirective(@Nonnull final String noValueError, @Nonnull
    final Delegates.IFunc2<String, String, SpanCodeGenerator> createCodeGenerator
                                            ) {
        // Set the block type
        getContext().getCurrentBlock().setType(BlockType.Directive);

        // Accept whitespace
        final JavaSymbol remainingWs = acceptSingleWhiteSpaceCharacter();

        if (!getSpan().getSymbols().isEmpty()) {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }

        output(SpanKind.MetaCode);

        if (remainingWs != null) {
            accept(remainingWs);
        }
        acceptWhile(isSpacingToken(false, true));

        // Parse a Type name
        if (!validSessionStateValue()) {
            getContext().onError(getCurrentLocation(), noValueError);
        }

        // Pull out the type name
        final ImmutableCollection<ISymbol> symbols = getSpan().getSymbols();
        final StringBuilder sb = new StringBuilder(symbols.size());
        for (ISymbol symbol : symbols) {
            final String s = Strings.nullToEmpty(symbol.getContent()).trim();
            sb.append(s);
        }
        final String sessionStateValue = sb.toString();

        // Setup code generation
        final SpanCodeGenerator generator = createCodeGenerator.invoke(
                                                                          SyntaxConstants.Java.SessionStateKeyword,
                                                                          sessionStateValue
                                                                      );
        assert generator != null;
        getSpan().setCodeGenerator(generator);

        // Output the span and finish the block
        completeBlock();
        output(SpanKind.Code);
    }

    protected boolean validSessionStateValue() {
        return optional(JavaSymbolType.Identifier);
    }

    // NOTE: Maybe look at excessive coupling?
    protected void helperDirective() {
        final boolean nested = getContext().isWithin(BlockType.Helper);

        // Set the block and span type
        getContext().getCurrentBlock().setType(BlockType.Helper);

        // Verify we're on "helper" and accept
        assertDirective(SyntaxConstants.Java.HelperKeyword);
        final Block block = new Block(getCurrentSymbol().getContent().toLowerCase(), getCurrentLocation());
        acceptAndMoveNext();

        if (nested) {
            getContext().onError(getCurrentLocation(), RazorResources().parseErrorHelpersCannotBeNested());
        }

        // Accept a single whitespace character if present, if not, we should stop now
        if (!at(JavaSymbolType.WhiteSpace)) {
            final String error;
            if (at(JavaSymbolType.NewLine)) {
                error = RazorResources().errorComponentNewline();
            }
            else if (isEndOfFile()) {
                error = RazorResources().errorComponentEndOfFile();
            }
            else {
                error = RazorResources().errorComponentCharacter(getCurrentSymbol().getContent());
            }

            getContext().onError(
                                    getCurrentLocation(),
                                    RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart(error)
                                );
            putCurrentBack();
            output(SpanKind.MetaCode);
            return;
        }

        final JavaSymbol remainingWs = acceptSingleWhiteSpaceCharacter();

        // Output metacode and continue
        output(SpanKind.MetaCode);
        if (remainingWs != null) {
            accept(remainingWs);
        }
        acceptWhile(isSpacingToken(false, true)); // Don't accept newlines.

        // Expecting an identifier (helper name)
        boolean errorReported = !required(
                                             JavaSymbolType.Identifier, true,
                                             RazorResources().parseErrorUnexpectedCharacterAtHelperNameStart()
                                         );
        if (!errorReported) {
            doAssert(JavaSymbolType.Identifier);
            acceptAndMoveNext();
        }

        acceptWhile(isSpacingToken(false, true));

        // Expecting parameter list start: "("
        final SourceLocation bracketErrorPos = getCurrentLocation();
        if (!optional(JavaSymbolType.LeftParenthesis)) {
            if (!errorReported) {
                errorReported = true;
                getContext().onError(
                                        getCurrentLocation(),
                                        RazorResources().parseErrorMissingCharAfterHelperName("(")
                                    );
            }
        }
        else {
            final SourceLocation bracketStart = getCurrentLocation();
            if (!balance(
                            BalancingModes.NoErrorOnFailure, JavaSymbolType.LeftParenthesis,
                            JavaSymbolType.RightParenthesis, bracketStart
                        )) {
                errorReported = true;
                getContext().onError(
                                        bracketErrorPos,
                                        RazorResources().parseErrorUnterminatedHelperParameterList()
                                    );
            }
            optional(JavaSymbolType.RightParenthesis);
        }

        final int bookmark = getCurrentLocation().getAbsoluteIndex();
        final Iterable<JavaSymbol> ws = readWhile(isSpacingToken(true, true));

        // Expecting a "{"
        final SourceLocation errorLocation = getCurrentLocation();
        final boolean headerComplete = at(JavaSymbolType.LeftBrace);
        if (headerComplete) {
            accept(ws);
            acceptAndMoveNext();
        }
        else {
            getContext().getSource().setPosition(bookmark);
            nextToken();
            acceptWhile(isSpacingToken(false, true));
            if (!errorReported) {
                getContext().onError(
                                        errorLocation,
                                        RazorResources().parseErrorMissingCharAfterHelperParameters(
                                                                                                       getLanguage().getSample(JavaSymbolType.LeftBrace)
                                                                                                   )
                                    );
            }
        }

        // Grab the signature and build the code generator
        addMarkerSymbolIfNecessary();
        final LocationTagged<String> signature = SymbolExtensions.getContent(getSpan());
        final HelperCodeGenerator blockGen = new HelperCodeGenerator(signature, headerComplete);
        getContext().getCurrentBlock().setCodeGenerator(blockGen);

        // The block will generate appropriate code,
        getSpan().setCodeGenerator(SpanCodeGenerator.Null);

        if (!headerComplete) {
            completeBlock();
            output(SpanKind.Code);
            return;
        }

        getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        output(SpanKind.Code);

        // We're valid, so parse the next block
        final AutoCompleteEditHandler bodyEditHandler = new AutoCompleteEditHandler(getLanguage().createTokenizeStringDelegate());
        try (IDisposable ignored = pushSpanConfig(delegate.defaultSpanConfigDelegate)) {
            try (IDisposable ignored2 = getContext().startBlock(BlockType.Statement)) {
                getSpan().setEditHandler(bodyEditHandler);
                delegate.parserStatements.codeBlock(false, block);
                completeBlock(true);
                output(SpanKind.Code);
            }
        }
        initialize(getSpan());

        ensureCurrent();

        getSpan().setCodeGenerator(SpanCodeGenerator.Null); // The block will generate the footer code.
        if (!optional(JavaSymbolType.RightBrace)) {
            // The } is missing, so set the initial signature span to use it as an autocomplete string
            bodyEditHandler.setAutoCompleteString("}");

            // Need to be able to accept anything to properly handle the autocomplete
            bodyEditHandler.setAcceptedCharacters(AcceptedCharacters.Any);
        }
        else {
            blockGen.setFooter(SymbolExtensions.getContent(getSpan()));
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }
        completeBlock();
        output(SpanKind.Code);
    }

    protected final Delegates.IAction helperDirectiveDelegate = this::helperDirective;

    protected void sectionDirective() {
        final boolean nested = getContext().isWithin(BlockType.Section);
        boolean errorReported = false;

        // Set the block and span type
        getContext().getCurrentBlock().setType(BlockType.Section);

        // Verify we're on "section" and accept
        assertDirective(SyntaxConstants.Java.SectionKeyword);
        acceptAndMoveNext();

        if (nested) {
            getContext().onError(
                                    getCurrentLocation(),
                                    RazorResources().parseErrorSectionsCannotBeNested(
                                                                                         RazorResources().sectionExample()
                                                                                     )
                                );
            errorReported = true;
        }

        Iterable<JavaSymbol> ws = readWhile(isSpacingToken(true, false));

        // Get the section name
        String sectionName = Strings.Empty;
        if (!required(
                         JavaSymbolType.Identifier, true,
                         RazorResources().parseErrorUnexpectedCharacterAtSectionNameStart()
                     )) {
            if (!errorReported) {
                errorReported = true;
            }
            putCurrentBack();
            putBack(ws);
            acceptWhile(isSpacingToken(false, false));
        }
        else {
            accept(ws);
            sectionName = getCurrentSymbol().getContent();
            acceptAndMoveNext();
        }
        getContext().getCurrentBlock().setCodeGenerator(new SectionCodeGenerator(sectionName));

        final SourceLocation errorLocation = getCurrentLocation();
        ws = readWhile(isSpacingToken(true, false));

        // Get the starting brace
        final boolean sawStartingBrace = at(JavaSymbolType.LeftBrace);
        if (!sawStartingBrace) {
            if (!errorReported) {
                errorReported = true;
                getContext().onError(errorLocation, RazorResources().parseErrorMissingOpenBraceAfterSection());
            }
            putCurrentBack();
            putBack(ws);
            acceptWhile(isSpacingToken(false, false));
            optional(JavaSymbolType.NewLine);
            output(SpanKind.MetaCode);
            completeBlock();
            return;
        }
        else {
            accept(ws);
        }

        // Set up edit handler
        final AutoCompleteEditHandler editHandler = new AutoCompleteEditHandler(getLanguage().createTokenizeStringDelegate());
        editHandler.setAutoCompleteAtEndOfSpan(true);

        getSpan().setEditHandler(editHandler);
        getSpan().accept(getCurrentSymbol());

        // Output Metacode then switch to section parser
        output(SpanKind.MetaCode);
        sectionBlock("{", "}", true);

        getSpan().setCodeGenerator(SpanCodeGenerator.Null);

        // Check for the terminating "}"
        if (!optional(JavaSymbolType.RightBrace)) {
            editHandler.setAutoCompleteString("}");
            getContext().onError(
                                    getCurrentLocation(),
                                    RazorResources().parseErrorExpectedX(
                                                                            getLanguage().getSample(JavaSymbolType.RightBrace)
                                                                        )
                                );
        }
        else {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }
        completeBlock(false, true);
        output(SpanKind.MetaCode);
    }

    protected final Delegates.IAction sectionDirectiveDelegate = this::sectionDirective;

    protected void functionsDirective() {
        // Set the block type
        getContext().getCurrentBlock().setType(BlockType.Functions);

        // Verify we're on "functions" and accept
        assertDirective(SyntaxConstants.Java.FunctionsKeyword);
        final Block block = new Block(getCurrentSymbol());
        acceptAndMoveNext();

        acceptWhile(isSpacingToken(true, false));

        if (!at(JavaSymbolType.LeftBrace)) {
            getContext().onError(
                                    getCurrentLocation(),
                                    RazorResources().parseErrorExpectedX(
                                                                            getLanguage().getSample(JavaSymbolType.LeftBrace)
                                                                        )
                                );
            completeBlock();
            output(SpanKind.MetaCode);
            return;
        }
        else {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }

        // Capture start point and continue
        final SourceLocation blockStart = getCurrentLocation();
        acceptAndMoveNext();

        // Output what we've seen and continue
        output(SpanKind.MetaCode);

        final AutoCompleteEditHandler editHandler = new AutoCompleteEditHandler(getLanguage().createTokenizeStringDelegate());
        getSpan().setEditHandler(editHandler);

        balance(BalancingModes.NoErrorOnFailure, JavaSymbolType.LeftBrace, JavaSymbolType.RightBrace, blockStart);
        getSpan().setCodeGenerator(new TypeMemberCodeGenerator());
        if (!at(JavaSymbolType.RightBrace)) {
            editHandler.setAutoCompleteString("}");
            getContext().onError(
                                    block.getStart(),
                                    RazorResources().parseErrorExpectedEndOfBlockBeforeEof(
                                                                                              block.getName(),
                                                                                              "}",
                                                                                              "{"
                                                                                          )
                                );
            completeBlock();
            output(SpanKind.Code);
        }
        else {
            output(SpanKind.Code);
            doAssert(JavaSymbolType.RightBrace);
            getSpan().setCodeGenerator(SpanCodeGenerator.Null);
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
            acceptAndMoveNext();
            completeBlock();
            output(SpanKind.MetaCode);
        }
    }

    protected final Delegates.IAction functionsDirectiveDelegate = this::functionsDirective;

    protected void inheritsDirective() {
        // Verify we're on the right keyword and accept
        assertDirective(SyntaxConstants.Java.InheritsKeyword);
        acceptAndMoveNext();

        inheritsDirectiveCore();
    }

    protected final Delegates.IAction inheritsDirectiveDelegate = this::inheritsDirective;

    protected void assertDirective(@Nonnull final String directive) {
        doAssert(JavaSymbolType.Identifier);
        if (Debug.isAssertEnabled()) { assert Objects.equals(getCurrentSymbol().getContent(), directive); }
    }

    protected void inheritsDirectiveCore() {
        baseTypeDirective(
                             RazorResources().parseErrorInheritsKeywordMustBeFollowedByTypeName(),
                             baseType -> {
                                 assert baseType != null;
                                 return new SetBaseTypeCodeGenerator(baseType);
                             }
                         );
    }

    protected void baseTypeDirective(@Nonnull final String noTypeNameError,
                                     @Nonnull final Delegates.IFunc1<String, SpanCodeGenerator> createCodeGenerator
                                    ) {
        // Set the block type
        getContext().getCurrentBlock().setType(BlockType.Directive);

        // Accept whitespace
        final JavaSymbol remainingWs = acceptSingleWhiteSpaceCharacter();

        if (getSpan().getSymbols().size() > 1) {
            getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
        }

        output(SpanKind.MetaCode);

        if (remainingWs != null) {
            accept(remainingWs);
        }
        acceptWhile(isSpacingToken(false, true));

        if (isEndOfFile() || at(JavaSymbolType.WhiteSpace) || at(JavaSymbolType.NewLine)) {
            getContext().onError(getCurrentLocation(), noTypeNameError);
        }

        // Parse to the end of the line
        acceptUntil(JavaSymbolType.NewLine);
        if (!getContext().isDesignTimeMode()) {
            // We want the newline to be treated as code, but it causes issues at design-time.
            optional(JavaSymbolType.WhiteSpace);
        }

        // Pull out the type name
        final String baseType = SymbolExtensions.getContent(getSpan()).toString();

        // Setup code generation
        final SpanCodeGenerator generator = createCodeGenerator.invoke(baseType.trim());
        assert generator != null;
        getSpan().setCodeGenerator(generator);

        // Output the span and finish the block
        completeBlock();
        output(SpanKind.Code);
    }

}
