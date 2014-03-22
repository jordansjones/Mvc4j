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

package nextmethod.web.razor;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.RazorError;

/**
 * Represents the results of parsing a Razor document.
 */
public class ParserResults {

    private final boolean success;
    private final Block document;
    private final List<RazorError> parserErrors;

    public ParserResults(@Nonnull final Block document, @Nullable final List<RazorError> parserErrors) {
        this(parserErrors == null || parserErrors.isEmpty(), document, parserErrors);
    }

    protected ParserResults(final boolean success, @Nonnull final Block document,
                            @Nullable final List<RazorError> parserErrors
                           ) {
        this.success = success;
        this.document = document;
        this.parserErrors = parserErrors != null
                            ? parserErrors
                            : Lists.<RazorError>newArrayList();
    }

    /**
     * Indicates if parsing was successful (no errors).
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * The root node in the document's syntax tree.
     */
    public Block getDocument() {
        return document;
    }

    /**
     * The list of errors which occurred during parsing.
     */
    public List<RazorError> getParserErrors() {
        return parserErrors;
    }
}
