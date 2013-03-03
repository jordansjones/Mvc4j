package nextmethod.web.razor.parser;

import com.google.common.collect.Lists;
import nextmethod.web.razor.ParserResults;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import org.junit.Test;

import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class ParserVisitorExtensionsTest {

	@SuppressWarnings("ConstantConditions")
	@Test(expected = NullPointerException.class)
	public void visitThrowsOnNullVisitor() {
		ParserVisitor target = null;
		final ParserResults results = new ParserResults(mockBlock(), Lists.<RazorError>newArrayList());

		target.visit(results);
	}

	@SuppressWarnings("ConstantConditions")
	@Test(expected = NullPointerException.class)
	public void visitThrowsOnNullResults() {
		final ParserVisitor target = mockParserVisitor();

		target.visit(null);
	}

	@Test
	public void visitSendsDocumentToVisitor() {
		final ParserVisitor mock = mockParserVisitor();

		final Block root = mockBlock();
		final ParserResults results = new ParserResults(root, Lists.<RazorError>newArrayList());

		mock.visit(results);

		verify(mock).visitBlock(eq(root));
	}

	@Test
	public void visitSendsErrorsToVisitor() {
		final ParserVisitor target = mockParserVisitor();
		final Block root = mockBlock();
		final List<RazorError> errors = Lists.newArrayList(
			new RazorError("Foo", 1, 0, 1),
			new RazorError("Bar", 2, 0, 2)
		);
		final ParserResults results = new ParserResults(root, errors);

		target.visit(results);

		verify(target).visitError(eq(errors.get(0)));
		verify(target).visitError(eq(errors.get(1)));
	}

	@Test
	public void visitCallsOnCompleteWhenAllNodesHaveBeenVisited() {
		final ParserVisitor target = mockParserVisitor();
		final Block root = mockBlock();
		final List<RazorError> errors = Lists.newArrayList(
			new RazorError("Foo", 1, 0, 1),
			new RazorError("Bar", 2, 0, 2)
		);
		final ParserResults results = new ParserResults(root, errors);

		target.visit(results);

		verify(target).onComplete();
	}

	private static ParserVisitor mockParserVisitor() {
		return mock(ParserVisitor.class, CALLS_REAL_METHODS);
	}

	private static Block mockBlock() {
		return new BlockBuilder().setType(BlockType.Comment).build();
	}

}
