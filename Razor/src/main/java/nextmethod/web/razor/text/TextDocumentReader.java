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

import javax.annotation.Nonnull;

public class TextDocumentReader extends TextReader implements ITextDocument, AutoCloseable {

    private final ITextDocument document;

    public TextDocumentReader(@Nonnull final ITextDocument document) {
        this.document = document;
    }

    @Override
    public SourceLocation getLocation() {
        return document.getLocation();
    }

    @Override
    public int getLength() {
        return document.getLength();
    }

    @Override
    public int getPosition() {
        return document.getPosition();
    }

    @Override
    public void setPosition(int position) {
        document.setPosition(position);
    }

    @Override
    public int read() {
        return document.read();
    }

    @Override
    public int peek() {
        return document.peek();
    }

}
