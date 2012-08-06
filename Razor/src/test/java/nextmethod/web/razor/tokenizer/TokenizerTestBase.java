package nextmethod.web.razor.tokenizer;

import nextmethod.SystemHelpers;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SeekableTextReader;
import nextmethod.web.razor.tokenizer.symbols.SymbolBase;

import java.io.StringReader;

import static org.junit.Assert.assertTrue;

public abstract class TokenizerTestBase<TSymbol extends SymbolBase<TSymbolType>, TSymbolType> {

	protected abstract TSymbol getIgnoreRemaining();
	protected abstract Tokenizer<TSymbol, TSymbolType> createTokenizer(final ITextDocument source);

	@SafeVarargs
	protected final void testTokenizer(final String input, final TSymbol... symbols) {
		boolean success = true;
		final StringBuilder output = new StringBuilder();
		try (final StringReader reader = new StringReader(input)) {
			final SeekableTextReader source = new SeekableTextReader(reader);
			final Tokenizer<TSymbol, TSymbolType> tokenizer = createTokenizer(source);
			int counter = 0;
			TSymbol current = null;
			while ((current = tokenizer.nextSymbol()) != null) {
				if (counter >= symbols.length) {
					output.append(String.format("F: Expected: << Nothing >>; Actual: %s", current)).append(SystemHelpers.NewLine());
					success = false;
				}
				else if (getIgnoreRemaining().equals(symbols[counter])) {
					output.append(String.format("P: Ignored %s", current)).append(SystemHelpers.NewLine());
				}
				else {
					if (!current.equals(symbols[counter])) {
						output.append(String.format("F: Expected: %s; Actual: %s", symbols[counter], current)).append(SystemHelpers.NewLine());
						success = false;
					}
					else {
						output.append(String.format("P: Expected %s", current)).append(SystemHelpers.NewLine());
					}
					counter++;
				}
			}
			if (counter < symbols.length && !getIgnoreRemaining().equals(symbols[counter])) {
				success = false;
				for (; counter < symbols.length; counter++) {
					output.append(String.format("F: Expected: %s; Actual: << NONE >>", symbols[counter])).append(SystemHelpers.NewLine());
				}
			}
		}
		assertTrue("\r\n" + output.toString(), success);
	}
}
