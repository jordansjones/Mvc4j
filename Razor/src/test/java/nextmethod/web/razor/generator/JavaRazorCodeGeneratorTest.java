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

import com.google.common.collect.Lists;
import nextmethod.base.Strings;
import nextmethod.web.razor.JavaRazorCodeLanguage;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static nextmethod.web.razor.utils.MiscUtils.createTestFilePath;

@RunWith(Theories.class)
public class JavaRazorCodeGeneratorTest extends RazorCodeGeneratorTest<JavaRazorCodeLanguage> {

    private static final String TestPhysicalPath = createTestFilePath("Bar.rzhtml");

    private RazorCodeGeneratorTestBuilder buildTest(final String name) {
        return new RazorCodeGeneratorTestBuilder()
            .setRunner(this::runTest)
            .setName(name);
    }

    @Override
    protected String getFileExtension() { return JavaRazorCodeLanguage.RazorFileExtension; }

    @Override
    protected String getLanguageName() { return JavaRazorCodeLanguage.LanguageName; }

    @Override
    protected String getBaselineExtension() { return "rzjava"; }

    @Override
    protected JavaRazorCodeLanguage newLanguageInstance() { return new JavaRazorCodeLanguage(); }


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

    @Theory(nullsAccepted = false)
    public void javaCodeGeneratorCorrectlyGeneratesRuntimeCode(@JavaTypeTest String testType) {
        buildTest(testType)
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesMappingsForSimpleUnspacedIf() {
        buildTest("SimpleUnspacedIf")
            .setBaselineName("SimpleUnspacedIf.DesignTime.Tabs")
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(1, 2, 1, 15),
                    new GeneratedCodeMapping(3, 13, 7, 3)
                )
            )
            .setTabTest(TabTest.Tabs)
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesMappingsForRazorCommentsAtDesignTime() {
        buildTest("RazorComments")
            .setBaselineName("RazorComments.DesignTime")
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(4, 3, 3, 6),
                    new GeneratedCodeMapping(5, 40, 39, 22),
                    new GeneratedCodeMapping(6, 50, 49, 58),
                    new GeneratedCodeMapping(12, 3, 3, 24),
                    new GeneratedCodeMapping(13, 46, 46, 3),
                    new GeneratedCodeMapping(15, 3, 7, 1),
                    new GeneratedCodeMapping(15, 8, 8, 1)
                )
            )
            .setTabTest(TabTest.NoTabs)
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGenerateMappingForOpenedCurlyIf() {
        openedIf(true);
    }

    @Test
    public void javaCodeGeneratorCorrectlyGenerateMappingForOpenedCurlyIfSpaces() {
        openedIf(false);
    }

    private void openedIf(boolean withTabs) {
        final int tabOffsetForMapping = withTabs
                                        ? 3
                                        : 0;

        buildTest("OpenedIf")
            .setBaselineName(
                "OpenedIf.DesignTime" + (withTabs
                                         ? ".Tabs"
                                         : Strings.Empty)
            )
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(3, 2, 1, 14),
                    new GeneratedCodeMapping(4, 8, 8 - tabOffsetForMapping, 2),
                    new GeneratedCodeMapping(5, 8, 8 - tabOffsetForMapping, 0)
                )
            )
            .setTestSpans(
                new TestSpan[]{
                    new TestSpan(SpanKind.Markup, 0, 16),
                    new TestSpan(SpanKind.Transition, 16, 17),
                    new TestSpan(SpanKind.Code, 17, 31),
                    new TestSpan(SpanKind.Markup, 31, 38),
                    new TestSpan(SpanKind.Code, 38, 40),
                    new TestSpan(SpanKind.Markup, 40, 47),
                    new TestSpan(SpanKind.Code, 47, 47),
                }
            )
            .setTabTest(
                withTabs
                ? TabTest.Tabs
                : TabTest.NoTabs
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesImportStatementsAtDesignTime() {
        buildTest("Imports")
            .setBaselineName("Imports.DesignTime")
            .setDesignTimeMode(true)
            .setTabTest(TabTest.NoTabs)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(1, 2, 1, 15),
                    new GeneratedCodeMapping(2, 2, 1, 32),
                    new GeneratedCodeMapping(3, 2, 1, 15),
                    new GeneratedCodeMapping(5, 30, 30, 21),
                    new GeneratedCodeMapping(6, 36, 36, 20)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesFunctionsBlocksAtDesignTime() {
        buildTest("FunctionsBlock")
            .setBaselineName("FunctionsBlock.DesignTime")
            .setDesignTimeMode(true)
            .setTabTest(TabTest.NoTabs)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(1, 13, 13, 4),
                    new GeneratedCodeMapping(5, 13, 13, 104),
                    new GeneratedCodeMapping(12, 26, 26, 11)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesFunctionsBlocksAtDesignTimeTabs() {
        buildTest("FunctionsBlock")
            .setBaselineName("FunctionsBlock.DesignTime.Tabs")
            .setDesignTimeMode(true)
            .setTabTest(TabTest.Tabs)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(1, 13, 4, 4),
                    new GeneratedCodeMapping(5, 13, 4, 104),
                    new GeneratedCodeMapping(12, 26, 14, 11)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesMinimalFunctionsBlocksAtDesignTimeTabs() {
        buildTest("FunctionsBlockMinimal")
            .setBaselineName("FunctionsBlockMinimal.DesignTime.Tabs")
            .setDesignTimeMode(true)
            .setTabTest(TabTest.Tabs)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(3, 13, 7, 55)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesHiddenSpansWithinCode() {
        buildTest("HiddenSpansInCode")
            .setDesignTimeMode(true)
            .setTabTest(TabTest.NoTabs)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(1, 3, 3, 6),
                    new GeneratedCodeMapping(2, 6, 6, 5)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorGeneratesCodeWithParserErrorsInDesignTimeMode() {
        buildTest("ParserError")
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(1, 3, 3, 31)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesInheritsAtRuntime() {
        buildTest("Inherits").setBaselineName("Inherits.Runtime").run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesInheritsAtDesigntime() {
        buildTest("Inherits")
            .setBaselineName("Inherits.Designtime")
            .setDesignTimeMode(true)
            .setTabTest(TabTest.NoTabs)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(1, 2, 7, 5),
                    new GeneratedCodeMapping(3, 11, 11, 25)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesDesignTimePragmasForUnfinishedExpressionsInCode() {
        buildTest("UnfinishedExpressionInCode")
            .setTabTest(TabTest.NoTabs)
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(1, 3, 3, 2),
                    new GeneratedCodeMapping(2, 2, 7, 9),
                    new GeneratedCodeMapping(2, 11, 11, 2)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesDesignTimePragmasForUnfinishedExpressionsInCodeTabs() {
        buildTest("UnfinishedExpressionInCode")
            .setBaselineName("UnfinishedExpressionInCode.Tabs")
            .setTabTest(TabTest.Tabs)
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(1, 3, 3, 2),
                    new GeneratedCodeMapping(2, 2, 7, 9),
                    new GeneratedCodeMapping(2, 11, 5, 2)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesDesignTimePragmasMarkupAndExpressions() {
        buildTest("DesignTime")
            .setDesignTimeMode(true)
            .setTabTest(TabTest.NoTabs)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(2, 14, 13, 36),
                    new GeneratedCodeMapping(3, 23, 23, 1),
                    new GeneratedCodeMapping(3, 28, 28, 15),
                    new GeneratedCodeMapping(8, 3, 7, 12),
                    new GeneratedCodeMapping(9, 2, 7, 4),
                    new GeneratedCodeMapping(9, 15, 15, 3),
                    new GeneratedCodeMapping(9, 26, 26, 1),
                    new GeneratedCodeMapping(14, 6, 7, 3),
                    new GeneratedCodeMapping(17, 9, 24, 7),
                    new GeneratedCodeMapping(17, 16, 16, 26),
                    new GeneratedCodeMapping(19, 19, 19, 9),
                    new GeneratedCodeMapping(21, 1, 1, 1)
                )
            )
            .run();
    }


    @Test
    public void javaCodeGeneratorCorrectlyGeneratesDesignTimePragmasForImplicitExpressionStartedAtEOF() {
        buildTest("ImplicitExpressionAtEOF")
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(3, 2, 7, 0)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesDesignTimePragmasForExplicitExpressionStartedAtEOF() {
        buildTest("ExplicitExpressionAtEOF")
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(3, 3, 7, 0)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesDesignTimePragmasForCodeBlockStartedAtEOF() {
        buildTest("CodeBlockAtEOF")
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(1, 3, 3, 0)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesDesignTimePragmasForEmptyImplicitExpression() {
        buildTest("EmptyImplicitExpression")
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(3, 2, 7, 0)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesDesignTimePragmasForEmptyImplicitExpressionInCode() {
        buildTest("EmptyImplicitExpressionInCode")
            .setDesignTimeMode(true)
            .setTabTest(TabTest.NoTabs)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(1, 3, 3, 6),
                    new GeneratedCodeMapping(2, 6, 7, 0),
                    new GeneratedCodeMapping(2, 6, 6, 2)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesDesignTimePragmasForEmptyImplicitExpressionInCodeTabs() {
        buildTest("EmptyImplicitExpressionInCode")
            .setBaselineName("EmptyImplicitExpressionInCode.Tabs")
            .setTabTest(TabTest.Tabs)
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(1, 3, 3, 6),
                    new GeneratedCodeMapping(2, 6, 7, 0),
                    new GeneratedCodeMapping(2, 6, 3, 2)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesDesignTimePragmasForEmptyExplicitExpression() {
        buildTest("EmptyExplicitExpression")
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(3, 3, 7, 0)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyGeneratesDesignTimePragmasForEmptyCodeBlock() {
        buildTest("EmptyCodeBlock")
            .setDesignTimeMode(true)
            .setExpectedDesignTimePragmas(
                Lists.newArrayList(
                    new GeneratedCodeMapping(3, 3, 3, 0)
                )
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorDoesNotRenderLinePragmasIfGenerateLinePragmasIsSetToFalse() {
        buildTest("NoLinePragmas")
            .setGeneratePragmas(false)
            .run();
    }

    @Test
    public void javaCodeGeneratorRendersHelpersBlockCorrectlyWhenInstanceHelperRequested() {
        buildTest("Helpers")
            .setBaselineName("Helpers.Instance")
            .setHostConfig(h -> h.setStaticHelpers(false))
            .run();
    }

    @Test
    public void javaCodeGeneratorCorrectlyInstrumentsRazorCodeWhenInstrumentationRequested() {
        buildTest("Instrumented")
            .setHostConfig(
                h -> {
                    h.enableInstrumentation(true);
                    h.setInstrumentedSourceFilePath(String.format("~/%s.rzhtml", h.getDefaultClassName()));
                }
            )
            .run();
    }

    @Test
    public void javaCodeGeneratorGeneratesUrlsCorrectlyWithCommentsAndQuotes() {
        buildTest("HtmlCommentWithQuote_Single")
            .setTabTest(TabTest.NoTabs)
            .run();

        buildTest("HtmlCommentWithQuote_Double")
            .setTabTest(TabTest.NoTabs)
            .run();
    }
}
