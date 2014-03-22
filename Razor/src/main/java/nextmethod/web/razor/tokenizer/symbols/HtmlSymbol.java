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

import com.google.common.collect.Lists;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.SourceLocation;

public class HtmlSymbol extends SymbolBase<HtmlSymbolType> {

    public HtmlSymbol(final int offset, final int line, final int column, @Nonnull final String content,
                      @Nonnull final HtmlSymbolType type
                     ) {
        this(new SourceLocation(offset, line, column), content, type, Lists.<RazorError>newArrayList());
    }

    public HtmlSymbol(@Nonnull final SourceLocation start, @Nonnull final String content,
                      @Nonnull final HtmlSymbolType type
                     ) {
        this(start, content, type, Lists.<RazorError>newArrayList());
    }

    public HtmlSymbol(@Nonnull final SourceLocation start, @Nonnull final String content,
                      @Nonnull final HtmlSymbolType htmlSymbolType, final Iterable<RazorError> errors
                     ) {
        super(start, content, htmlSymbolType, errors);
    }

    public HtmlSymbol(final int offset, final int line, final int column, @Nonnull final String content,
                      @Nonnull final HtmlSymbolType type, final Iterable<RazorError> errors
                     ) {
        this(new SourceLocation(offset, line, column), content, type, errors);
    }
}

