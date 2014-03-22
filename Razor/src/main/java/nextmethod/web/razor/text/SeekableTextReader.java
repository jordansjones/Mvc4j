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

import java.io.Reader;
import javax.annotation.Nonnull;

import com.google.common.base.Optional;

public class SeekableTextReader extends TextReader implements ITextDocument {

    private int position = 0;
    private LineTrackingStringBuffer buffer = new LineTrackingStringBuffer();
    private SourceLocation location = SourceLocation.Zero;
    private Optional<Character> current;

    public SeekableTextReader(@Nonnull final String content) {
        buffer.append(content);
        updateState();
    }

    public SeekableTextReader(@Nonnull final Reader reader) {
        this(TextExtensions.readToEnd(reader));
    }

    public SeekableTextReader(@Nonnull final ITextBuffer buffer) {
        this(TextExtensions.readToEnd(buffer));
    }

    @Override
    public SourceLocation getLocation() {
        return location;
    }

    @Override
    public int getLength() {
        return buffer.length();
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        if (this.position != position) {
            this.position = position;
            updateState();
        }
    }

    @Override
    public int read() {
        if (current == null || !current.isPresent()) { return -1; }

        final Character chr = current.get();
        position++;
        updateState();
        return chr;
    }

    @Override
    public int peek() {
        if (current == null || !current.isPresent()) { return -1; }
        return current.get();
    }

    private void updateState() {
        final int len = buffer.length();
        if (position < len) {
            final LineTrackingStringBuffer.CharRef chr = buffer.charAt(position);
            current = Optional.of(chr.get());
            location = chr.location();
        }
        else if (len == 0) {
            current = Optional.absent();
            location = SourceLocation.Zero;
        }
        else {
            current = Optional.absent();
            location = buffer.getEndLocation();
        }
    }
}
