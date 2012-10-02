package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.generator.HelperCodeGenerator;
import nextmethod.web.razor.generator.SectionCodeGenerator;
import nextmethod.web.razor.generator.StatementCodeGenerator;
import nextmethod.web.razor.parser.SyntaxConstants;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.DirectiveBlock;
import nextmethod.web.razor.parser.syntaxtree.FunctionsBlock;
import nextmethod.web.razor.parser.syntaxtree.HelperBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.SectionBlock;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;
import org.junit.Test;

/**
 *
 */
public class JavaDirectivesTest extends JavaHtmlCodeParserTestBase {

	@Test
	public void inheritsDirective() {
		testInheritsDirective("System.Web.WebPages.WebPage");
	}

	@Test
	public void inheritsDirectiveSupportsArrays() {
		testInheritsDirective("string[[]][]");
	}

	@Test
	public void inheritsDirectiveSupportsNestedGenerics() {
		testInheritsDirective("System.Web.Mvc.WebViewPage<IEnumerable<MvcApplication2.Models.RegisterModel>>");
	}

	@Test
	public void inheritsDirectiveSupportsTypeKeywords() {
		testInheritsDirective("string");
	}

	@Test
	public void inheritsDirectiveSupportsVSTemplateTokens() { // NOTE: Is this really needed (VS)?
		testInheritsDirective("$rootnamespace$.MyBase");
	}

	private void testInheritsDirective(final String type) {
		parseBlockTest(
			"@inherits " + type,
			new DirectiveBlock(
				factory().codeTransition().build(),
				factory().metaCode(SyntaxConstants.Java.InheritsKeyword + " ")
					.accepts(AcceptedCharacters.None).build(),
				factory().code(type)
					.asBaseType(type).build()
			)
		);
	}


	@Test
	public void sessionStateDirectiveWorks() {
		final String keyword = "InProc";
		parseBlockTest(
			"@sessionstate " + keyword,
			new DirectiveBlock(
				factory().codeTransition().build(),
				factory().metaCode(SyntaxConstants.Java.SessionStateKeyword + " ")
					.accepts(AcceptedCharacters.None).build(),
				factory().code(keyword)
					.asRazorDirectiveAnnotation("sessionstate", keyword).build()
			)
		);
	}

	@Test
	public void sessionStateDirectiveParsesInvalidSessionValue() {
		final String keyword = "Blah";
		parseBlockTest(
			"@sessionstate " + keyword,
			new DirectiveBlock(
				factory().codeTransition().build(),
				factory().metaCode(SyntaxConstants.Java.SessionStateKeyword + " ")
					.accepts(AcceptedCharacters.None).build(),
				factory().code(keyword)
					.asRazorDirectiveAnnotation("sessionstate", keyword).build()
			)
		);
	}


	@Test
	public void functionsDirective() {
		parseBlockTest(
			"@functions { foo(); bar(); }",
			new FunctionsBlock(
				factory().codeTransition().build(),
				factory().metaCode(SyntaxConstants.Java.FunctionsKeyword + " {")
					.accepts(AcceptedCharacters.None).build(),
				factory().code(" foo(); bar(); ")
					.asFunctionsBody().build(),
				factory().metaCode("}")
					.accepts(AcceptedCharacters.None).build()
			)
		);
	}

	@Test
	public void emptyFunctionsDirective() {
		parseBlockTest(
			"@functions { }",
			new FunctionsBlock(
				factory().codeTransition().build(),
				factory().metaCode(SyntaxConstants.Java.FunctionsKeyword + " {")
					.accepts(AcceptedCharacters.None).build(),
				factory().code(" ")
					.asFunctionsBody().build(),
				factory().metaCode("}")
					.accepts(AcceptedCharacters.None).build()
			)
		);
	}


	@Test
	public void sectionDirective() {
		parseBlockTest(
			"@section Header { <p>F{o}o</p> }",
			new SectionBlock(
				new SectionCodeGenerator("Header"),
				factory().codeTransition().build(),
				factory().metaCode("section Header {")
					.autoCompleteWith(null, true)
					.accepts(AcceptedCharacters.Any).build(),
				new MarkupBlock(
					factory().markup(" <p>F", "{", "o", "}", "o", "</p> ").build()
				),
				factory().metaCode("}")
					.accepts(AcceptedCharacters.None).build()
			)
		);
	}


	@Test
	public void helperDirective() {
		parseBlockTest(
			"@helper Strong(string value) { foo(); }",
			new HelperBlock(
				new HelperCodeGenerator(new LocationTagged<>("Strong(string value) {", new SourceLocation(8, 0, 8)), true),
				factory().codeTransition().build(),
				factory().metaCode("helper ")
					.accepts(AcceptedCharacters.None).build(),
				factory().code("Strong(string value) {")
					.hidden()
					.accepts(AcceptedCharacters.None).build(),
				new StatementBlock(
					factory().code(" foo(); ")
						.asStatement()
						.with(new StatementCodeGenerator()).build()
				),
				factory().code("}")
					.hidden()
					.accepts(AcceptedCharacters.None).build()
			)
		);
	}

}
