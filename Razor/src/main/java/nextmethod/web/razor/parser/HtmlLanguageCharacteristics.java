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

import javax.annotation.Nonnull;

import nextmethod.base.Debug;
import nextmethod.base.Strings;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.HtmlTokenizer;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import nextmethod.web.razor.tokenizer.symbols.KnownSymbolType;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class HtmlLanguageCharacteristics extends LanguageCharacteristics<HtmlTokenizer, HtmlSymbol, HtmlSymbolType> {

    public static final HtmlLanguageCharacteristics Instance = new HtmlLanguageCharacteristics();

    @Override
    public String getSample(@Nonnull final HtmlSymbolType type) {
        switch (type) {
            case Text:
                return RazorResources().htmlSymbolText();
            case WhiteSpace:
                return RazorResources().htmlSymbolWhiteSpace();
            case NewLine:
                return RazorResources().htmlSymbolNewLine();
            case OpenAngle:
                return "<";
            case Bang:
                return "!";
            case QuestionMark:
                return "?";
            case DoubleHyphen:
                return "--";
            case LeftBracket:
                return "[";
            case CloseAngle:
                return ">";
            case RightBracket:
                return "]";
            case Equals:
                return "=";
            case DoubleQuote:
                return "\"";
            case SingleQuote:
                return "'";
            case Transition:
                return "@";
            case Colon:
                return ":";
            case RazorComment:
                return RazorResources().htmlSymbolRazorComment();
            case RazorCommentStar:
                return "*";
            case RazorCommentTransition:
                return "@";
            default:
                return RazorResources().symbolUnknown();
        }
    }

    @Override
    public HtmlTokenizer createTokenizer(@Nonnull final ITextDocument source) {
        return new HtmlTokenizer(source);
    }

    @Override
    public HtmlSymbolType flipBracket(@Nonnull final HtmlSymbolType bracket) {
        switch (bracket) {
            case LeftBracket:
                return HtmlSymbolType.RightBracket;
            case OpenAngle:
                return HtmlSymbolType.CloseAngle;
            case RightBracket:
                return HtmlSymbolType.LeftBracket;
            case CloseAngle:
                return HtmlSymbolType.OpenAngle;
            default:
                Debug.fail("flipBracket must be called with a bracket character");
                return HtmlSymbolType.Unknown;
        }
    }

    @Override
    public HtmlSymbol createMarkerSymbol(@Nonnull final SourceLocation location) {
        return new HtmlSymbol(location, Strings.Empty, HtmlSymbolType.Unknown);
    }

    @Override
    public HtmlSymbolType getKnownSymbolType(@Nonnull final KnownSymbolType type) {
        switch (type) {
            case CommentStart:
                return HtmlSymbolType.RazorCommentTransition;
            case CommentStar:
                return HtmlSymbolType.RazorCommentStar;
            case CommentBody:
                return HtmlSymbolType.RazorComment;
            case Identifier:
                return HtmlSymbolType.Text;
            case Keyword:
                return HtmlSymbolType.Text;
            case NewLine:
                return HtmlSymbolType.NewLine;
            case Transition:
                return HtmlSymbolType.Transition;
            case WhiteSpace:
                return HtmlSymbolType.WhiteSpace;
            default:
                return HtmlSymbolType.Unknown;
        }
    }

    @Override
    protected HtmlSymbol createSymbol(@Nonnull final SourceLocation location, @Nonnull final String content,
                                      @Nonnull final HtmlSymbolType htmlSymbolType,
                                      @Nonnull final Iterable<RazorError> errors
                                     ) {
        return new HtmlSymbol(location, content, htmlSymbolType, errors);
    }
}
