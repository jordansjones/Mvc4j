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

package nextmethod.web.razor.tokenizer;

import javax.annotation.Nonnull;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.web.razor.State;
import nextmethod.web.razor.parser.ParserHelpers;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.JavaKeyword;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaTokenizer extends Tokenizer<JavaSymbol, JavaSymbolType> {

    private final ImmutableMap<Character, Delegates.IFunc<JavaSymbolType>> operatorHandlers;

    public JavaTokenizer(@Nonnull final ITextDocument source) {
        super(source);
        this.setCurrentState(dataState);

        this.operatorHandlers = ImmutableMap.<Character, Delegates.IFunc<JavaSymbolType>>builder()
                                            .put('-', minusOperatorAction)
                                            .put('<', lessThanOperatorAction)
                                            .put('>', greaterThanOperatorAction)
                                            .put(
                                                    '&', createTwoCharOperatorHandler(
                                                                                         JavaSymbolType.And, '=',
                                                                                         JavaSymbolType.AndAssign, '&',
                                                                                         JavaSymbolType.DoubleAnd
                                                                                     )
                                                )
                                            .put(
                                                    '|', createTwoCharOperatorHandler(
                                                                                         JavaSymbolType.Or, '=',
                                                                                         JavaSymbolType.OrAssign, '|',
                                                                                         JavaSymbolType.DoubleOr
                                                                                     )
                                                )
                                            .put(
                                                    '+', createTwoCharOperatorHandler(
                                                                                         JavaSymbolType.Plus, '=',
                                                                                         JavaSymbolType.PlusAssign, '+',
                                                                                         JavaSymbolType.Increment
                                                                                     )
                                                )
                                            .put(
                                                    '=', createTwoCharOperatorHandler(
                                                                                         JavaSymbolType.Assign, '=',
                                                                                         JavaSymbolType.Equals, '>',
                                                                                         JavaSymbolType.GreaterThanEqual
                                                                                     )
                                                )
                                            .put(
                                                    '!', createTwoCharOperatorHandler(
                                                                                         JavaSymbolType.Not, '=',
                                                                                         JavaSymbolType.NotEqual
                                                                                     )
                                                )
                                            .put(
                                                    '%', createTwoCharOperatorHandler(
                                                                                         JavaSymbolType.Modulo, '=',
                                                                                         JavaSymbolType.ModuloAssign
                                                                                     )
                                                )
                                            .put(
                                                    '*', createTwoCharOperatorHandler(
                                                                                         JavaSymbolType.Star, '=',
                                                                                         JavaSymbolType.MultiplyAssign
                                                                                     )
                                                )
                                            .put(
                                                    ':', createTwoCharOperatorHandler(
                                                                                         JavaSymbolType.Colon, ':',
                                                                                         JavaSymbolType.DoubleColon
                                                                                     )
                                                )
                                            .put(
                                                    '?', createTwoCharOperatorHandler(
                                                                                         JavaSymbolType.QuestionMark,
                                                                                         '?',
                                                                                         JavaSymbolType.NullCoalesce
                                                                                     )
                                                )
                                            .put(
                                                    '^', createTwoCharOperatorHandler(
                                                                                         JavaSymbolType.Xor, '=',
                                                                                         JavaSymbolType.XorAssign
                                                                                     )
                                                )
                                            .put('(', createIAction(JavaSymbolType.LeftParenthesis))
                                            .put(')', createIAction(JavaSymbolType.RightParenthesis))
                                            .put('{', createIAction(JavaSymbolType.LeftBrace))
                                            .put('}', createIAction(JavaSymbolType.RightBrace))
                                            .put('[', createIAction(JavaSymbolType.LeftBracket))
                                            .put(']', createIAction(JavaSymbolType.RightBracket))
                                            .put(',', createIAction(JavaSymbolType.Comma))
                                            .put(';', createIAction(JavaSymbolType.Semicolon))
                                            .put('~', createIAction(JavaSymbolType.Tilde))
                                            .put('#', createIAction(JavaSymbolType.Hash))
                                            .build();
    }

    @Override
    protected JavaSymbol createSymbol(@Nonnull final SourceLocation start, @Nonnull final String content,
                                      @Nonnull final JavaSymbolType javaSymbolType,
                                      @Nonnull final Iterable<RazorError> errors
                                     ) {
        return new JavaSymbol(start, content, javaSymbolType, errors);
    }

    @Override
    public JavaSymbolType getRazorCommentStarType() {
        return JavaSymbolType.RazorCommentStar;
    }

    @Override
    public JavaSymbolType getRazorCommentType() {
        return JavaSymbolType.RazorComment;
    }

    @Override
    public JavaSymbolType getRazorCommentTransitionType() {
        return JavaSymbolType.RazorCommentTransition;
    }

    @Override
    protected State getStartState() {
        return dataState;
    }

    private final State dataState = this::data;

    private StateResult data() {
        char currentChar = getCurrentChar();
        if (ParserHelpers.isNewLine(currentChar)) {
            final boolean checkTwoCharNL = currentChar == '\r';
            takeCurrent();
            if (checkTwoCharNL && getCurrentChar() == '\n') { takeCurrent(); }

            return stay(endSymbol(JavaSymbolType.NewLine));
        }
        if (ParserHelpers.isWhitespace(currentChar)) {
            takeUntil(Predicates.not(ParserHelpers.IsWhitespacePredicate));
            return stay(endSymbol(JavaSymbolType.WhiteSpace));
        }
        if (JavaHelpers.isIdentifierStart(currentChar)) {
            return identifier();
        }
        if (Character.isDigit(currentChar)) {
            return numericLiteral();
        }
        switch (currentChar) {
            case '@':
                return atSymbol();
            case '\'':
                takeCurrent();
                return transition(quotedLiteral('\'', JavaSymbolType.CharacterLiteral));
            case '"':
                takeCurrent();
                return transition(quotedLiteral('"', JavaSymbolType.StringLiteral));
            case '.':
                if (Character.isDigit(peek())) { return realLiteral(); }
                return stay(single(JavaSymbolType.Dot));
            case '/':
                takeCurrent();
                currentChar = getCurrentChar();
                if (currentChar == '/') {
                    takeCurrent();
                    return singleLineComment();
                }
                if (currentChar == '*') {
                    takeCurrent();
                    return transition(blockComment());
                }
                if (currentChar == '=') {
                    takeCurrent();
                    return stay(endSymbol(JavaSymbolType.DivideAssign));
                }
                return stay(endSymbol(JavaSymbolType.Slash));
            default:
                return stay(endSymbol(operator()));
        }
    }

    private StateResult atSymbol() {
        takeCurrent();
        if (getCurrentChar() == '"') {
            takeCurrent();
            return transition(verbatimStringLiteral());
        }
        if (getCurrentChar() == '*') {
            return transition(endSymbol(JavaSymbolType.RazorCommentTransition), afterRazorCommenTransitionState);
        }
        if (getCurrentChar() == '@') {
            return transition(
                                 endSymbol(JavaSymbolType.Transition), () -> {
                    takeCurrent();
                    return transition(endSymbol(JavaSymbolType.Transition), dataState);
                }
                             );
        }
        return stay(endSymbol(JavaSymbolType.Transition));
    }

    private JavaSymbolType operator() {
        final char first = getCurrentChar();
        takeCurrent();
        if (operatorHandlers.containsKey(first)) { return operatorHandlers.get(first).invoke(); }

        return JavaSymbolType.Unknown;
    }

    private final Delegates.IFunc<JavaSymbolType> operatorAction = this::operator;

    private JavaSymbolType lessThanOperator() {
        if (getCurrentChar() == '=') {
            takeCurrent();
            return JavaSymbolType.LessThanEqual;
        }
        return JavaSymbolType.LessThan;
    }

    private final Delegates.IFunc<JavaSymbolType> lessThanOperatorAction = this::lessThanOperator;

    private JavaSymbolType greaterThanOperator() {
        if (getCurrentChar() == '=') {
            takeCurrent();
            return JavaSymbolType.GreaterThanEqual;
        }
        return JavaSymbolType.GreaterThan;
    }

    private final Delegates.IFunc<JavaSymbolType> greaterThanOperatorAction = this::greaterThanOperator;

    private JavaSymbolType minusOperator() {
        if (getCurrentChar() == '>') {
            takeCurrent();
            return JavaSymbolType.Arrow;
        }
        if (getCurrentChar() == '-') {
            takeCurrent();
            return JavaSymbolType.Decrement;
        }
        if (getCurrentChar() == '=') {
            takeCurrent();
            return JavaSymbolType.MinusAssign;
        }
        return JavaSymbolType.Minus;
    }

    private final Delegates.IFunc<JavaSymbolType> minusOperatorAction = this::minusOperator;

    private Delegates.IFunc<JavaSymbolType> createTwoCharOperatorHandler(@Nonnull final JavaSymbolType typeIfOnlyFirst,
                                                                         final char second,
                                                                         @Nonnull final JavaSymbolType typeIfBoth
                                                                        ) {
        return () -> {
            if (getCurrentChar() == second) {
                takeCurrent();
                return typeIfBoth;
            }
            return typeIfOnlyFirst;
        };
    }

    private Delegates.IFunc<JavaSymbolType> createTwoCharOperatorHandler(@Nonnull final JavaSymbolType typeIfOnlyFirst,
                                                                         final char option1,
                                                                         @Nonnull final JavaSymbolType typeIfOption1,
                                                                         final char option2,
                                                                         @Nonnull final JavaSymbolType typeIfOption2
                                                                        ) {
        return () -> {
            if (getCurrentChar() == option1) {
                takeCurrent();
                return typeIfOption1;
            }
            if (getCurrentChar() == option2) {
                takeCurrent();
                return typeIfOption2;
            }
            return typeIfOnlyFirst;
        };
    }

    private State verbatimStringLiteral() {
        return () -> {
            takeUntil(Predicates.equalTo('"'));
            if (getCurrentChar() == '"') {
                takeCurrent();
                if (getCurrentChar() == '"') {
                    takeCurrent();
                    // State in the literal, this is an escaped "
                    return stay();
                }
            }
            else if (isEndOfFile()) {
                getCurrentErrors().add(
                                          new RazorError(
                                                            RazorResources().parseErrorUnterminatedStringLiteral(),
                                                            getCurrentStart()
                                          )
                                      );
            }
            return transition(endSymbol(JavaSymbolType.StringLiteral), dataState);
        };
    }

    private State quotedLiteral(final char quote, @Nonnull final JavaSymbolType literalType) {
        return () -> {
            takeUntil(
                         input -> input != null && (
                                                       input == '\\' || input == quote || ParserHelpers.isNewLine(input)
                         )
                     );
            if (getCurrentChar() == '\\') {
                takeCurrent(); // Take the '\'
                takeCurrent(); // Take the next char as well (multi-char escapes don't matter)
                return stay();
            }
            else if (isEndOfFile() || ParserHelpers.isNewLine(getCurrentChar())) {
                getCurrentErrors().add(
                                          new RazorError(
                                                            RazorResources().parseErrorUnterminatedStringLiteral(),
                                                            getCurrentStart()
                                          )
                                      );
            }
            else {
                takeCurrent(); // No-op if at EOF
            }
            return transition(endSymbol(literalType), dataState);
        };
    }

    private State blockComment() {
        return () -> {
            takeUntil(Predicates.equalTo('*'));
            if (isEndOfFile()) {
                getCurrentErrors().add(
                                          new RazorError(
                                                            RazorResources().parseErrorBlockCommentNotTerminated(),
                                                            getCurrentStart()
                                          )
                                      );
                return transition(endSymbol(JavaSymbolType.Comment), dataState);
            }
            if (getCurrentChar() == '*') {
                takeCurrent();
                if (getCurrentChar() == '/') {
                    takeCurrent();
                    return transition(endSymbol(JavaSymbolType.Comment), dataState);
                }
            }
            return stay();
        };
    }

    private StateResult singleLineComment() {
        takeUntil(input -> input != null && ParserHelpers.isNewLine(input));
        return stay(endSymbol(JavaSymbolType.Comment));
    }

    private StateResult numericLiteral() {
        if (takeAll("0x", true)) {
            return hexLiteral();
        }
        return decimalLiteral();
    }

    private StateResult hexLiteral() {
        takeUntil(input -> input != null && !ParserHelpers.isHexDigit(input));
        takeIntegerSuffix();
        return stay(endSymbol(JavaSymbolType.IntegerLiteral));
    }

    private StateResult decimalLiteral() {
        takeUntil(Predicates.not(IsCharacterDigitPredicate));
        if (getCurrentChar() == '.' && Character.isDigit(peek())) {
            return realLiteral();
        }
        if (JavaHelpers.isRealLiteralSuffix(getCurrentChar()) || getCurrentChar() == 'E' || getCurrentChar() == 'e') {
            return realLiteralExponentPart();
        }
        takeIntegerSuffix();
        return stay(endSymbol(JavaSymbolType.IntegerLiteral));
    }

    private StateResult realLiteralExponentPart() {
        if (getCurrentChar() == 'E' || getCurrentChar() == 'e') {
            takeCurrent();
            if (getCurrentChar() == '+' || getCurrentChar() == '-') {
                takeCurrent();
            }
            takeUntil(Predicates.not(IsCharacterDigitPredicate));
        }
        if (JavaHelpers.isRealLiteralSuffix(getCurrentChar())) { takeCurrent(); }

        return stay(endSymbol(JavaSymbolType.RealLiteral));
    }

    private StateResult realLiteral() {
        assertCurrent('.');
        takeCurrent();
        assert Character.isDigit(getCurrentChar());
        takeUntil(Predicates.not(IsCharacterDigitPredicate));
        return realLiteralExponentPart();
    }

    private void takeIntegerSuffix() {
        if (Character.toLowerCase(getCurrentChar()) == 'l') {
            takeCurrent();
        }
    }

    private StateResult identifier() {
        if (Debug.isAssertEnabled()) assert JavaHelpers.isIdentifierStart(getCurrentChar());
        takeCurrent();
        takeUntil(Predicates.not(JavaHelpers.IsIdentifierPartPredicate));
        JavaSymbol sym = null;
        if (haveContent()) {
            final Optional<JavaKeyword> keyword = JavaKeywordDetector.symbolTypeForIdentifier(buffer.toString());
            JavaSymbolType type = JavaSymbolType.Identifier;
            if (keyword.isPresent()) {
                type = JavaSymbolType.Keyword;
            }
            sym = new JavaSymbol(getCurrentStart(), buffer.toString(), type);
            sym.setKeyword(keyword);
        }
        startSymbol();
        return stay(sym);
    }

    private static Delegates.IFunc<JavaSymbolType> createIAction(@Nonnull final JavaSymbolType type) {
        return () -> type;
    }

    public static final Predicate<Character> IsCharacterDigitPredicate = input -> input != null &&
                                                                                  Character.isDigit(input);
}
