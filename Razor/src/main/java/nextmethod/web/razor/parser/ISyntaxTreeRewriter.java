package nextmethod.web.razor.parser;

import nextmethod.web.razor.parser.syntaxtree.Block;

import javax.annotation.Nonnull;

interface ISyntaxTreeRewriter {

	Block rewrite(@Nonnull final Block input);

}
