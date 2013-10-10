/*
 * Copyright 2013 Jordan S. Jones <jordansjones@gmail.com>
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

import com.google.common.base.Function;
import nextmethod.base.IDisposable;
import nextmethod.base.OutParam;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.junit.Assert.assertEquals;

public abstract class LookaheadTextReaderTestBase {

	protected abstract LookaheadTextReader createReader(@Nonnull final String testString);

	protected void runPeekTest(@Nonnull final String input, int peekAt) {
		runPeekOrReadTest(input, peekAt, false);
	}

	protected void runReadTest(@Nonnull final String input, int readAt) {
		runPeekOrReadTest(input, readAt, true);
	}

	protected void runSourceLocationTest(@Nonnull final String input, @Nonnull final SourceLocation expected, final int checkAt) {
		runSourceLocationTest(input, expected, input1 -> {
			advanceReader(checkAt, input1);
			return input1;
		});
	}

	protected void runSourceLocationTest(@Nonnull final String input, @Nonnull final SourceLocation expected, @Nonnull final Function<LookaheadTextReader, LookaheadTextReader> readerAction) {
		final LookaheadTextReader reader = createReader(input);
		readerAction.apply(reader);

		final SourceLocation actual = reader.getCurrentLocation();
		assertEquals(expected, actual);
	}

	@SuppressWarnings("unchecked")
	protected void runEndLookaheadUpdatesSourceLocationTest() {
		final OutParam<SourceLocation> expectedLocation = OutParam.of();
		final OutParam<SourceLocation> actualLocation = OutParam.of();
		runLookaheadTest(
			"abc\r\ndef\r\nghi",
			null,
			read(6),
			captureSourceLocation(input -> {
				expectedLocation.set(input);
				return input;
			}),
			lookAhead(read(6)),
			captureSourceLocation(input -> {
				actualLocation.set(input);
				return input;
			})
		);

		assertEquals(expectedLocation.value().getAbsoluteIndex(), actualLocation.value().getAbsoluteIndex());
		assertEquals(expectedLocation.value().getCharacterIndex(), actualLocation.value().getCharacterIndex());
		assertEquals(expectedLocation.value().getLineIndex(), actualLocation.value().getLineIndex());
	}

	protected void runReadToEndTest() {
		final LookaheadTextReader reader = createReader("abcdefg");
		final String s = reader.readToEnd();
		assertEquals("abcdefg", s);
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	protected void runCancelBacktrackOutsideLookaheadTest() {
		expectedException.expect(UnsupportedOperationException.class);

		final LookaheadTextReader reader = createReader("abcdefg");
		reader.cancelBacktrack();
	}

	protected Func<StringBuilder, LookaheadTextReader> captureSourceLocation(@Nonnull final Function<SourceLocation, SourceLocation> capture) {
		return (inputOne, inputTwo) -> {
			capture.apply(inputTwo.getCurrentLocation());
		};
	}

	protected Func<StringBuilder, LookaheadTextReader> read(final int count) {
		return (inputOne, inputTwo) -> {
			for (int i = 0; i < count; i++) {
				read(inputOne, inputTwo);
			}
		};
	}

	protected void read(@Nonnull final StringBuilder builder, @Nonnull final LookaheadTextReader reader) {
		builder.append((char) reader.read());
	}

	protected void readtoEnd(@Nonnull final StringBuilder builder, @Nonnull final LookaheadTextReader reader) {
		builder.append(reader.readToEnd());
	}

	protected void cancelBacktrack(@Nonnull final StringBuilder builder, @Nonnull final LookaheadTextReader reader) {
		reader.cancelBacktrack();
	}

	@SafeVarargs
	protected final Func<StringBuilder, LookaheadTextReader> lookAhead(final Func<StringBuilder, LookaheadTextReader>... readerCommands) {
		return (builder, reader) -> {
			try (final IDisposable la = reader.beginLookahead()) {
				runAll(readerCommands, builder, reader);
			}
		};
	}

	@SafeVarargs
	protected final void runLookaheadTest(final String input, final String expected, final Func<StringBuilder, LookaheadTextReader>... readerCommands) {
		final StringBuilder sb = new StringBuilder();
		try(LookaheadTextReader reader = createReader(input)) {
			runAll(readerCommands, sb, reader);
		}
		if (expected != null)
			assertEquals(expected, sb.toString());
	}

	protected void runReadUntilTest(@Nonnull final Function<LookaheadTextReader, String> readMethod, final int expectedRaw, final int expectedChar, final int expectedLine) {
		final LookaheadTextReader reader = createReader("a\r\nbcd\r\nefg");
		reader.read(); // reader: "\r\nbcd\r\nefg"
		reader.read(); // reader: "\nbcd\r\nefg"
		reader.read(); // reader: "bcd\r\nefg"

		String read = null;
		SourceLocation actualLocation;
		try (IDisposable la = reader.beginLookahead()) {
			read = readMethod.apply(reader);
			actualLocation = reader.getCurrentLocation();
		}

		assertEquals(3, reader.getCurrentLocation().getAbsoluteIndex());
		assertEquals(0, reader.getCurrentLocation().getCharacterIndex());
		assertEquals(1, reader.getCurrentLocation().getLineIndex());
		assertEquals(expectedRaw, actualLocation.getAbsoluteIndex());
		assertEquals(expectedChar, actualLocation.getCharacterIndex());
		assertEquals(expectedLine, actualLocation.getLineIndex());
		assertEquals('b', reader.peek());
		assertEquals(read, readMethod.apply(reader));
	}

	protected void runBufferReadTest(@Nonnull final Func4<LookaheadTextReader, char[], Integer, Integer, Integer> readMethod) {
		final LookaheadTextReader reader = createReader("abcdefg");
		reader.read(); // Reader: "bcdefg"

		final char[] buffer = new char[4];
		int read = -1;
		SourceLocation actualLocation;
		try (IDisposable la = reader.beginLookahead()) {
			read = readMethod.apply(reader, buffer, 0, 4);
			actualLocation = reader.getCurrentLocation();
		}

		assertEquals("bcde", new String(buffer));
		assertEquals(4, read);
		assertEquals(5, actualLocation.getAbsoluteIndex());
		assertEquals(5, actualLocation.getCharacterIndex());
		assertEquals(0, actualLocation.getLineIndex());
		assertEquals(1, reader.getCurrentLocation().getCharacterIndex());
		assertEquals(0, reader.getCurrentLocation().getLineIndex());
		assertEquals('b', reader.peek());
	}

	private static void runAll(final Func<StringBuilder, LookaheadTextReader>[] readerCommands, @Nonnull final StringBuilder builder, @Nonnull final LookaheadTextReader reader) {
		for (Func<StringBuilder, LookaheadTextReader> readerCommand : readerCommands) {
			readerCommand.apply(builder, reader);
		}
	}

	private void runPeekOrReadTest(final String input, final int offset, final boolean isRead) {
		try (final LookaheadTextReader reader = createReader(input)) {
			advanceReader(offset, reader);

			final int actual = isRead ? reader.read() : reader.peek();

			assertReaderValueCorrect(actual, input, offset, "Peek");
			if (isRead)
				assertReaderValueCorrect(reader.peek(), input, offset + 1, "Read");
			else
				assertEquals(actual, reader.peek());
		}
	}

	private static void advanceReader(final int offset, @Nonnull final LookaheadTextReader reader) {
		for (int i = 0; i < offset; i++) {
			final char read = (char) reader.read();
		}
	}

	private void assertReaderValueCorrect(final int actual, final String input, final int expectedOffset, final String methodName) {
		if (expectedOffset < input.length())
			assertEquals(input.charAt(expectedOffset), (char) actual);
		else
			assertEquals(-1, actual);
	}

	protected interface Func4<POne, PTwo, PThree, PFour, RType> {
		public RType apply (@Nonnull final POne inputOne, @Nonnull final PTwo inputTwo, @Nonnull final PThree inputThree, @Nonnull final PFour inputFour);
	}

	protected interface Func<POne, PTwo> {
		public void apply(@Nonnull final POne inputOne, @Nonnull final PTwo inputTwo);
	}
}
