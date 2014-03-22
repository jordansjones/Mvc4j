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

import javax.annotation.Nonnull;

public enum JavaKeyword {
    Abstract("abstract"),
    Assert("assert"),
    Boolean("boolean"),
    Break("break"),
    Byte("byte"),
    Case("case"),
    Catch("catch"),
    Char("char"),
    Class("class"),
    Const("const"),
    Continue("continue"),
    Default("default"),
    Do("do"),
    Double("double"),
    Else("else"),
    Enum("enum"),
    Extends("extends"),
    Final("final"),
    Finally("finally"),
    Float("float"),
    For("for"),
    Foreach("foreach"),
    Goto("goto"),
    If("if"),
    Implements("implements"),
    Import("import"),
    Instanceof("instanceof"),
    Int("int"),
    Interface("interface"),
    Lock("lock"),
    Long("long"),
    Namespace("namespace"),
    Native("native"),
    New("new"),
    Package("package"),
    Private("private"),
    Protected("protected"),
    Public("public"),
    Return("return"),
    Short("short"),
    Static("static"),
    Strictfp("strictfp"),
    Super("super"),
    Switch("switch"),
    Synchronized("synchronized"),
    This("this"),
    Throw("throw"),
    Throws("throws"),
    Transient("transient"),
    Try("try"),
    Using("using"),
    Void("void"),
    Volatile("volatile"),
    While("while"),

    Null("null"),
    False("false"),
    True("true"),;

    private final String keyword;

    private JavaKeyword(@Nonnull final String keyword) {
        this.keyword = keyword;
    }

    public String keyword() {
        return keyword;
    }
}
