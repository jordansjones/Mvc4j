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

import java.util.List;

import com.google.common.collect.Lists;
import nextmethod.web.razor.parser.ParserHelpers;

final class LineTrackingStringBuffer {

    private TextLine currentLine;
    private TextLine endLine;
    private final List<TextLine> lines;

    LineTrackingStringBuffer() {
        this.endLine = new TextLine(0, 0);
        this.lines = Lists.newArrayList(this.endLine);
    }

    public int length() {
        return endLine.end();
    }

    public LineTrackingStringBuffer append(final String content) {
        for (int i = 0, size = content.length(); i < size; i++) {
            final char c = content.charAt(i);
            appendCore(c);

            if ((c == '\r' && (i + 1 == size || content.charAt(i + 1) != '\n')) ||
                (c != '\r' && ParserHelpers.isNewLine(c))) {
                pushNewLine();
            }
        }
        return this;
    }

    public CharRef charAt(int absoluteIndex) {
        final TextLine line = findLine(absoluteIndex);
        if (line == null) { throw new ArrayIndexOutOfBoundsException(absoluteIndex); }

        final int idx = absoluteIndex - line.start();
        return new CharRef(line.content().charAt(idx), new SourceLocation(absoluteIndex, line.index(), idx));
    }

    private void pushNewLine() {
        endLine = new TextLine(endLine.end(), endLine.index() + 1);
        lines.add(endLine);
    }

    private void appendCore(char chr) {
        assert !lines.isEmpty();
        lines.get(lastLineOffset()).content().append(chr);
    }

    private int lastLineOffset() {
        return lines.size() - 1;
    }

    public SourceLocation getEndLocation() {
        final int offset = lastLineOffset();
        return new SourceLocation(length(), offset, lines.get(offset).length());
    }

    private TextLine findLine(final int absIdx) {
        TextLine selected = null;
        if (currentLine != null) {
            if (currentLine.contains(absIdx)) {
                selected = currentLine;
            }
            else if (absIdx > currentLine.index() && currentLine.index() + 1 < lines.size()) {
                selected = scanLines(absIdx, currentLine.index());
            }
        }

        if (selected == null) { selected = scanLines(absIdx, 0); }

        assert selected == null || selected.contains(absIdx);
        currentLine = selected;
        return selected;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    private TextLine scanLines(int absIdx, int startPos) {
        for (int i = 0, size = lines.size(); i < size; i++) {
            int idx = (i + startPos) % size;
            assert idx >= 0 && idx < size;

            if (lines.get(idx).contains(absIdx)) { return lines.get(idx); }
        }
        return null;
    }

    public static class CharRef {

        private final char val;
        private final SourceLocation location;

        public CharRef(char val, SourceLocation location) {
            this.val = val;
            this.location = location;
        }

        public char get() {
            return val;
        }

        public SourceLocation location() {
            return location;
        }
    }

    private class TextLine {

        private final StringBuilder content;
        private int start;
        private int index;

        private TextLine(int start, int index) {
            this.start = start;
            this.index = index;
            this.content = new StringBuilder();
        }

        public StringBuilder content() {
            return content;
        }

        public int start() {
            return start;
        }

        public int index() {
            return index;
        }

        public int end() {
            return start() + length();
        }

        public int length() {
            return content.length();
        }

        public boolean contains(final int index) {
            return index < end() && index >= start();
        }

    }
}
