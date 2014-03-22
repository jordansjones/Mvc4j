/*
 * Copyright 2014 Jordan S. Jones <jordansjones@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextmethod.web.razor.generator;

import nextmethod.base.Strings;
import nextmethod.web.razor.JavaRazorCodeLanguage;
import org.junit.Test;

import static nextmethod.web.razor.utils.MiscUtils.createTestFilePath;

public class JavaRazorCodeGeneratorTest extends RazorCodeGeneratorTest<JavaRazorCodeLanguage> {

    private static final String TestPhysicalPath = createTestFilePath("Bar.rzhtml");
    private static final String TestVirtualPath = "~/Foo/Bar.rzhtml";


    @SuppressWarnings("ConstantConditions")
    @Test(expected = IllegalArgumentException.class)
    public void constructorRequiresNonNullClassName() {
        new JavaRazorCodeGenerator(null, TestRootNamespaceName, TestPhysicalPath, createHost());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRequiresNonEmptyClassName() {
        new JavaRazorCodeGenerator(Strings.Empty, TestRootNamespaceName, TestPhysicalPath, createHost());
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullRootNamespaceName() {
        new JavaRazorCodeGenerator("Foo", null, TestPhysicalPath, createHost());
    }

    @Test
    public void constructorAllowsEmptyRootNamespaceName() {
        new JavaRazorCodeGenerator("Foo", Strings.Empty, TestPhysicalPath, createHost());
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullHost() {
        new JavaRazorCodeGenerator("Foo", TestRootNamespaceName, TestPhysicalPath, null);
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesRuntimeCode() {
        final String[] testTypes = {
                                       "NestedCodeBlocks",
                                       "CodeBlock",
                                       "ExplicitExpression",
                                       "MarkupInCodeBlock",
                                       "Blocks",
                                       "ImplicitExpression",
                                       "Imports",
                                       "ExpressionsInCode",
                                       "FunctionsBlock",
                                       "Templates",
                                       "Sections",
                                       "RazorComments",
                                       "Helpers",
                                       "HelpersMissingCloseParen",
                                       "HelpersMissingOpenBrace",
                                       "HelpersMissingOpenParen",
                                       "NestedHelpers",
                                       "InlineBlocks",
                                       "LayoutDirective",
                                       "ConditionalAttributes",
                                       "ResolveUrl"
        };
        for (String testType : testTypes) {
            testJavaCodeGeneratorCorrectlyGeneratesRuntimeCode(testType);
        }
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
        return "rzjava";
    }

    @Override
    protected JavaRazorCodeLanguage newLanguageInstance() {
        return new JavaRazorCodeLanguage();
    }
}
