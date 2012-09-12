package nextmethod.web.razor.parser.syntaxtree;

import javax.annotation.Nullable;

/**
 *
 */
public final class BlockExtensions {

	private BlockExtensions () {}

	public static void linkNodes(@Nullable final Block self) {
		if (self == null) return;

		Span first = null;
		Span previous = null;
		for (Span span : self.flatten()) {
			if (first == null)
				first = span;

			span.setPrevious(previous);

			if (previous != null)
				previous.setNext(span);

			previous = span;
		}
	}
}
