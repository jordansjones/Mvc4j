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

import nextmethod.base.IDisposable;
import nextmethod.web.razor.text.ITextBuffer;

import static com.google.common.base.Preconditions.checkNotNull;

public class StringTextBuffer implements ITextBuffer, IDisposable {

    private final char[] buffer;
    private boolean disposed;
    private int position;

    public StringTextBuffer(final String buffer) {
        checkNotNull(buffer);
        this.buffer = buffer.toCharArray();
    }

    @Override
    public void close() {
        disposed = true;
    }

    @Override
    public int getLength() {
        return buffer.length;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int read() {
        if (position >= buffer.length) {
            return -1;
        }
        return buffer[position++];
    }

    @Override
    public int peek() {
        if (position >= buffer.length) {
            return -1;
        }
        return buffer[position];
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void setDisposed(boolean disposed) {
        this.disposed = disposed;
    }
}
