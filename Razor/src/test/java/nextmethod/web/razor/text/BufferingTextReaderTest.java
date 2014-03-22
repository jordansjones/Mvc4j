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

import org.junit.Test;

public class BufferingTextReaderTest extends LookaheadTextReaderTestBase {

    private static final String TestString = "abcdefg";

    @Override
    protected LookaheadTextReader createReader(@Nonnull String testString) {
        return new BufferingTextReader(new StringReaderDelegate(testString));
    }

    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullSourceReader() {
        new BufferingTextReader(null);
    }

    @Test
    public void peekReturnsCurrentCharacterWithoutAdvancingPosition() {
        runPeekTest("abc", 2);
    }

    @Test
    public void peekReturnsNegativeOneAtEndOfSourceReader() {
        runPeekTest("abc", 3);
    }

    @Test
    public void readReturnsCurrentCharacterAndAdvancesToNextCharacter() {
        runReadTest("abc", 2);
    }

    @Test
    public void endingLookaheadReturnsReaderToPreviousLocation() {
        final Func<StringBuilder, LookaheadTextReader> readerFunc = this::read;
        runLookaheadTest(
                            "abcdefg", "abcb",
                            readerFunc,
                            lookAhead(readerFunc, readerFunc),
                            readerFunc
                        );
    }

    @Test
    public void multipleLookaheadsCanBePerformed() {
        final Func<StringBuilder, LookaheadTextReader> readerFunc = this::read;
        runLookaheadTest(
                            "abcdefg", "abcbcdc",
                            readerFunc,
                            lookAhead(readerFunc, readerFunc),
                            readerFunc,
                            lookAhead(readerFunc, readerFunc),
                            readerFunc
                        );
    }
}
