package nextmethod.web.razor.parser.java;

import nextmethod.web.razor.framework.JavaHtmlCodeParserTestBase;
import nextmethod.web.razor.generator.ExpressionCodeGenerator;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.DirectiveBlock;
import nextmethod.web.razor.parser.syntaxtree.ExpressionBlock;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import org.junit.Test;

// Basic tests for Java Statements
//  * Basic case for each statement
//  * Basic case for ALL clauses

// This class DOES NOT contain
//  * Error cases
//  * Tests for various types of nested statements
//  * Comment tests
public class JavaStatementTest extends JavaHtmlCodeParserTestBase {

	@Test
	public void forStatement() {
		runBlockTestAcceptsNone("for(int i = 0; i++; i < length) { foo(); }");
	}

	@Test
	public void forEachStatement() {
		runBlockTestAcceptsNone("foreach(var foo in bar) { foo(); }");
	}

	@Test
	public void whileStatement() {
		runBlockTestAcceptsNone("while(true) { foo(); }");
	}

	@Test
	public void switchStatement() {
		runBlockTestAcceptsNone("switch(foo) { foo(); }");
	}

	@Test
	public void lockStatement() {
		runBlockTestAcceptsNone("lock(baz) { foo(); }");
	}

	@Test
	public void ifStatement() {
		runBlockTest("if(true) { foo(); }");
	}

	@Test
	public void elseIfClause() {
		runBlockTest("if(true) { foo(); } else if(false) { foo(); } else if(!false) { foo(); }");
	}

	@Test
	public void elseClause() {
		runBlockTestAcceptsNone("if(true) { foo(); } else { foo(); }");
	}

	@Test
	public void tryStatement() {
		runBlockTest("try { foo(); }");
	}

	@Test
	public void catchClause() {
		runBlockTest("try { foo(); } catch(IOException ioex) { handleIO(); } catch(Exception ex) { handleOther(); }");
	}

	@Test
	public void finallyClause() {
		runBlockTestAcceptsNone("try { foo(); } finally { Dispose(); }");
	}

	@Test
	public void usingStatement() {
		runBlockTestAcceptsNone("using(var foo = new Foo()) { foo.Bar(); }");
	}

	@Test
	public void usingTypeAlias() {
		parseBlockTest(
			"@using StringDictionary = System.Collections.Generic.Dictionary<string, string>",
			new DirectiveBlock(
				factory().codeTransition(),
				factory().code("using StringDictionary = System.Collections.Generic.Dictionary<string, string>")
					.asPackageImport(" StringDictionary = System.Collections.Generic.Dictionary<string, string>", 5)
					.accepts(AcceptedCharacters.AnyExceptNewLine)
					
			)
		);
	}

	@Test
	public void usingNamespaceImport() {
		parseBlockTest(
			"@using System.Text.Encoding.ASCIIEncoding",
			new DirectiveBlock(
				factory().codeTransition(),
				factory().code("using System.Text.Encoding.ASCIIEncoding")
					.asPackageImport(" System.Text.Encoding.ASCIIEncoding", 5)
					.accepts(AcceptedCharacters.AnyExceptNewLine)
					
			)
		);
	}

	@Test
	public void doStatement() {
		runBlockTestAcceptsNone("do { foo(); } while(true);");
	}

	@Test
	public void nonBlockKeywordTreatedAsImplicitExpression() {
		parseBlockTest(
			"@is foo",
			new ExpressionBlock(
				new ExpressionCodeGenerator(),
				factory().codeTransition(),
				factory().code("is")
					.asImplicitExpression(JavaCodeParser.DefaultKeywords)
					.accepts(AcceptedCharacters.NonWhiteSpace)
					
			)
		);
	}

	private void runBlockTest(final String code) {
		parseBlockTest(
			"@" + code,
			new StatementBlock(
				factory().codeTransition(),
				factory().code(code)
					.asStatement()
			)
		);
	}

	private void runBlockTestAcceptsNone(final String code) {
		parseBlockTest(
			"@" + code,
			new StatementBlock(
				factory().codeTransition(),
				factory().code(code)
					.asStatement()
					.accepts(AcceptedCharacters.None)
			)
		);
	}

}
