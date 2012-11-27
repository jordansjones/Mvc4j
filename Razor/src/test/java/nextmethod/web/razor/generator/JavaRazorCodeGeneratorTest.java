package nextmethod.web.razor.generator;

import nextmethod.web.razor.JavaRazorCodeLanguage;
import org.junit.Test;

import static nextmethod.web.razor.utils.MiscUtils.createTestFilePath;

public class JavaRazorCodeGeneratorTest extends RazorCodeGeneratorTest<JavaRazorCodeLanguage> {

	private static final String TestPhysicalPath = createTestFilePath("Bar.rzhtml");
	private static final String TestVirtualPath = "~/Foo/Bar.rzhtml";


	@Test(expected = IllegalArgumentException.class)
	public void constructorRequiresNonNullClassName() {
		new JavaRazorCodeGenerator(null, TestRootNamespaceName, TestPhysicalPath, createHost());
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorRequiresNonEmptyClassName() {
		new JavaRazorCodeGenerator("", TestRootNamespaceName, TestPhysicalPath, createHost());
	}

	@Test(expected = NullPointerException.class)
	public void constructorRequiresNonNullRootNamespaceName() {
		new JavaRazorCodeGenerator("Foo", null, TestPhysicalPath, createHost());
	}

	@Test
	public void constructorAllowsEmptyRootNamespaceName() {
		new JavaRazorCodeGenerator("Foo", "", TestPhysicalPath, createHost());
	}

	@Test(expected = NullPointerException.class)
	public void constructorRequiresNonNullHost() {
		new JavaRazorCodeGenerator("Foo", TestRootNamespaceName, TestPhysicalPath, null);
	}

	@Test
	public void javaCodeGeneratorCorrectlyGeneratesRuntimeCode() {
//		for ()
	}

	private void testJavaCodeGeneratorCorrectlyGeneratesRuntimeCode(final String testType) {
		runTest(testType);
	}



	@Override
	protected String getFileExtension() {
		return JavaRazorCodeLanguage.RazorFileExtension;
	}

	@Override
	protected String getLanguageName() {
		return JavaRazorCodeLanguage.LanguageName;
	}

	@Override
	protected String getBaselineExtension() {
		return JavaRazorCodeLanguage.LanguageFileExtension;
	}

	@Override
	protected JavaRazorCodeLanguage newLanguageInstance() {
		return new JavaRazorCodeLanguage();
	}
}
