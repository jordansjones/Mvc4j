package nextmethod.web.razor;

import nextmethod.base.Delegates;
import nextmethod.codedom.CodeCompileUnit;
import nextmethod.codedom.CodeMemberMethod;
import nextmethod.codedom.CodePackage;
import nextmethod.codedom.CodeTypeDeclaration;
import nextmethod.web.razor.generator.GeneratedClassContext;
import nextmethod.web.razor.generator.JavaRazorCodeGenerator;
import nextmethod.web.razor.generator.RazorCodeGenerator;
import nextmethod.web.razor.parser.HtmlMarkupParser;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.ParserBase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class RazorEngineHostTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void constructorRequiresNonNullCodeLanguage() {
        assertThrowsNullPointerException(() -> new RazorEngineHost(null));
        assertThrowsNullPointerException(() -> new RazorEngineHost(null, HtmlMarkupParser::new));
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullMarkupParser() {
        new RazorEngineHost(new JavaRazorCodeLanguage(), null);
    }

    @Test
    public void constructorWithCodeLanguageSetsPropertiesAppropriately() {
        // Arrange
        RazorCodeLanguage language = new JavaRazorCodeLanguage();

        // Act
        RazorEngineHost host = new RazorEngineHost(language);

        // Assert
        verifyCommonDefaults(host);
        assertSame(language, host.getCodeLanguage());
        assertThat(host.createMarkupParser(), instanceOf(HtmlMarkupParser.class));
    }

    @Test
    public void constructorWithCodeLanguageAndMarkupParserSetsPropertiesAppropriately() {
        // Arrange
        RazorCodeLanguage language = new JavaRazorCodeLanguage();
        ParserBase expected = new HtmlMarkupParser();

        // Act
        RazorEngineHost host = new RazorEngineHost(language, () -> expected);

        // Assert
        verifyCommonDefaults(host);
        assertSame(language, host.getCodeLanguage());
        assertSame(expected, host.createMarkupParser());
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void decorateCodeParserRequiresNonNullCodeParser() {
        createHost().decorateCodeParser(null);
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void decorateMarkupParserRequiresNonNullMarkupParser() {
        createHost().decorateMarkupParser(null);
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void decorateCodeGeneratorRequiresNonNullCodeGenerator() {
        createHost().decorateCodeGenerator(null);
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void postProcessGeneratedCodeRequiresNonNullCompileUnit() {
        createHost().postProcessGeneratedCode(null, new CodePackage(), new CodeTypeDeclaration(), new CodeMemberMethod());
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void postProcessGeneratedCodeRequiresNonNullGeneratedNamespace() {
        createHost().postProcessGeneratedCode(new CodeCompileUnit(), null, new CodeTypeDeclaration(), new CodeMemberMethod());
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void postProcessGeneratedCodeRequiresNonNullGeneratedClass() {
        createHost().postProcessGeneratedCode(new CodeCompileUnit(), new CodePackage(), null, new CodeMemberMethod());
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void postProcessGeneratedCodeRequiresNonNullExecuteMethod() {
        createHost().postProcessGeneratedCode(new CodeCompileUnit(), new CodePackage(), new CodeTypeDeclaration(), null);
    }

    @Test
    public void decorateCodeParserDoesNotModifyIncomingParser() {
        // Arrange
        ParserBase expected = new JavaCodeParser();

        // Act
        ParserBase actual = createHost().decorateCodeParser(expected);

        // Assert
        assertSame(expected, actual);
    }

    @Test
    public void decorateMarkupParserReturnsIncomingParser() {
        // Arrange
        ParserBase expected = new HtmlMarkupParser();

        // Act
        ParserBase actual = createHost().decorateMarkupParser(expected);

        // Assert
        assertSame(expected, actual);
    }

    @Test
    public void decorateCodeGeneratorReturnsIncomingCodeGenerator() {
        // Arrange
        RazorCodeGenerator expected = new JavaRazorCodeGenerator("Foo", "Bar", "Baz", createHost());

        // Act
        RazorCodeGenerator actual = createHost().decorateCodeGenerator(expected);

        // Assert
        assertSame(expected, actual);
    }

    @Test
    public void postProcessGeneratedCodeDoesNotModifyCode() {
        // Arrange
        CodeCompileUnit compileUnit = new CodeCompileUnit();
        CodePackage pkg = new CodePackage();
        CodeTypeDeclaration typeDecl = new CodeTypeDeclaration();
        CodeMemberMethod execMethod = new CodeMemberMethod();

        // Act
        createHost().postProcessGeneratedCode(compileUnit, pkg, typeDecl, execMethod);

        // Assert
        assertTrue(compileUnit.getPackages().isEmpty());
        assertTrue(pkg.getImports().isEmpty());
        assertTrue(pkg.getTypes().isEmpty());
        assertTrue(typeDecl.getMembers().isEmpty());
        assertTrue(execMethod.getStatements().isEmpty());
    }

    private static RazorEngineHost createHost() {
        return new RazorEngineHost(new JavaRazorCodeLanguage());
    }

    private static void verifyCommonDefaults(RazorEngineHost host) {
        assertEquals(GeneratedClassContext.Default, host.getGeneratedClassContext());
        assertTrue(host.getPackageImports().isEmpty());
        assertFalse(host.isDesignTimeMode());
        assertEquals(RazorEngineHost.InternalDefaultClassName, host.getDefaultClassName());
        assertEquals(RazorEngineHost.InternalDefaultPackage, host.getDefaultPackage());
    }

    private static void assertThrowsNullPointerException(Delegates.IAction action) {
        try
        {
            action.invoke();
            fail("Failed to throw Null Pointer Exception");
        }
        catch (NullPointerException npe)
        {
            assertNotNull(npe);
        }
    }

}
