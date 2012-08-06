package nextmethod.web.razor.text;

import org.junit.Test;

import javax.annotation.Nonnull;

public class BufferingTextReaderTest extends LookaheadTextReaderTestBase {

	private static final String TestString = "abcdefg";

	@Override
	protected LookaheadTextReader createReader(@Nonnull String testString) {
		return new BufferingTextReader(new StringReaderDelegate(testString));
	}

	@Test(expected = NullPointerException.class)
	public void constractorRequiresNonNullSourceReader() {
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
		final Func<StringBuilder, LookaheadTextReader> readerFunc = new Func<StringBuilder, LookaheadTextReader>() {
			public void apply(StringBuilder sb, LookaheadTextReader reader) {
				read(sb, reader);
			}
		};
		runLookaheadTest("abcdefg", "abcb",
				readerFunc,
				lookAhead(readerFunc, readerFunc),
				readerFunc
			);
	}

	@Test
	public void multipleLookaheadsCanBePerformed() {
		final Func<StringBuilder, LookaheadTextReader> readerFunc = new Func<StringBuilder, LookaheadTextReader>() {
			public void apply(StringBuilder sb, LookaheadTextReader reader) {
				read(sb, reader);
			}
		};
		runLookaheadTest("abcdefg", "abcbcdc",
			readerFunc,
			lookAhead(readerFunc, readerFunc),
			readerFunc,
			lookAhead(readerFunc, readerFunc),
			readerFunc
		);
	}
}
