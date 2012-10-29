package nextmethod.web.razor.framework;

import nextmethod.web.razor.parser.ParserBase;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;

import javax.annotation.Nonnull;

/**
 *
 */
public abstract class MarkupParserTestBase extends CodeParserTestBase {

	@Override
	protected ParserBase selectActiveParser(@Nonnull final ParserBase codeParser, @Nonnull final ParserBase markupParser) {
		return markupParser;
	}

	protected void singleSpanDocumentTest(@Nonnull final String document, @Nonnull final BlockType blockType, @Nonnull final SpanKind spanType) {
		final Block b = createSimpleBlockAndSpan(document, blockType, spanType);
		parseDocumentTest(document, b);
	}

}
