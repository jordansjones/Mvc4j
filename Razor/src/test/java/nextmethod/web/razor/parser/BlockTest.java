package nextmethod.web.razor.parser;

import com.google.common.collect.Iterables;
import nextmethod.web.razor.StringTextBuffer;
import nextmethod.web.razor.framework.SpanFactory;
import nextmethod.web.razor.generator.ExpressionCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.TextChange;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 *
 */
public class BlockTest {

	@Test
	public void testConstructorWithBlockBuilderSetsParent() {
		final Block block = new BlockBuilder().setType(BlockType.Comment).build();
		final Span span = new SpanBuilder().setKind(SpanKind.Code).build();

		assertSame(block, span.getParent());
	}

	@Test
	public void testConstructorCopiesBasicValuesFromBlockBuilder() {
		final Block block = new BlockBuilder().setName("Foo").setType(BlockType.Helper).build();

		assertEquals("Foo", block.getName());
		assertEquals(BlockType.Helper, block.getType());
	}

	@Test
	public void testConstructorTransfersInstanceOfCodeGeneratorFromBlockBuilder() {
		final IBlockCodeGenerator expected = new ExpressionCodeGenerator();
		final Block block = new BlockBuilder().setType(BlockType.Helper).setCodeGenerator(expected).build();

		assertSame(expected, block.getCodeGenerator());
	}

	@Test
	public void testConstructorTransfersChildrenFromBlockBuilder() {
		final Span expected = new SpanBuilder().setKind(SpanKind.Code).build();
		final BlockBuilder blockBuilder = new BlockBuilder().setType(BlockType.Functions);
		blockBuilder.getChildren().add(expected);

		final Block block = blockBuilder.build();

		assertSame(expected, Iterables.getFirst(block.getChildren(), null));
	}

	@Test
	public void testLocateOwnerReturnsNullIfNoSpanReturnsTrueForOwnsSpan() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final Block block = new MarkupBlock(
			factory.markup("Foo ").build(),
			new StatementBlock(
				factory.codeTransition().build(),
				factory.code("bar").asStatement().build()
			),
			factory.markup(" Baz").build()
		);

		final TextChange change = new TextChange(128, 1, new StringTextBuffer("Foo @bar Baz"), 1, new StringTextBuffer("Foo @bor Baz"));

		final Span actual = block.locateOwner(change);

		assertNull(actual);
	}

	@Test
	public void testLocateOwnerReturnsNullIfChangeCrossesMultipleSpans() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final Block block = new MarkupBlock(
			factory.markup("Foo ").build(),
			new StatementBlock(
				factory.codeTransition().build(),
				factory.code("bar").asStatement().build()
			),
			factory.markup(" Baz").build()
		);

		final TextChange change = new TextChange(4, 10, new StringTextBuffer("Foo @bar Baz"), 10, new StringTextBuffer("Foo @bor Baz"));

		final Span actual = block.locateOwner(change);

		assertNull(actual);
	}

}
