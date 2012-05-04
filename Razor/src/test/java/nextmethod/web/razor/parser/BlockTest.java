package nextmethod.web.razor.parser;

import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import org.junit.Test;

/**
 *
 */
public class BlockTest {

	@Test
	public void testConstructorWithBlockBuilderSetsParent() {
		final BlockBuilder blockBuilder = new BlockBuilder();
		blockBuilder.setType(BlockType.Comment);
//		new SpanBuilder();

		final Block block = blockBuilder.build();

//		assertSame(block, span.parent);
	}

}
