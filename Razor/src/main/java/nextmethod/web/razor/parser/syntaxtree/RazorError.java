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

package nextmethod.web.razor.parser.syntaxtree;

import javax.annotation.Nonnull;

import nextmethod.web.razor.text.SourceLocation;

public class RazorError {

    private final String message;
    private final SourceLocation location;
    private final int length;

    public RazorError(@Nonnull final String message, @Nonnull final SourceLocation location) {
        this(message, location, 1);
    }

    public RazorError(@Nonnull final String message, final int absoluteIndex, final int lineIndex,
                      final int columnIndex
                     ) {
        this(message, new SourceLocation(absoluteIndex, lineIndex, columnIndex));
    }

    public RazorError(@Nonnull final String message, final int absoluteIndex, final int lineIndex,
                      final int columnIndex, final int length
                     ) {
        this(message, new SourceLocation(absoluteIndex, lineIndex, columnIndex), length);
    }

    public RazorError(String message, SourceLocation location, int length) {
        this.message = message;
        this.location = location;
        this.length = length;
    }

    @Override
    public String toString() {
        return String.format("Error @ %s(%s) - [%d]", location, message, length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RazorError)) return false;

        RazorError that = (RazorError) o;
        return message.equals(that.message) && SourceLocation.isEqual(location, that.location);
    }

    @Override
    public int hashCode() {
        int result = message != null
                     ? message.hashCode()
                     : 0;
        result = 31 * result + (location != null
                                ? location.hashCode()
                                : 0);
        result = 31 * result + length;
        return result;
    }
}
