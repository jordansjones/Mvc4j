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

import nextmethod.base.IDisposable;

public class TextReader extends Reader implements IDisposable {

    public static final TextReader Null = new TextReader();

    public int read() {
        return -1;
    }

    public int peek() {
        return -1;
    }

    @Override
    public int read(char[] cbuf, int off, int len) {
        int c, i;
        for (i = 0; i < len; i++) {
            if ((c = read()) == -1) { return i; }
            cbuf[off + i] = (char) c;
        }

        return i;
    }

    public String readToEnd() {
        final StringBuilder sb = new StringBuilder();
        int c;
        while ((c = read()) != -1)
            sb.append((char) c);

        return sb.toString();
    }

    @Override
    public final void close() {
        dispose(true);
    }

    protected void dispose(final boolean disposing) {

    }
}
