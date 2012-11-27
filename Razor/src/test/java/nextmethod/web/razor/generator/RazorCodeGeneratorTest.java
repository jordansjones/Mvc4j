package nextmethod.web.razor.generator;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.io.CharStreams;
import com.google.common.primitives.Ints;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
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
import nextmethod.web.razor.utils.TestFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public abstract class RazorCodeGeneratorTest<TLang extends RazorCodeLanguage> {

	protected static final String TestRootNamespaceName = "TestOutput";

	protected abstract String getFileExtension();
	protected abstract String getLanguageName();
	protected abstract String getBaselineExtension();
	protected abstract TLang newLanguageInstance();

	protected RazorEngineHost createHost() {
		return new RazorEngineHost(newLanguageInstance());
	}

	protected void runTest(final String name) {
		runTest(name, null);
	}

	protected void runTest(final String name, String baselineName) {
		runTest(name, baselineName, true);
	}

	protected void runTest(final String name, String baselineName, boolean generatePragmas) {
		runTest(name, baselineName, generatePragmas, false);
	}

	protected void runTest(final String name, String baselineName, boolean generatePragmas, boolean designTimeMode) {
		runTest(name, baselineName, generatePragmas, designTimeMode, null);
	}

	protected void runTest(final String name, String baselineName, boolean generatePragmas, boolean designTimeMode, final List<GeneratedCodeMapping> expectedDesignTimePragmas) {
		runTest(name, baselineName, generatePragmas, designTimeMode, expectedDesignTimePragmas, null);
	}

	protected void runTest(final String name, String baselineName, boolean generatePragmas, boolean designTimeMode, final List<GeneratedCodeMapping> expectedDesignTimePragmas, Delegates.IAction1<RazorEngineHost> hostConfig) {
		if (Strings.isNullOrEmpty(baselineName)) {
			baselineName = name;
		}

		final String source = TestFile.create(String.format("codeGenerator.%s.source.%s.%s", getLanguageName(), name, getFileExtension())).readAllText();
		final String expectedOutput = TestFile.create(String.format("codeGenerator.%s.output.%s.%s", getLanguageName(), baselineName, getBaselineExtension())).readAllText();

		// Setup the host and engine
		final RazorEngineHost host = createHost();
		host.getPackageImports().add("java.lang");
		host.setDesignTimeMode(designTimeMode);
		host.setStaticHelpers(true);
		host.setDefaultClassName(name);

		// Add support for templates, etc.
		final GeneratedClassContext generatedClassContext = new GeneratedClassContext(
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
		generatedClassContext.setLayoutPropertyName("Layout");
		generatedClassContext.setResolveUrlMethodName("Href");
		host.setGeneratedClassContext(generatedClassContext);

		if (hostConfig != null) {
			hostConfig.invoke(host);
		}

		final RazorTemplateEngine engine = new RazorTemplateEngine(host);

		// Generate code for the file
		GeneratorResults results = null;
		try (final StringTextBuffer buffer = new StringTextBuffer(source)) {
			results = engine.generateCode(buffer, name, TestRootNamespaceName, generatePragmas ? String.format("%s.%s", name, getFileExtension()) : null);
		}

		// Generate Code
		final CodeCompileUnit compileUnit = results.getGeneratedCode();
		CodeDomProvider codeDomProvider = null;
		try {
			codeDomProvider = host.getCodeLanguage().getCodeDomProviderType().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			Throwables.propagate(e);
		}

		final CodeGeneratorOptions options = new CodeGeneratorOptions();
		options.setBlankLinesBetweenMembers(false);
		options.setIndentString("");

		final StringBuilder output = new StringBuilder();
		try (Writer writer = CharStreams.asWriter(output)) {
			codeDomProvider.generateCodeFromCompileUnit(compileUnit, writer, options);
		} catch (IOException e) {
			Throwables.propagate(e);
		}

		final String generatedOutput = output.toString();

		writeBaseLine(
			String.format(
				Filesystem.createFilePath(
					"test",
					getClass().getPackage().getName(),
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
			assertEquals(expectedOutput, generatedOutput);
		}

		// Verify design-time pragmas
		if (designTimeMode) {
			assertTrue(expectedDesignTimePragmas != null || results.getDesignTimeLineMappings() == null || results.getDesignTimeLineMappings().size() == 0);
			assertTrue(expectedDesignTimePragmas == null || (results.getDesignTimeLineMappings() != null && !results.getDesignTimeLineMappings().isEmpty()));
			if (expectedDesignTimePragmas != null) {
				final Ordering<Map.Entry<Integer, GeneratedCodeMapping>> ordering = Ordering.from(createGeneratedCodeMappingComparator());
				final Iterable<GeneratedCodeMapping> generatedCodeMappings = Iterables.transform(ordering.sortedCopy(results.getDesignTimeLineMappingEntries()), createSelectGeneratedCodeMappingFunction());
				assertArrayEquals(
					Iterables.toArray(expectedDesignTimePragmas, GeneratedCodeMapping.class),
					Iterables.toArray(generatedCodeMappings, GeneratedCodeMapping.class)
				);
			}
		}
	}

	private void writeBaseLine(final String baselineFile, final String output) {
		if (Debug.isDebugArgPresent(DebugArgs.GenerateBaselines)) {
			// TODO
		}
	}

	private Comparator<Map.Entry<Integer, GeneratedCodeMapping>> createGeneratedCodeMappingComparator() {
		return new Comparator<Map.Entry<Integer, GeneratedCodeMapping>>() {
			@Override
			public int compare(final Map.Entry<Integer, GeneratedCodeMapping> o1, final Map.Entry<Integer, GeneratedCodeMapping> o2) {
				return Ints.compare(o1.getKey(), o2.getKey());
			}
		};
	}

	protected Function<Map.Entry<Integer, GeneratedCodeMapping>, GeneratedCodeMapping> createSelectGeneratedCodeMappingFunction() {
		return new Function<Map.Entry<Integer, GeneratedCodeMapping>, GeneratedCodeMapping>() {
			@Override
			public GeneratedCodeMapping apply(@Nullable final Map.Entry<Integer, GeneratedCodeMapping> input) {
				return input.getValue();
			}
		};
	}
}
