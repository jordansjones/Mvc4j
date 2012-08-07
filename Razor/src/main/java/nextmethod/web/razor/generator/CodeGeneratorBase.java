package nextmethod.web.razor.generator;

import com.google.common.base.Strings;
import nextmethod.web.razor.parser.syntaxtree.Span;

public abstract class CodeGeneratorBase {

	// Helpers
	protected static int calculatePadding(final Span target) {
		return calculatePadding(target, 0);
	}

	protected static int calculatePadding(final Span target, final int generatedStart) {
		int padding = target.getStart().getCharacterIndex() - generatedStart;
		if (padding < 0)
			padding = 0;

		return padding;
	}

	protected static String pad(final String code, final Span target) {
		return pad(code, target, 0);
	}

	protected static String pad(final String code, final Span target, final int generatedStart) {
		return Strings.padStart(code, calculatePadding(target, generatedStart) + code.length(), ' ');
	}

}
