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

public final class SyntaxConstants {

    private SyntaxConstants() {
    }

    public static final String TextTagName = "text";
    public static final char TransitionCharacter = '@';
    public static final String TransitionString = "@";
    public static final String StartCommentSequence = "@*";
    public static final String EndCommentSequence = "*@";

    public static final class Java {

        private Java() {
        }

        public static final int UsingKeywordLength = 5;
        public static final String InheritsKeyword = "inherits";
        public static final String FunctionsKeyword = "functions";
        public static final String SectionKeyword = "section";
        public static final String HelperKeyword = "helper";
        public static final String ElseIfKeyword = "else if";
        public static final String PackageKeyword = "package";
        public static final String NamespaceKeyword = "namespace";
        public static final String ClassKeyword = "class";
        public static final String LayoutKeyword = "layout";
        public static final String SessionStateKeyword = "sessionstate";
    }
}
