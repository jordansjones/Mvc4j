package nextmethod.web.razor.parser.java;

import nextmethod.base.SystemHelpers;
import nextmethod.web.razor.editor.AutoCompleteEditHandler;
import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.generator.HelperCodeGenerator;
import nextmethod.web.razor.generator.MarkupCodeGenerator;
import nextmethod.web.razor.generator.SectionCodeGenerator;
import nextmethod.web.razor.generator.StatementCodeGenerator;
import nextmethod.web.razor.parser.JavaLanguageCharacteristics;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.FunctionsBlock;
import nextmethod.web.razor.parser.syntaxtree.HelperBlock;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.SectionBlock;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import org.junit.Test;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class JavaAutoCompleteTest extends JavaHtmlCodeParserTestBase {

	@Test
	public void functionsDirectiveAutoCompleteAtEOF() {
		parseBlockTest(
			"@functions{",
			new FunctionsBlock(
				getFactory().codeTransition("@").accepts(AcceptedCharacters.None).build(),
				getFactory().metaCode("functions{").accepts(AcceptedCharacters.None).build(),
				getFactory().emptyJava()
					.asFunctionsBody()
					.with(new AutoCompleteEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()) {{
						this.setAutoCompleteString("}");
					}}).build()
			),
			new RazorError(
				String.format(
					RazorResources().getString("parseError.expected.endOfBlock.before.eof"),
					"functions",
					"}",
					"{"
				),
				1, 0, 1
			)
		);
	}

	@Test
	public void helperDirectiveAutoCompleteAtEOF() {
		parseBlockTest(
			"@helper Strong(string value) {",
			new HelperBlock(
				new HelperCodeGenerator(new LocationTagged<>("Strong(string value) {", 8, 0, 8), true),
				getFactory().codeTransition().build(),
				getFactory().metaCode("helper ").accepts(AcceptedCharacters.None).build(),
				getFactory().code("Strong(string value) {")
					.hidden().accepts(AcceptedCharacters.None).build(),
				new StatementBlock(
					getFactory().emptyJava().asStatement().with(
						new AutoCompleteEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()) {{
							this.setAutoCompleteString("}");
						}}
					).build()
				)
			),
			new RazorError(
				String.format(
					RazorResources().getString("parseError.expected.endOfBlock.before.eof"),
					"helper",
					"}",
					"{"
				),
				1, 0, 1
			)
		);
	}

	@Test
	public void sectionDirectiveAutoCompleteAtEOF() {
		parseBlockTest(
			"@section Header {",
			new SectionBlock(
				new SectionCodeGenerator("Header"),
				getFactory().codeTransition().build(),
				getFactory().metaCode("section Header {")
					.autoCompleteWith("}", true)
					.accepts(AcceptedCharacters.Any).build(),
				new MarkupBlock()
			),
			new RazorError(
				String.format(
					RazorResources().getString("parseError.expected.x"),
					"}"
				),
				17, 0, 17
			)
		);
	}

	@Test
	public void verbatimBlockAutoCompleteAtEOF() {
		parseBlockTest(
			"@{",
			new StatementBlock(
				getFactory().codeTransition().build(),
				getFactory().metaCode("{").accepts(AcceptedCharacters.None).build(),
				getFactory().emptyJava().asStatement()
					.with(new AutoCompleteEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()) {{
						this.setAutoCompleteString("}");
					}}).build()
			),
			new RazorError(
				String.format(
					RazorResources().getString("parseError.expected.endOfBlock.before.eof"),
					RazorResources().getString("blockName.code"),
					"}",
					"{"
				),
				1, 0, 1
			)
		);
	}

	@Test
	public void functionsDirectiveAutoCompleteAtStartOfFile() {
		parseBlockTest(
			"@functions{" + SystemHelpers.newLine() + "foo",
			new FunctionsBlock(
				getFactory().codeTransition("@").accepts(AcceptedCharacters.None).build(),
				getFactory().metaCode("functions{").accepts(AcceptedCharacters.None).build(),
				getFactory().code(SystemHelpers.newLine() + "foo").asFunctionsBody()
					.with(new AutoCompleteEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()) {{
						this.setAutoCompleteString("}");
					}}).build()
			),
			new RazorError(
				String.format(RazorResources().getString("parseError.expected.endOfBlock.before.eof"), "functions", "}", "{"),
				1, 0, 1
			)
		);
	}

	@Test
	public void helperDirectiveAutoCompleteAtStartOfFile() {
		parseBlockTest(
			"@helper Strong(string value) {" + newLine() + "<p></p>",
			new HelperBlock(
				new HelperCodeGenerator(new LocationTagged<>("Strong(string value) {", 8, 0, 8), true),
				getFactory().codeTransition().build(),
				getFactory().metaCode("helper ").accepts(AcceptedCharacters.None).build(),
				getFactory().code("Strong(string value) {").hidden().accepts(AcceptedCharacters.None).build(),
				new StatementBlock(
					getFactory().code(newLine()).asStatement()
						.with(new AutoCompleteEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()) {{
							this.setAutoCompleteString("}");
						}}).build(),
					new MarkupBlock(
						getFactory().markup("<p></p>")
							.with(new MarkupCodeGenerator())
							.accepts(AcceptedCharacters.None).build()
					),
					getFactory().span(
						SpanKind.Code,
						new JavaSymbol(
							getFactory().getLocationTracker().getCurrentLocation(),
							"",
							JavaSymbolType.Unknown
						)
					).with(new StatementCodeGenerator()).build()
				)
			),
			new RazorError(
				String.format(RazorResources().getString("parseError.expected.endOfBlock.before.eof"), "helper", "}", "{"),
				1, 0, 1
			)
		);
	}

	@Test
	public void sectionDirectiveAutoCompleteAtStartOfFile() {
		parseBlockTest(
			"@section Header {" + newLine() + "<p>Foo</p>",
			new SectionBlock(
				new SectionCodeGenerator("Header"),
				getFactory().codeTransition().build(),
				getFactory().metaCode("section Header {")
					.autoCompleteWith("}", true)
					.accepts(AcceptedCharacters.Any).build(),
				new MarkupBlock(getFactory().markup(newLine() + "<p>Foo</p>").build())
			),
			new RazorError(
				String.format(RazorResources().getString("parseError.expected.x"), "}"),
				29, 1, 10
			)
		);
	}

	@Test
	public void verbatimBlockAutoCompleteAtStartOfFile() {
		parseBlockTest(
			"@{" + newLine() + "<p></p>",
			new StatementBlock(
				getFactory().codeTransition().build(),
				getFactory().metaCode("{").accepts(AcceptedCharacters.None).build(),
				getFactory().code(newLine()).asStatement()
					.with(new AutoCompleteEditHandler(JavaLanguageCharacteristics.Instance.createTokenizeStringDelegate()) {{ this.setAutoCompleteString("}"); }})
					.build(),
				new MarkupBlock(
					getFactory().markup("<p></p>")
						.with(new MarkupCodeGenerator())
						.accepts(AcceptedCharacters.None)
						.build()
				),
				getFactory().span(
					SpanKind.Code,
					new JavaSymbol(
						getFactory().getLocationTracker().getCurrentLocation(),
						"",
						JavaSymbolType.Unknown
					)
				)
					.with(new StatementCodeGenerator())
					.build()
			),
			new RazorError(
				String.format(
					RazorResources().getString("parseError.expected.endOfBlock.before.eof"),
					RazorResources().getString("blockName.code"),
					"}",
					"{"
				),
				1, 0, 1
			)
		);
	}

}
