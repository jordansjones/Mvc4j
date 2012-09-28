package nextmethod.web.razor.framework;

import nextmethod.web.razor.parser.ParserBase;
import nextmethod.web.razor.parser.SyntaxConstants;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 */
public abstract class CodeParserTestBase extends ParserTestBase {

	protected abstract Set<String> getKeywordSet();

	@Override
	protected ParserBase selectActiveParser(@Nonnull final ParserBase codeParser, @Nonnull final ParserBase markupParser) {
		return codeParser;
	}

	protected void implicitExpressionTest(final String input, final RazorError... errors) {
		implicitExpressionTest(input, AcceptedCharacters.NonWhiteSpace, errors);
	}

	protected void implicitExpressionTest(final String input, final AcceptedCharacters acceptedCharacters, final RazorError... errors) {
		implicitExpressionTest(input, input, AcceptedCharacters.NonWhiteSpace, errors);
	}

	protected void implicitExpressionTest(final String input, final String expected, final RazorError... errors) {
		implicitExpressionTest(input, expected, AcceptedCharacters.NonWhiteSpace, errors);
	}

	protected void implicitExpressionTest(final String input, final String expected, final AcceptedCharacters acceptedCharacters, final RazorError... errors) {
		final SpanFactory factory = createSpanFactory();
		parseBlockTest(
			SyntaxConstants.TransitionString + input,
			new ExpressionBlock(
				getFactory().codeTransition().build(),
				getFactory().code(expected)
					.asImplicitExpression(getKeywordSet())
					.accepts(acceptedCharacters).build()
			),
			errors
		);
	}

	@Override
	protected void singleSpanBlockTest(final String document, final BlockType blockType, final SpanKind spanType, final EnumSet<AcceptedCharacters> acceptedCharacters) {
		singleSpanBlockTest(document, blockType, spanType, acceptedCharacters, new RazorError[0]);
	}

	@Override
	protected void singleSpanBlockTest(final String document, final String spanContent, final BlockType blockType, final SpanKind spanType, final EnumSet<AcceptedCharacters> acceptedCharacters) {
		singleSpanBlockTest(document, spanContent, blockType, spanType, acceptedCharacters, new RazorError[0]);
	}

	@Override
	protected void singleSpanBlockTest(final String document, final BlockType blockType, final SpanKind spanType, final RazorError... expectedError) {
		singleSpanBlockTest(document, document, blockType, spanType, expectedError);
	}

	@Override
	protected void singleSpanBlockTest(final String document, final String spanContent, final BlockType blockType, final SpanKind spanType, final RazorError... expectedError) {
		singleSpanBlockTest(document, spanContent, blockType, spanType, AcceptedCharacters.Any, expectedError);
	}

	@Override
	protected void singleSpanBlockTest(final String document, final BlockType blockType, final SpanKind spanType, final EnumSet<AcceptedCharacters> acceptedCharacters, final RazorError... expectedErrors) {
		singleSpanBlockTest(document, document, blockType, spanType, acceptedCharacters, expectedErrors);
	}

	@Override
	protected void singleSpanBlockTest(final String document, final String spanContent, final BlockType blockType, final SpanKind spanType, final EnumSet<AcceptedCharacters> acceptedCharacters, final RazorError... expectedErrors) {
		final Block b = createSimpleBlockAndSpan(spanContent, blockType, spanType, acceptedCharacters);
		parseBlockTest(document, b, expectedErrors != null ? expectedErrors : new RazorError[0]);
	}
}
