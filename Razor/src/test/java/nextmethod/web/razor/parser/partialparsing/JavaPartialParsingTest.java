package nextmethod.web.razor.parser.partialparsing;

import nextmethod.web.razor.JavaRazorCodeLanguage;

// TODO
public class JavaPartialParsingTest extends PartialParsingTestBase<JavaRazorCodeLanguage> {

	@Override
	protected JavaRazorCodeLanguage createNewLanguage() {
		return new JavaRazorCodeLanguage();
	}

//	Commented out to prevent builds from failing.
//	@Test
//	public void implicitExpressionProvisionallyAcceptsDeleteOfIdentifierPartsIfDotRemains() {
//		final SpanFactory factory = SpanFactory.createJavaHtml();
//		final StringTextBuffer changed = new StringTextBuffer("foo @User. baz");
//		final StringTextBuffer old = new StringTextBuffer("foo @User.Name baz");
//		runPartialParseTest(
//			new TextChange(10, 4, old, 0, changed),
//			new MarkupBlock(
//				factory.markup("foo "),
//				new ExpressionBlock(
//					factory.codeTransition(),
//					factory.code("User.")
//						.asImplicitExpression(JavaCodeParser.DefaultKeywords)
//						.accepts(AcceptedCharacters.NonWhiteSpace)
//				),
//				factory.markup(" baz")
//			),
//			EnumSet.of(PartialParseResult.Provisional)
//		);
//	}

}
