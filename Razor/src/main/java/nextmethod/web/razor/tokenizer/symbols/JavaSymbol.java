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

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.SourceLocation;

public class JavaSymbol extends SymbolBase<JavaSymbolType> {

    private Optional<Boolean> escapedIdentifier = Optional.absent();
    private Optional<JavaKeyword> keyword = Optional.absent();

    public JavaSymbol(final int offset, final int line, final int column, @Nonnull final String content,
                      @Nonnull final JavaSymbolType type
                     ) {
        this(new SourceLocation(offset, line, column), content, type, Lists.<RazorError>newArrayList());
    }

    public JavaSymbol(@Nonnull final SourceLocation start, @Nonnull final String content,
                      @Nonnull final JavaSymbolType type
                     ) {
        this(start, content, type, Lists.<RazorError>newArrayList());
    }

    public JavaSymbol(final int offset, final int line, final int column, @Nonnull final String content,
                      @Nonnull final JavaSymbolType type, @Nonnull final Iterable<RazorError> errors
                     ) {
        this(new SourceLocation(offset, line, column), content, type, errors);
    }

    public JavaSymbol(@Nonnull final SourceLocation start, @Nonnull final String content,
                      @Nonnull final JavaSymbolType type, @Nonnull final Iterable<RazorError> errors
                     ) {
        super(start, content, type, errors);
    }


    public Optional<Boolean> getEscapedIdentifier() {
        return escapedIdentifier;
    }

    public void setEscapedIdentifier(final Boolean escapedIdentifier) {
        this.escapedIdentifier = Optional.fromNullable(escapedIdentifier);
    }

    public Optional<JavaKeyword> getKeyword() {
        return keyword;
    }

    public void setKeyword(final JavaKeyword keyword) {
        this.keyword = Optional.fromNullable(keyword);
    }

    public void setKeyword(@Nonnull final Optional<JavaKeyword> keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JavaSymbol)) return false;
        if (!super.equals(o)) return false;

        JavaSymbol that = (JavaSymbol) o;
        return super.equals(that) && that.keyword.equals(keyword);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (keyword != null
                                ? keyword.hashCode()
                                : 0);
        return result;
    }
}
