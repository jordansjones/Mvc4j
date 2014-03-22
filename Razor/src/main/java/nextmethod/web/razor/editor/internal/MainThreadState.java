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

import java.util.List;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Monitor;
import nextmethod.annotations.Internal;
import nextmethod.base.IDisposable;
import nextmethod.base.IEventHandler;
import nextmethod.io.Filesystem;
import nextmethod.threading.CancellationToken;
import nextmethod.threading.CancellationTokenSource;
import nextmethod.web.razor.DocumentParseCompleteEventArgs;
import nextmethod.web.razor.text.TextChange;
import nextmethod.web.razor.utils.DisposableAction;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

@Internal
final class MainThreadState extends BaseThreadState implements IDisposable {

    private final Monitor stateLock = new Monitor();
    private final Monitor.Guard hasParcel = new Monitor.Guard(stateLock) {

        @Override
        public boolean isSatisfied() {
            return !changes.isEmpty();
        }
    };


    private final CancellationTokenSource cancelSource = new CancellationTokenSource();
    private CancellationTokenSource currentParcelCancelSource;

    private String fileName;
    private volatile List<TextChange> changes = Lists.newArrayList();
    private IEventHandler<DocumentParseCompleteEventArgs> resultsReadyHandler;

    MainThreadState(@Nonnull final String fileName) {
        this.fileName = checkNotNull(fileName);
        setThreadId(Thread.currentThread().getId());
    }

    public CancellationToken getCancelToken() {
        return cancelSource.getToken();
    }

    public boolean isIdle() {
        stateLock.enter();
        try {
            return currentParcelCancelSource == null;
        }
        finally {
            stateLock.leave();
        }
    }

    public void cancel() {
        ensureOnThread();
        cancelSource.cancel();
    }

    public IDisposable lock() {
        stateLock.enter();
        return new DisposableAction(stateLock::leave);
    }

    public void queueChange(@Nonnull final TextChange change) {
        RazorEditorTrace.traceLine(
                                      RazorResources().traceQueuingParse(), Filesystem.getFileName(fileName),
                                      checkNotNull(change)
                                  );

        ensureOnThread();
        stateLock.enter();
        try {
            // CurrentParcel token source is not null ==> There's a parse underway
            if (currentParcelCancelSource != null) {
                currentParcelCancelSource.cancel();
            }
            changes.add(change);
        }
        finally {
            stateLock.leave();
        }
    }

    public WorkParcel getParcel() {
        ensureNotOnThread(); // Only the background thread can get a parcel
        stateLock.enterWhenUninterruptibly(hasParcel);
        try {
            // Create a cancellation source for this parcel
            currentParcelCancelSource = new CancellationTokenSource();

            final ImmutableList<TextChange> textChanges = ImmutableList.copyOf(changes);
            this.changes = Lists.newArrayList();
            return new WorkParcel(textChanges, currentParcelCancelSource.getToken());
        }
        finally {
            stateLock.leave();
        }
    }

    public void returnParcel(@Nonnull final DocumentParseCompleteEventArgs args) {
        stateLock.enter();
        try {
            // Clear the current parcel cancellation source
            if (currentParcelCancelSource != null) {
                currentParcelCancelSource.close();
                currentParcelCancelSource = null;
            }

            // If there are things waiting to be parsed, just don't fire the event because we're already out of date
            if (!changes.isEmpty()) {
                return;
            }

            if (resultsReadyHandler != null) {
                resultsReadyHandler.handleEvent(this, args);
            }
        }
        finally {
            stateLock.leave();
        }
    }

    @Override
    public void close() {
        close(true);
    }

    protected void close(boolean closing) {
        if (closing) {
            if (currentParcelCancelSource != null) {
                currentParcelCancelSource.close();
                currentParcelCancelSource = null;
            }
            cancelSource.close();
        }
    }

    public void setResultsReadyHandler(final IEventHandler<DocumentParseCompleteEventArgs> resultsReadyHandler) {
        this.resultsReadyHandler = resultsReadyHandler;
    }
}
