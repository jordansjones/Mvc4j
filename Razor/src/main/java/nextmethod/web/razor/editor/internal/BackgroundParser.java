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

package nextmethod.web.razor.editor.internal;

import nextmethod.annotations.Internal;
import nextmethod.base.IDisposable;
import nextmethod.base.IEventHandler;
import nextmethod.threading.CancellationToken;
import nextmethod.web.razor.DocumentParseCompleteEventArgs;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.editor.EditResult;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.TextChange;

@Internal
public class BackgroundParser implements IDisposable {

    private IEventHandler<DocumentParseCompleteEventArgs> resultsReadyHandler;
    private MainThreadState main;
    private BackgroundThread bg;

    public BackgroundParser(final RazorEngineHost host, final String fileName) {
        this.main = new MainThreadState(fileName);
        this.bg = new BackgroundThread(main, host, fileName);

        this.main.setResultsReadyHandler((sender, e) -> onResultsReady(e));
    }

    public boolean isIdle() {
        return main.isIdle();
    }

    public void start() {
        bg.start();
    }

    public void cancel() {
        main.cancel();
    }

    public void queueChange(final TextChange change) {
        main.queueChange(change);
    }

    @Override
    public void close() {
        main.close();
    }

    public IDisposable synchronizeMainThreadState() {
        return main.lock();
    }

    protected void onResultsReady(final DocumentParseCompleteEventArgs args) {
        if (resultsReadyHandler != null) {
            resultsReadyHandler.handleEvent(this, args);
        }
    }

    static boolean treesAreDifferent(final Block leftTree, final Block rightTree, final Iterable<TextChange> changes) {
        return treesAreDifferent(leftTree, rightTree, changes, CancellationToken.none());
    }

    static boolean treesAreDifferent(final Block leftTree, final Block rightTree, final Iterable<TextChange> changes,
                                     final CancellationToken cancelToken
                                    ) {

        // Apply all pending changes to the original tree
        for (TextChange change : changes) {
            cancelToken.throwIfCancellationRequested();
            final Span changeOwner = leftTree.locateOwner(change);

            // Apply the change to the tree
            if (changeOwner == null) {
                return true;
            }
            final EditResult editResult = changeOwner.getEditHandler().applyChange(changeOwner, change, true);
            changeOwner.replaceWith(editResult.getEditedSpan());
        }

        // Now compare the trees
        return !leftTree.equivalentTo(rightTree);
    }

    public void setResultsReadyHandler(final IEventHandler<DocumentParseCompleteEventArgs> resultsReadyHandler) {
        this.resultsReadyHandler = resultsReadyHandler;
    }
}
