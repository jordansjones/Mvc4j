package nextmethod.web.razor.parser;

import nextmethod.web.razor.framework.ParserTestBase;
import nextmethod.web.razor.framework.SpanFactory;
import nextmethod.web.razor.parser.internal.WhiteSpaceRewriter;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import org.junit.Test;

/**
 *
 */
public class WhitespaceRewriterTest {

	@SuppressWarnings("ConstantConditions")
	@Test(expected = NullPointerException.class)
	public void constructorRequiresNonNullSymbolConverter() {
		new WhiteSpaceRewriter(null);
	}

	@Test
	public void rewriteMovesWhitespacePreceedingExpressionBlockToParentBlock() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final Block start = new MarkupBlock(
			factory.markup("test"),
			new ExpressionBlock(
				factory.code("    ").asExpression(),
				factory.codeTransition(SyntaxConstants.TransitionString),
				factory.code("foo").asExpression()
			),
			factory.markup("test")
		);

		final WhiteSpaceRewriter rewriter = new WhiteSpaceRewriter(new HtmlMarkupParser().createBuildSpanDelegate());

		final Block actual = rewriter.rewrite(start);

		factory.reset();

		ParserTestBase.evaluateParseTree(
			actual,
			new MarkupBlock(
				factory.markup("test"),
				factory.markup("    "),
				new ExpressionBlock(
					factory.codeTransition(SyntaxConstants.TransitionString),
					factory.code("foo").asExpression()
				),
				factory.markup("test")
			)
		);
	}
}
