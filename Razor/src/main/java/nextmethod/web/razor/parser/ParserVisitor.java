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

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nextmethod.threading.CancellationToken;
import nextmethod.threading.OperationCanceledException;
import nextmethod.web.razor.ParserResults;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;

/**
 *
 */
public abstract class ParserVisitor {

    private Optional<CancellationToken> cancelToken = Optional.empty();

    public void visitBlock(@Nonnull final Block block) {
        visitStartBlock(block);
        for (SyntaxTreeNode node : block.getChildren()) {
            node.accept(this);
        }
        visitEndBlock(block);
    }

    public void visitStartBlock(@Nonnull final Block block) {
        throwIfCanceled();
    }

    public void visitSpan(@Nonnull final Span span) {
        throwIfCanceled();
    }

    public void visitEndBlock(@Nonnull final Block block) {
        throwIfCanceled();
    }

    public void visitError(@Nonnull final RazorError error) {
        throwIfCanceled();
    }

    public void onComplete() {
        throwIfCanceled();
    }

    public void throwIfCanceled() {
        if (cancelToken != null && cancelToken.isPresent() && cancelToken.get().isCancellationRequested()) {
            throw new OperationCanceledException();
        }
    }

    public void visit(@Nonnull final ParserResults result) {
        result.getDocument().accept(this);
        result.getParserErrors().forEach(this::visitError);
        onComplete();
    }

    @Nullable
    public Optional<CancellationToken> getCancelToken() {
        return cancelToken;
    }

    public ParserVisitor setCancelToken(@Nullable final Optional<CancellationToken> cancelToken) {
        this.cancelToken = cancelToken;
        return this;
    }

    public ParserVisitor setCancelToken(@Nullable final CancellationToken cancelToken) {
        return setCancelToken(Optional.ofNullable(cancelToken));
    }
}
