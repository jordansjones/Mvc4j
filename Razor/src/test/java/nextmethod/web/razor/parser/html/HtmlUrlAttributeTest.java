package nextmethod.web.razor.parser.html;

import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.editor.EditorHints;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.generator.AttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.DynamicAttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.LiteralAttributeCodeGenerator;
import nextmethod.web.razor.generator.ResolveUrlCodeGenerator;
import nextmethod.web.razor.generator.SectionCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.SectionBlock;
import nextmethod.web.razor.text.LocationTagged;
import org.junit.Test;

import static nextmethod.web.razor.framework.LocationTaggedExtensions.locationTagged;


public class HtmlUrlAttributeTest extends JavaHtmlMarkupParserTestBase {

	@Test
	public void simpleUrlInAttributeInMarkupBlock() {
		parseBlockTest(
			"<a href='~/Foo/Bar/Baz' />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("href", locationTagged(" href='", 2, 0, 2), locationTagged("'", 22, 0, 22)),
					factory().markup(" href='").with(SpanCodeGenerator.Null),
					factory().markup("~/Foo/Bar/Baz")
						.withEditorHints(EditorHints.VirtualPath)
						.with(LiteralAttributeCodeGenerator.fromValueGenerator(
							locationTagged(9, 0),
							locationTagged(new ResolveUrlCodeGenerator(), 9, 0, 9)
						)),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void simpleUrlInAttributeInMarkupDocument() {
		parseDocumentTest(
			"<a href='~/Foo/Bar/Baz' />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("href", locationTagged(" href='", 2, 0, 2), locationTagged("'", 22, 0, 22)),
					factory().markup(" href='").with(SpanCodeGenerator.Null),
					factory().markup("~/Foo/Bar/Baz")
						.withEditorHints(EditorHints.VirtualPath)
						.with(LiteralAttributeCodeGenerator.fromValueGenerator(
							locationTagged(9, 0),
							locationTagged(new ResolveUrlCodeGenerator(), 9, 0, 9)
						)),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />")
			)
		);
	}

	@Test
	public void simpleUrlInAttributeInMarkupSection() {
		parseDocumentTest(
			"@section Foo { <a href='~/Foo/Bar/Baz' /> }",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("Foo"),
					factory().codeTransition(),
					factory().metaCode("section Foo {")
						.autoCompleteWith(null, true)
						.accepts(AcceptedCharacters.Any),
					new MarkupBlock(
						factory().markup(" <a"),
						new MarkupBlock(
							new AttributeBlockCodeGenerator("href", locationTagged(" href='", 17, 0, 17), locationTagged("'", 37, 0, 37)),
							factory().markup(" href='").with(SpanCodeGenerator.Null),
							factory().markup("~/Foo/Bar/Baz")
								.withEditorHints(EditorHints.VirtualPath)
								.with(LiteralAttributeCodeGenerator.fromValueGenerator(
									locationTagged(24, 0),
									locationTagged(new ResolveUrlCodeGenerator(), 24, 0, 24)
								)),
							factory().markup("'").with(SpanCodeGenerator.Null)
						),
						factory().markup(" /> ")
					),
					factory().metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
			)
		);
	}

	@Test
	public void urlWithExpressionsInAttributeInMarkupBlock() {
		parseBlockTest(
			"<a href='~/Foo/@id/Baz' />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("href", locationTagged(" href='", 2, 0, 2), locationTagged("'", 22, 0, 22)),
					factory().markup(" href='").with(SpanCodeGenerator.Null),
					factory().markup("~/Foo/")
						.withEditorHints(EditorHints.VirtualPath)
						.with(LiteralAttributeCodeGenerator.fromValueGenerator(
							locationTagged(9, 0),
							locationTagged(new ResolveUrlCodeGenerator(), 9, 0, 9)
						)),
					new MarkupBlock(
						new DynamicAttributeBlockCodeGenerator(locationTagged(15, 0), 15, 0, 15),
						new ExpressionBlock(
							factory().codeTransition().accepts(AcceptedCharacters.None),
							factory().code("id")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords)
								.accepts(AcceptedCharacters.NonWhiteSpace)
						)
					),
					factory().markup("/Baz")
						.with(LiteralAttributeCodeGenerator.fromValue(
							locationTagged(18, 0),
							locationTagged("/Baz", 18, 0, 18)
						)),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void urlWithExpressionsInAttributeInMarkupDocument() {
		parseDocumentTest(
			"<a href='~/Foo/@id/Baz' />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("href", locationTagged(" href='", 2, 0, 2), locationTagged("'", 22, 0, 22)),
					factory().markup(" href='").with(SpanCodeGenerator.Null),
					factory().markup("~/Foo/")
						.withEditorHints(EditorHints.VirtualPath)
						.with(LiteralAttributeCodeGenerator.fromValueGenerator(
							locationTagged(9, 0),
							locationTagged(new ResolveUrlCodeGenerator(), 9, 0, 9)
						)),
					new MarkupBlock(
						new DynamicAttributeBlockCodeGenerator(locationTagged(15, 0), 15, 0, 15),
						new ExpressionBlock(
							factory().codeTransition().accepts(AcceptedCharacters.None),
							factory().code("id")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords)
								.accepts(AcceptedCharacters.NonWhiteSpace)
						)
					),
					factory().markup("/Baz")
						.with(LiteralAttributeCodeGenerator.fromValue(
							locationTagged(18, 0),
							locationTagged("/Baz", 18, 0, 18)
						)),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />")
			)
		);
	}

	@Test
	public void urlWithExpressionsInAttributeInMarkupSection() {
		parseDocumentTest(
			"@section Foo { <a href='~/Foo/@id/Baz' /> }",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("Foo"),
					factory().codeTransition(),
					factory().metaCode("section Foo {")
						.autoCompleteWith(null, true),
					new MarkupBlock(
						factory().markup(" <a"),
						new MarkupBlock(
							new AttributeBlockCodeGenerator("href", locationTagged(" href='", 17, 0, 17), locationTagged("'", 37, 0, 37)),
							factory().markup(" href='").with(SpanCodeGenerator.Null),
							factory().markup("~/Foo/")
								.withEditorHints(EditorHints.VirtualPath)
								.with(LiteralAttributeCodeGenerator.fromValueGenerator(
									locationTagged(24, 0),
									locationTagged(new ResolveUrlCodeGenerator(), 24, 0, 24)
								)),
							new MarkupBlock(
								new DynamicAttributeBlockCodeGenerator(locationTagged(30, 0), 30, 0, 30),
								new ExpressionBlock(
									factory().codeTransition().accepts(AcceptedCharacters.None),
									factory().code("id")
										.asImplicitExpression(JavaCodeParser.DefaultKeywords)
										.accepts(AcceptedCharacters.NonWhiteSpace)
								)
							),
							factory().markup("/Baz")
								.with(LiteralAttributeCodeGenerator.fromValue(
									locationTagged(33, 0),
									locationTagged("/Baz", 33, 0, 33)
								)),
							factory().markup("'").with(SpanCodeGenerator.Null)
						),
						factory().markup(" /> ")
					),
					factory().metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
			)
		);
	}

	@Test
	public void urlWithComplexCharactersInAttributeInMarkupBlock() {
		parseBlockTest(
			"<a href='~/Foo+Bar:Baz(Biz),Boz' />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("href", locationTagged(" href='", 2, 0, 2), locationTagged("'", 31, 0, 31)),
					factory().markup(" href='").with(SpanCodeGenerator.Null),
					factory().markup("~/Foo+Bar:Baz(Biz),Boz")
						.withEditorHints(EditorHints.VirtualPath)
						.with(LiteralAttributeCodeGenerator.fromValueGenerator(
							locationTagged(9, 0),
							locationTagged(new ResolveUrlCodeGenerator(), 9, 0, 9)
						)),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void urlWithComplexCharactersInAttributeInMarkupDocument() {
		parseDocumentTest(
			"<a href='~/Foo+Bar:Baz(Biz),Boz' />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("href", locationTagged(" href='", 2, 0, 2), locationTagged("'", 31, 0, 31)),
					factory().markup(" href='").with(SpanCodeGenerator.Null),
					factory().markup("~/Foo+Bar:Baz(Biz),Boz")
						.withEditorHints(EditorHints.VirtualPath)
						.with(LiteralAttributeCodeGenerator.fromValueGenerator(
							locationTagged(9, 0),
							locationTagged(new ResolveUrlCodeGenerator(), 9, 0, 9)
						)),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />")
			)
		);
	}

	@Test
	public void urlInUnquotedAttributeValueInMarkupBlock() {
		parseBlockTest(
			"<a href=~/Foo+Bar:Baz(Biz),Boz/@id/Boz />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("href", locationTagged(" href=", 2, 0, 2), locationTagged(38, 0)),
					factory().markup(" href=").with(SpanCodeGenerator.Null),
					factory().markup("~/Foo+Bar:Baz(Biz),Boz/")
						.withEditorHints(EditorHints.VirtualPath)
						.with(LiteralAttributeCodeGenerator.fromValueGenerator(
							locationTagged(8, 0),
							locationTagged(new ResolveUrlCodeGenerator(), 8, 0, 8)
						)),
					new MarkupBlock(
						new DynamicAttributeBlockCodeGenerator(locationTagged(31, 0), 31, 0, 31),
						new ExpressionBlock(
							factory().codeTransition().accepts(AcceptedCharacters.None),
							factory().code("id")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords)
								.accepts(AcceptedCharacters.NonWhiteSpace)
						)
					),
					factory().markup("/Boz")
						.with(LiteralAttributeCodeGenerator.fromValue(
							locationTagged(34, 0),
							locationTagged("/Boz", 34, 0, 34)
						))
				),
				factory().markup(" />").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void urlInUnquotedAttributeValueInMarkupDocument() {
		parseDocumentTest(
			"<a href=~/Foo+Bar:Baz(Biz),Boz/@id/Boz />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator("href", locationTagged(" href=", 2, 0, 2), locationTagged(38, 0)),
					factory().markup(" href=").with(SpanCodeGenerator.Null),
					factory().markup("~/Foo+Bar:Baz(Biz),Boz/")
						.withEditorHints(EditorHints.VirtualPath)
						.with(LiteralAttributeCodeGenerator.fromValueGenerator(
							locationTagged(8, 0),
							locationTagged(new ResolveUrlCodeGenerator(), 8, 0, 8)
						)),
					new MarkupBlock(
						new DynamicAttributeBlockCodeGenerator(locationTagged(31, 0), 31, 0, 31),
						new ExpressionBlock(
							factory().codeTransition().accepts(AcceptedCharacters.None),
							factory().code("id")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords)
								.accepts(AcceptedCharacters.NonWhiteSpace)
						)
					),
					factory().markup("/Boz")
						.with(LiteralAttributeCodeGenerator.fromValue(
							locationTagged(34, 0),
							locationTagged("/Boz", 34, 0, 34)
						))
				),
				factory().markup(" />")
			)
		);
	}

	@Test
	public void urlInUnquotedAttributeValueInMarkupSection() {
		parseDocumentTest(
			"@section Foo { <a href=~/Foo+Bar:Baz(Biz),Boz/@id/Boz /> }",
			new MarkupBlock(
				factory().emptyHtml(),
				new SectionBlock(
					new SectionCodeGenerator("Foo"),
					factory().codeTransition(),
					factory().metaCode("section Foo {")
						.autoCompleteWith(null, true),
					new MarkupBlock(
						factory().markup(" <a"),
						new MarkupBlock(
							new AttributeBlockCodeGenerator("href", locationTagged(" href=", 17, 0, 17), locationTagged(53, 0)),
							factory().markup(" href=").with(SpanCodeGenerator.Null),
							factory().markup("~/Foo+Bar:Baz(Biz),Boz/")
								.withEditorHints(EditorHints.VirtualPath)
								.with(LiteralAttributeCodeGenerator.fromValueGenerator(
									locationTagged(23, 0),
									locationTagged(new ResolveUrlCodeGenerator(), 23, 0, 23)
								)),
							new MarkupBlock(
								new DynamicAttributeBlockCodeGenerator(locationTagged(46, 0), 46, 0, 46),
								new ExpressionBlock(
									factory().codeTransition().accepts(AcceptedCharacters.None),
									factory().code("id")
										.asImplicitExpression(JavaCodeParser.DefaultKeywords)
										.accepts(AcceptedCharacters.NonWhiteSpace)
								)
							),
							factory().markup("/Boz")
								.with(LiteralAttributeCodeGenerator.fromValue(
									locationTagged(49, 0),
									locationTagged("/Boz", 49, 0, 49)
								))
						),
						factory().markup(" /> ")
					),
					factory().metaCode("}").accepts(AcceptedCharacters.None)
				),
				factory().emptyHtml()
			)
		);
	}

}
