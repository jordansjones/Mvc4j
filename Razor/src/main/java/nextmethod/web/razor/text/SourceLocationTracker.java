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

import nextmethod.web.razor.parser.ParserHelpers;

public class SourceLocationTracker {

    private int absoluteIndex = 0;
    private int lineIndex = 0;
    private int characterIndex = 0;
    private SourceLocation currentLocation;

    public SourceLocationTracker() {
        this(SourceLocation.Zero);
    }

    public SourceLocationTracker(@Nonnull final SourceLocation currentLocation) {
        this.currentLocation = currentLocation;
        updateInternalState();
    }

    public SourceLocation getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(final SourceLocation currentLocation) {
        if (SourceLocation.isNotEqual(this.currentLocation, currentLocation)) {
            this.currentLocation = currentLocation;
            updateInternalState();
        }
    }

    public void updateLocation(final char charRead, final char nextChar) {
        absoluteIndex++;
        if (ParserHelpers.isNewLine(charRead) && (charRead != '\r' || nextChar != '\n')) {
            lineIndex++;
            characterIndex = 0;
        }
        else {
            characterIndex++;
        }
        updateLocation();
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public SourceLocationTracker updateLocation(@Nonnull final String content) {
        final char[] chars = content.toCharArray();
        final int len = chars.length - 1;
        for (int i = 0; i < chars.length; i++) {
            char nextChar = '\0';
            if (i < len) {
                nextChar = chars[i + 1];
            }
            updateLocation(chars[i], nextChar);
        }
        return this;
    }

    private void updateLocation() {
        setCurrentLocation(new SourceLocation(absoluteIndex, lineIndex, characterIndex));
    }

    private void updateInternalState() {
        absoluteIndex = currentLocation.getAbsoluteIndex();
        lineIndex = currentLocation.getLineIndex();
        characterIndex = currentLocation.getCharacterIndex();
    }

    public static SourceLocation calculateNewLocation(@Nonnull final SourceLocation lastPosition,
                                                      @Nonnull final String newContent
                                                     ) {
        return new SourceLocationTracker(lastPosition).updateLocation(newContent).currentLocation;
    }
}
