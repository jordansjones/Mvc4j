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

package nextmethod.web.razor.tokenizer.symbols;

public enum HtmlSymbolType {

    Unknown,
    Text, // Text which isn't one of the below
    WhiteSpace, // Non-newline WhiteSpace
    NewLine,
    OpenAngle, // <
    Bang, // !
    Solidus, // /
    QuestionMark, // ?
    DoubleHyphen, // --
    LeftBracket, // [
    CloseAngle, // >
    RightBracket, // ]
    Equals, // =
    DoubleQuote, // "
    SingleQuote, // '
    Transition, // @
    Colon,
    RazorComment,
    RazorCommentStar,
    RazorCommentTransition
}
