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

import nextmethod.base.IDisposable;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
class HtmlMarkupParserDocument extends HtmlMarkupParserDelegate {

    protected HtmlMarkupParserDocument(final HtmlMarkupParser delegate) {
        super(delegate);
    }

    @Override
    public void parseDocument() {
        if (getContext() == null) {
            throw new UnsupportedOperationException(RazorResources().parserContextNotSet());
        }

        try (IDisposable ignored = pushSpanConfig(getBlockParser().defaultMarkupSpanDelegate)) {
            try (IDisposable ignored2 = getContext().startBlock(BlockType.Markup)) {
                nextToken();
                while (!isEndOfFile()) {
                    skipToAndParseCode(HtmlSymbolType.OpenAngle);
                    scanTagInDocumentContext();
                }
                addMarkerSymbolIfNecessary();
                output(SpanKind.Markup);
            }
        }
    }

    /**
     * Reads the content of a tag (if present) in the MarkupDocument (or MarkupSection) context,
     * where we don't care about maintaining a stack of tags.
     *
     * @return A boolean indicating if we scanned at least one tag.
     */
    boolean scanTagInDocumentContext() {
        if (optional(HtmlSymbolType.OpenAngle) && !at(HtmlSymbolType.Solidus)) {
            final boolean scriptTag =
                at(HtmlSymbolType.Text) && "script".equalsIgnoreCase(getCurrentSymbol().getContent());
            optional(HtmlSymbolType.Text);
            getBlockParser().tagContent(); // Parse the tag, don't care about the content
            optional(HtmlSymbolType.Solidus);
            optional(HtmlSymbolType.CloseAngle);
            if (scriptTag) {
                getBlockParser().skipToEndScriptAndParseCode();
            }
            return true;
        }
        return false;
    }
}
