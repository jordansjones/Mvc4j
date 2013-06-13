package nextmethod.web.razor.parser.html;

import nextmethod.base.Strings;
import nextmethod.web.razor.ParserResults;
import nextmethod.web.razor.editor.EditorHints;
import nextmethod.web.razor.framework.JavaHtmlMarkupParserTestBase;
import nextmethod.web.razor.generator.AttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.DynamicAttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.LiteralAttributeCodeGenerator;
import nextmethod.web.razor.generator.ResolveUrlCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.HtmlMarkupParser;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.internal.ConditionalAttributeCollapser;
import nextmethod.web.razor.parser.internal.MarkupCollapser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.text.LocationTagged;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class HtmlAttributeTest extends JavaHtmlMarkupParserTestBase {

	@Test
	public void simpleLiteralAttribute() {
		parseBlockTest(
			"<a href='Foo' />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator(
						"href",
						new LocationTagged<>(" href='", 2, 0, 2),
						new LocationTagged<>("'", 12, 0, 12)
					),
					factory().markup(" href='").with(SpanCodeGenerator.Null),
					factory().markup("Foo").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(Strings.Empty, 9, 0, 9), new LocationTagged<>("Foo", 9, 0, 9))),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void multiPartLiteralAttribute() {
		parseBlockTest(
			"<a href='Foo Bar Baz' />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator(
						"href",
						new LocationTagged<>(" href='", 2, 0, 2),
						new LocationTagged<>("'", 20, 0, 20)
					),
					factory().markup(" href='").with(SpanCodeGenerator.Null),
					factory().markup("Foo").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(Strings.Empty, 9, 0, 9), new LocationTagged<>("Foo", 9, 0, 9))),
					factory().markup(" Bar").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(" ", 12, 0, 12), new LocationTagged<>("Bar", 13, 0, 13))),
					factory().markup(" Baz").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(" ", 16, 0, 16), new LocationTagged<>("Baz", 17, 0, 17))),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void doubleQuotedLiteralAttribute() {
		parseBlockTest(
			"<a href=\"Foo Bar Baz\" />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator(
						"href",
						new LocationTagged<>(" href=\"", 2, 0, 2),
						new LocationTagged<>("\"", 20, 0, 20)
					),
					factory().markup(" href=\"").with(SpanCodeGenerator.Null),
					factory().markup("Foo").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(Strings.Empty, 9, 0, 9), new LocationTagged<>("Foo", 9, 0, 9))),
					factory().markup(" Bar").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(" ", 12, 0, 12), new LocationTagged<>("Bar", 13, 0, 13))),
					factory().markup(" Baz").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(" ", 16, 0, 16), new LocationTagged<>("Baz", 17, 0, 17))),
					factory().markup("\"").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void unquotedLiteralAttribute() {
		parseBlockTest(
			"<a href=Foo Bar Baz />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator(
						"href",
						new LocationTagged<>(" href=", 2, 0, 2),
						new LocationTagged<>(Strings.Empty, 11, 0, 11)
					),
					factory().markup(" href=").with(SpanCodeGenerator.Null),
					factory().markup("Foo").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(Strings.Empty, 8, 0, 8), new LocationTagged<>("Foo", 8, 0, 8)))
				),
				factory().markup(" Bar Baz />").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void simpleExpressionAttribute() {
		parseBlockTest(
			"<a href='@foo' />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator(
						"href",
						new LocationTagged<>(" href='", 2, 0, 2),
						new LocationTagged<>("'", 13, 0, 13)
					),
					factory().markup(" href='").with(SpanCodeGenerator.Null),
					new MarkupBlock(
						new DynamicAttributeBlockCodeGenerator(new LocationTagged<>(Strings.Empty, 9, 0, 9), 9, 0, 9),
						new ExpressionBlock(
							factory().codeTransition(),
							factory().code("foo")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords)
								.accepts(AcceptedCharacters.NonWhiteSpace)
						)
					),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void multiValueExpressionAttribute() {
		parseBlockTest(
			"<a href='@foo bar @baz' />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator(
						"href",
						new LocationTagged<>(" href='", 2, 0, 2),
						new LocationTagged<>("'", 22, 0, 22)
					),
					factory().markup(" href='").with(SpanCodeGenerator.Null),
					new MarkupBlock(
						new DynamicAttributeBlockCodeGenerator(new LocationTagged<>(Strings.Empty, 9, 0, 9), 9, 0, 9),
						new ExpressionBlock(
							factory().codeTransition(),
							factory().code("foo")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords)
								.accepts(AcceptedCharacters.NonWhiteSpace)
						)
					),
					factory().markup(" bar").with(LiteralAttributeCodeGenerator.fromValue(new LocationTagged<>(" ", 13, 0, 13), new LocationTagged<>("bar", 14, 0, 14))),
					new MarkupBlock(
						new DynamicAttributeBlockCodeGenerator(new LocationTagged<>(" ", 17, 0, 17), 18, 0, 18),
						factory().markup(" ").with(SpanCodeGenerator.Null),
						new ExpressionBlock(
							factory().codeTransition(),
							factory().code("baz")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords)
								.accepts(AcceptedCharacters.NonWhiteSpace)
						)
					),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void virtualPathAttributesWorkWithConditionalAttributes() {
		parseBlockTest(
			"<a href='@foo ~/Foo/Bar' />",
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator(
						"href",
						new LocationTagged<>(" href='", 2, 0, 2),
						new LocationTagged<>("'", 23, 0, 23)
					),
					factory().markup(" href='").with(SpanCodeGenerator.Null),
					new MarkupBlock(
						new DynamicAttributeBlockCodeGenerator(new LocationTagged<>(Strings.Empty, 9, 0, 9), 9, 0, 9),
						new ExpressionBlock(
							factory().codeTransition(),
							factory().code("foo")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords)
								.accepts(AcceptedCharacters.NonWhiteSpace)
						)
					),
					factory().markup(" ~/Foo/Bar")
						.withEditorHints(EditorHints.VirtualPath)
						.with(LiteralAttributeCodeGenerator.fromValueGenerator(new LocationTagged<>(" ", 13, 0, 13), new LocationTagged<SpanCodeGenerator>(new ResolveUrlCodeGenerator(), 14, 0, 14))),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void unquotedAttributeWithCodeWithSpacesInBlock() {
		parseBlockTest(
			"<input value=@foo />",
			new MarkupBlock(
				factory().markup("<input"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator(
						"value",
						new LocationTagged<>(" value=", 6, 0, 6),
						new LocationTagged<>(Strings.Empty, 17, 0, 17)
					),
					factory().markup(" value=").with(SpanCodeGenerator.Null),
					new MarkupBlock(
						new DynamicAttributeBlockCodeGenerator(new LocationTagged<>(Strings.Empty, 13, 0, 13), 13, 0, 13),
						new ExpressionBlock(
							factory().codeTransition(),
							factory().code("foo")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords)
								.accepts(AcceptedCharacters.NonWhiteSpace)
						)
					)
				),
				factory().markup(" />").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void unquotedAttributeWithCodeWithSpacesInDocument() {
		parseDocumentTest(
			"<input value=@foo />",
			new MarkupBlock(
				factory().markup("<input"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator(
						"value",
						new LocationTagged<>(" value=", 6, 0, 6),
						new LocationTagged<>(Strings.Empty, 17, 0, 17)
					),
					factory().markup(" value=").with(SpanCodeGenerator.Null),
					new MarkupBlock(
						new DynamicAttributeBlockCodeGenerator(new LocationTagged<>(Strings.Empty, 13, 0, 13), 13, 0, 13),
						new ExpressionBlock(
							factory().codeTransition(),
							factory().code("foo")
								.asImplicitExpression(JavaCodeParser.DefaultKeywords)
								.accepts(AcceptedCharacters.NonWhiteSpace)
						)
					)
				),
				factory().markup(" />")
			)
		);
	}

	@Test
	public void conditionalAttributeCollapserDoesNotRemoveUrlAttributeValues() {
		final ParserResults results = parseDocument("<a href='~/Foo/Bar' />");
		Block rewritten = new ConditionalAttributeCollapser(new HtmlMarkupParser().createBuildSpanDelegate()).rewrite(results.getDocument());
		rewritten = new MarkupCollapser(new HtmlMarkupParser().createBuildSpanDelegate()).rewrite(rewritten);

		assertEquals(0, results.getParserErrors().size());
		evaluateParseTree(
			rewritten,
			new MarkupBlock(
				factory().markup("<a"),
				new MarkupBlock(
					new AttributeBlockCodeGenerator(
						"href",
						new LocationTagged<>(" href='", 2, 0, 2),
						new LocationTagged<>("'", 18, 0, 18)
					),
					factory().markup(" href='").with(SpanCodeGenerator.Null),
					factory().markup("~/Foo/Bar")
						.withEditorHints(EditorHints.VirtualPath)
						.with(LiteralAttributeCodeGenerator.fromValueGenerator(new LocationTagged<>(Strings.Empty, 9, 0, 9), new LocationTagged<SpanCodeGenerator>(new ResolveUrlCodeGenerator(), 9, 0, 9))),
					factory().markup("'").with(SpanCodeGenerator.Null)
				),
				factory().markup(" />")
			)
		);
	}

	@Test
	public void conditionalAttributesDoNotCreateExtraDataForEntirelyLiteralAttribute() {
		final String code = "<div class=\"sidebar\">"
			+ "    <h1>Title</h1>"
			+ "    <p>"
			+ "        As the author, you can <a href=\"/Photo/Edit/photoId\">edit</a>"
			+ "        or <a href=\"/Photo/Remove/photoId\">remove</a> this photo."
			+ "    </p>"
			+ "    <dl>"
			+ "        <dt class=\"description\">Description</dt>"
			+ "        <dd class=\"description\">"
			+ "            The uploader did not provide a description for this photo."
			+ "        </dd>"
			+ "        <dt class=\"uploaded-by\">Uploaded by</dt>"
			+ "        <dd class=\"uploaded-by\"><a href=\"/User/View/user.UserId\">user.DisplayName</a></dd>"
			+ "        <dt class=\"upload-date\">Upload date</dt>"
			+ "        <dd class=\"upload-date\">photo.UploadDate</dd>"
			+ "        <dt class=\"part-of-gallery\">Gallery</dt>"
			+ "        <dd><a href=\"/View/gallery.Id\" title=\"View gallery.Name gallery\">gallery.Name</a></dd>"
			+ "        <dt class=\"tags\">Tags</dt>"
			+ "        <dd class=\"tags\">"
			+ "            <ul class=\"tags\">"
			+ "                <li>This photo has no tags.</li>"
			+ "            </ul>"
			+ "            <a href=\"/Photo/EditTags/photoId\">edit tags</a>"
			+ "        </dd>"
			+ "    </dl>"
			+ ""
			+ "    <p>"
			+ "        <a class=\"download\" href=\"/Photo/Full/photoId\" title=\"Download: (photo.FileTitle + photo.FileExtension)\">Download full photo</a> ((photo.FileSize / 1024) KB)"
			+ "    </p>"
			+ "</div>"
			+ "<div class=\"main\">"
			+ "    <img class=\"large-photo\" alt=\"photo.FileTitle\" src=\"/Photo/Thumbnail\" />"
			+ "    <h2>Nobody has commented on this photo</h2>"
			+ "    <ol class=\"comments\">"
			+ "        <li>"
			+ "            <h3 class=\"comment-header\">"
			+ "                <a href=\"/User/View/comment.UserId\" title=\"View comment.DisplayName's profile\">comment.DisplayName</a> commented at comment.CommentDate:"
			+ "            </h3>"
			+ "            <p class=\"comment-body\">comment.CommentText</p>"
			+ "        </li>"
			+ "    </ol>"
			+ ""
			+ "    <form method=\"post\" action=\"\">"
			+ "        <fieldset id=\"addComment\">"
			+ "            <legend>Post new comment</legend>"
			+ "            <ol>"
			+ "                <li>"
			+ "                    <label for=\"newComment\">Comment</label>"
			+ "                    <textarea id=\"newComment\" name=\"newComment\" title=\"Your comment\" rows=\"6\" cols=\"70\"></textarea>"
			+ "                </li>"
			+ "            </ol>"
			+ "            <p class=\"form-actions\">"
			+ "                <input type=\"submit\" title=\"Add comment\" value=\"Add comment\" />"
			+ "            </p>"
			+ "        </fieldset>"
			+ "    </form>"
			+ "</div>";

		final ParserResults results = parseDocument(code);
		Block rewritten = new ConditionalAttributeCollapser(new HtmlMarkupParser().createBuildSpanDelegate()).rewrite(results.getDocument());
		rewritten = new MarkupCollapser(new HtmlMarkupParser().createBuildSpanDelegate()).rewrite(rewritten);

		assertEquals(0, results.getParserErrors().size());
		evaluateParseTree(
			rewritten,
			new MarkupBlock(
				factory().markup(code)
			)
		);
	}

	@Test
	public void conditionalAttributesAreDisabledForDataAttributesInBlock() {
		parseBlockTest(
			"<span data-foo='@foo'></span>",
			new MarkupBlock(
				factory().markup("<span"),
				new MarkupBlock(
					factory().markup(" data-foo='"),
					new ExpressionBlock(
						factory().codeTransition(),
						factory().code("foo")
							.asImplicitExpression(JavaCodeParser.DefaultKeywords)
							.accepts(AcceptedCharacters.NonWhiteSpace)
					),
					factory().markup("'")
				),
				factory().markup("></span>").accepts(AcceptedCharacters.None)
			)
		);
	}

	@Test
	public void conditionalAttributesAreDisabledForDataAttributesInDocument() {
		parseDocumentTest(
			"<span data-foo='@foo'></span>",
			new MarkupBlock(
				factory().markup("<span"),
				new MarkupBlock(
					factory().markup(" data-foo='"),
					new ExpressionBlock(
						factory().codeTransition(),
						factory().code("foo")
							.asImplicitExpression(JavaCodeParser.DefaultKeywords)
							.accepts(AcceptedCharacters.NonWhiteSpace)
					),
					factory().markup("'")
				),
				factory().markup("></span>")
			)
		);
	}
}
