package nextmethod.web.razor.parser.partialparsing;

import nextmethod.web.razor.JavaRazorCodeLanguage;
import nextmethod.web.razor.PartialParseResult;
import nextmethod.web.razor.StringTextBuffer;
import nextmethod.web.razor.framework.SpanFactory;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.text.TextChange;
import org.junit.Test;

import java.util.EnumSet;

// TODO
public class JavaPartialParsingTest extends PartialParsingTestBase<JavaRazorCodeLanguage> {

	@Override
	protected JavaRazorCodeLanguage createNewLanguage() {
		return new JavaRazorCodeLanguage();
	}

	@Test
	public void implicitExpressionProvisionallyAcceptsDeleteOfIdentifierPartsIfDotRemains() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final StringTextBuffer changed = new StringTextBuffer("foo @User. baz");
		final StringTextBuffer old = new StringTextBuffer("foo @User.Name baz");
		runPartialParseTest(
			new TextChange(10, 4, old, 0, changed),
			new MarkupBlock(
				factory.markup("foo "),
				new ExpressionBlock(
					factory.codeTransition(),
					factory.code("User.")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory.markup(" baz")
			),
			EnumSet.of(PartialParseResult.Provisional)
		);
	}

	@Test
	public void implicitExpressionAcceptsDeleteOfIdentifierPartsIfSomeOfIdentifierRemains() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final StringTextBuffer changed = new StringTextBuffer("foo @Us baz");
		final StringTextBuffer old = new StringTextBuffer("foo @User baz");
		runPartialParseTest(
			new TextChange(7, 2, old, 0, changed),
			new MarkupBlock(
				factory.markup("foo "),
				new ExpressionBlock(
					factory.codeTransition(),
					factory.code("Us")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory.markup(" baz")
			)
		);
	}

	@Test
	public void implicitExpressionProvisionallyAcceptsMultipleInsertionIfItCausesIdentifierExpansionAndTrailingDot() {
		final SpanFactory factory = SpanFactory.createJavaHtml();
		final StringTextBuffer changed = new StringTextBuffer("foo @User. baz");
		final StringTextBuffer old = new StringTextBuffer("foo @U baz");
		runPartialParseTest(
			new TextChange(6, 0, old, 4, changed),
			new MarkupBlock(
				factory.markup("foo "),
				new ExpressionBlock(
					factory.codeTransition(),
					factory.code("User.")
						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
						.accepts(AcceptedCharacters.NonWhiteSpace)
				),
				factory.markup(" baz")
			),
			PartialParseResult.Provisional
		);
	}

}
