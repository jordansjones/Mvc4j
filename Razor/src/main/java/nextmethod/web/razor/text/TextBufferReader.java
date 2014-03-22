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

package nextmethod.web.razor.text;

import java.util.Deque;
import javax.annotation.Nonnull;

import com.google.common.collect.Queues;
import nextmethod.base.IDisposable;
import nextmethod.web.razor.utils.DisposableAction;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class TextBufferReader extends LookaheadTextReader {

    private final Deque<BacktrackContext> bookmarks = Queues.newArrayDeque();
    private SourceLocationTracker tracker = new SourceLocationTracker();

    private final ITextBuffer innerBuffer;

    public TextBufferReader(@Nonnull final ITextBuffer buffer) {
        this.innerBuffer = checkNotNull(buffer);
    }

    @Override
    public SourceLocation getCurrentLocation() {
        return tracker.getCurrentLocation();
    }

    @Override
    public int peek() {
        return innerBuffer.peek();
    }

    @Override
    public int read() {
        final int read = innerBuffer.read();
        if (read != -1) {
            char nextChar = '\0';
            final int next = peek();
            if (next != -1) { nextChar = (char) next; }

            tracker.updateLocation((char) read, nextChar);
        }
        return read;
    }

    @Override
    protected void dispose(boolean disposing) {
        if (disposing) {
            if (innerBuffer instanceof IDisposable) {
                IDisposable.class.cast(innerBuffer).close();
            }
        }
        super.dispose(disposing);
    }

    @Override
    public IDisposable beginLookahead() {
        final BacktrackContext context = new BacktrackContext(getCurrentLocation());
        bookmarks.push(context);
        return new DisposableAction(() -> endLookahead(context));
    }

    @Override
    public void cancelBacktrack() {
        if (bookmarks.isEmpty()) {
            throw new UnsupportedOperationException(RazorResources().cancelBacktrackMustBeCalledWithinLookahead());
        }

        bookmarks.pop();
    }

    private void endLookahead(@Nonnull final BacktrackContext context) {
        if (!bookmarks.isEmpty() && bookmarks.peek() == context) {
            // Backtrack wasn't cancelled, so pop it
            bookmarks.pop();
            // Set the new current location
            tracker.setCurrentLocation(context.getLocation());
            innerBuffer.setPosition(context.getLocation().getAbsoluteIndex());
        }
    }

    private class BacktrackContext {

        private final SourceLocation location;

        private BacktrackContext(@Nonnull final SourceLocation location) {
            this.location = location;
        }

        public SourceLocation getLocation() {
            return location;
        }
    }
}
