package nextmethod.web.razor.parser.syntaxtree;

import com.google.common.collect.Lists;
import nextmethod.web.razor.framework.ISpanConstructor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

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

	private static final Class<ISpanConstructor> ISpanConstructorClass = ISpanConstructor.class;

	public static Collection<SyntaxTreeNode> buildSpanConstructors(final Collection<SyntaxTreeNode> children) {
		final List<SyntaxTreeNode> built = Lists.newArrayListWithExpectedSize(children.size());
		for (SyntaxTreeNode child : children) {
			if (ISpanConstructorClass.isAssignableFrom(child.getClass())) {
				built.add(ISpanConstructorClass.cast(child).build());
			}
			else {
				built.add(child);
			}
		}
		return built;
	}
}
