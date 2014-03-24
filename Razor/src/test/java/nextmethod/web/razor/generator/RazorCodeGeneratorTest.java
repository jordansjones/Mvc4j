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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Throwables;
import com.google.common.primitives.Ints;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.base.Strings;
import nextmethod.base.SystemHelpers;
import nextmethod.codedom.CodeCompileUnit;
import nextmethod.codedom.compiler.CodeDomProvider;
import nextmethod.codedom.compiler.CodeGeneratorOptions;
import nextmethod.io.Filesystem;
import nextmethod.web.razor.DebugArgs;
import nextmethod.web.razor.GeneratorResults;
import nextmethod.web.razor.RazorCodeLanguage;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.RazorTemplateEngine;
import nextmethod.web.razor.StringTextBuffer;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.utils.MiscUtils;
import nextmethod.web.razor.utils.TestFile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public abstract class RazorCodeGeneratorTest<TLang extends RazorCodeLanguage> {

    protected static final String TestRootNamespaceName = "test.output";

    static {
        SystemHelpers.NLSupplier = () -> Strings.CRLF;
    }

    protected abstract String getFileExtension();

    protected abstract String getLanguageName();

    protected abstract String getBaselineExtension();

    protected abstract TLang newLanguageInstance();

    protected RazorEngineHost createHost() {
        return new RazorEngineHost(newLanguageInstance());
    }

    protected void runTest(final String name,
                           String baselineName,
                           boolean generatePragmas,
                           boolean designTimeMode,
                           final List<GeneratedCodeMapping> expectedDesignTimePragmas,
                           final TestSpan[] spans,
                           TabTest tabTest,
                           Delegates.IAction1<RazorEngineHost> hostConfig) {
        boolean testRun = false;

        if ((tabTest.value() & TabTest.Tabs.value()) == TabTest.Tabs.value()) {
            runtTestInternal(
                name,
                baselineName,
                generatePragmas,
                designTimeMode,
                expectedDesignTimePragmas,
                spans,
                true,
                hostConfig
            );
            testRun = true;
        }

        if ((tabTest.value() & TabTest.NoTabs.value()) == TabTest.NoTabs.value()) {
            runtTestInternal(
                name,
                baselineName,
                generatePragmas,
                designTimeMode,
                expectedDesignTimePragmas,
                spans,
                false,
                hostConfig
            );
            testRun = true;
        }

        assertThat("No test was run because TabTest is not set correctly", testRun, is(true));
    }

    private void runtTestInternal(
        final String name,
        String baselineName,
        final boolean generatePragmas,
        final boolean designTimeMode,
        final List<GeneratedCodeMapping> expectedDesignTimePragmas,
        final TestSpan[] testSpans,
        final boolean withTabs,
        final Delegates.IAction1<RazorEngineHost> hostConfig
    ) {

        if (Strings.isNullOrEmpty(baselineName)) baselineName = name;

        final String source = TestFile.create(String.format(
                                                  "codeGenerator/%s/source/%s.%s",
                                                  getLanguageName(),
                                                  name,
                                                  getFileExtension()
                                              )).readAllText();
        final String expectedOutput = TestFile.create(String.format(
                                                          "codeGenerator/%s/output/%s.%s",
                                                          getLanguageName(),
                                                          baselineName,
                                                          getBaselineExtension()
                                                      )).readAllText();

        final RazorEngineHost host = createHost();
        host.getPackageImports().add("java.lang");
        host.setDesignTimeMode(designTimeMode);
        host.setStaticHelpers(true);
        host.setDefaultClassName(name);

        final GeneratedClassContext genCtx = new GeneratedClassContext(
            GeneratedClassContext.DefaultExecuteMethodName,
            GeneratedClassContext.DefaultWriteMethodName,
            GeneratedClassContext.DefaultWriteLiteralMethodName,
            "WriteTo",
            "WriteLiteralTo",
            "Template",
            "DefineSection",
            "BeginContext",
            "EndContext"
        );
        genCtx.setLayoutPropertyName("Layout");
        genCtx.setResolveUrlMethodName("Href");
        host.setGeneratedClassContext(genCtx);

        if (hostConfig != null) {
            hostConfig.invoke(host);
        }

        host.setIndentingWithTabs(withTabs);

        final RazorTemplateEngine engine = new RazorTemplateEngine(host);

        GeneratorResults results;
        try (final StringTextBuffer buffer = new StringTextBuffer(source)) {
            results = engine.generateCode(
                buffer,
                name,
                TestRootNamespaceName,
                generatePragmas ? String.format("%s.%s", name, getFileExtension()) : null
            );
        }

        final CodeCompileUnit compileUnit = results.getGeneratedCode();
        CodeDomProvider codeDomProvider = null;
        try {
            codeDomProvider = host.getCodeLanguage().getCodeDomProviderType().newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            Throwables.propagate(e);
        }

        final CodeGeneratorOptions options = new CodeGeneratorOptions();
        options.setBlankLinesBetweenMembers(false);
        options.setIndentString(Strings.Empty);
        options.setNewlineString(Strings.CRLF);

        final String generatedOutput;
        try (final StringWriter stringWriter = new StringWriter()) {
            assert codeDomProvider != null;
            codeDomProvider.generateCodeFromCompileUnit(compileUnit, stringWriter, options);
            generatedOutput = MiscUtils.stripRuntimeVersion(stringWriter.getBuffer().toString());
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }

        writeBaseLine(
            String.format(
                Filesystem.createFilePath(
                    "test",
                    "resources",
                    "testFiles",
                    "codeGenerator",
                    "%s",
                    "output",
                    "%s.%s"
                ),
                getLanguageName(),
                baselineName,
                getBaselineExtension()
            ),
            generatedOutput
        );

        // Verify code against baseline
        if (!Debug.isDebugArgPresent(DebugArgs.GenerateBaselines)) {
            assertEquals(name, expectedOutput, generatedOutput);
        }

        final Collection<Span> generatedSpans = results.getDocument().flatten();
        for (Span span : generatedSpans) {
            verifyNoBrokenEndOfLines(span.getContent());
        }

        if (designTimeMode) {
            if (testSpans != null) {
                assertArrayEquals(testSpans, generatedSpans.stream().map(TestSpan::new).toArray(TestSpan[]::new));
            }

            if (expectedDesignTimePragmas != null) {
                final Map<Integer, GeneratedCodeMapping> designTimeLineMappings = results.getDesignTimeLineMappings();
                assertThat(
                    designTimeLineMappings != null && !designTimeLineMappings.isEmpty(),
                    is(true)
                );

                assert designTimeLineMappings != null;
                assertThat(designTimeLineMappings.size(), is(expectedDesignTimePragmas.size()));


                final GeneratedCodeMapping[] actualResults = designTimeLineMappings.entrySet().stream()
                    .sorted((x, y) -> Ints.compare(x.getKey(), y.getKey()))
                    .map(Map.Entry<Integer, GeneratedCodeMapping>::getValue)
                    .toArray(GeneratedCodeMapping[]::new);

                final GeneratedCodeMapping[] expectedResults = expectedDesignTimePragmas.stream().toArray(GeneratedCodeMapping[]::new);

                assertThat(
                    actualResults,
                    is(expectedResults)
                );
            }
        }
    }

    private void writeBaseLine(final String baselineFile, final String output) {
        if (Debug.isDebugArgPresent(DebugArgs.GenerateBaselines)) {
            // TODO
        }
    }

    private void verifyNoBrokenEndOfLines(final String text) {
        final int textLength = text.length();
        for (int i = 0; i < textLength; i++) {
            final char c = text.charAt(i);
            if (c == '\r') {
                assertThat(textLength > i + 1, is(true));
                assertThat(text.charAt(i + 1), is('\n'));
            }
            else if (c == '\n') {
                assertThat(i > 0, is(true));
                assertThat(text.charAt(i - 1), is('\r'));
            }
        }
    }

}
