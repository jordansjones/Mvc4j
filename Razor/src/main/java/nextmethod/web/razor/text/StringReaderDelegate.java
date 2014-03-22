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

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import javax.annotation.Nonnull;

import com.google.common.base.Throwables;

public class StringReaderDelegate extends TextReader {

    private final StringReader reader;

    public StringReaderDelegate(@Nonnull final String content) {
        this.reader = new StringReader(content);
    }

    @Override
    public int peek() {
        try {
            this.reader.mark(1);
            return this.reader.read();
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
        finally {
            try {
                this.reader.reset();
            }
            catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    @Override
    protected void dispose(boolean disposing) {
        if (disposing) {
            this.reader.close();
        }
        super.dispose(disposing);
    }

    @Override
    public void mark(int readAheadLimit)
        throws IOException {
        reader.mark(readAheadLimit);
    }

    @Override
    public boolean markSupported() {
        return reader.markSupported();
    }

    @Override
    public int read() {
        try {
            return reader.read();
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public boolean ready() {
        try {
            return reader.ready();
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void reset() {
        try {
            reader.reset();
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public long skip(long ns) {
        try {
            return reader.skip(ns);
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public int read(char[] cbuf) {
        try {
            return reader.read(cbuf);
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public int read(CharBuffer target) {
        try {
            return reader.read(target);
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
