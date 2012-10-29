package nextmethod.web.razor.parser.internal;

import nextmethod.annotations.Internal;
import nextmethod.web.razor.parser.syntaxtree.Block;

import javax.annotation.Nonnull;

@Internal
public interface ISyntaxTreeRewriter {

	Block rewrite(@Nonnull final Block input);

}
