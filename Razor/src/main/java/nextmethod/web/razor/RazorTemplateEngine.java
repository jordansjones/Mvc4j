package nextmethod.web.razor;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import nextmethod.annotations.Internal;
import nextmethod.base.Debug;
import nextmethod.threading.CancellationToken;
import nextmethod.web.razor.generator.GeneratedCodeMapping;
import nextmethod.web.razor.generator.RazorCodeGenerator;
import nextmethod.web.razor.parser.ParserBase;
import nextmethod.web.razor.parser.RazorParser;
import nextmethod.web.razor.text.ITextBuffer;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SeekableTextReader;
import nextmethod.web.razor.text.TextExtensions;
import nextmethod.web.razor.text.TextReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class RazorTemplateEngine {

	public static final String DefaultClassName = "Template";
	public static final String DefaultPackage = "";

	private final RazorEngineHost host;

	public RazorTemplateEngine(@Nonnull final RazorEngineHost host) {
		this.host = checkNotNull(host);
	}

	public RazorEngineHost getHost() {
		return host;
	}

	public ParserResults parseTemplate(@Nonnull final ITextBuffer input) {
		return parseTemplate(input, Optional.<CancellationToken>absent());
	}

	public ParserResults parseTemplate(@Nonnull final ITextBuffer input, @Nonnull Optional<CancellationToken> cancelToken) {
		return parseTemplateCore(TextExtensions.toDocument(input), cancelToken);
	}

	public ParserResults parseTemplate(@Nonnull final TextReader input) {
		return parseTemplate(input, Optional.<CancellationToken>absent());
	}

	public ParserResults parseTemplate(@Nonnull final TextReader input, @Nonnull Optional<CancellationToken> cancelToken) {
		return parseTemplateCore(new SeekableTextReader(input), cancelToken);
	}

	@Internal
	protected ParserResults parseTemplateCore(@Nonnull final ITextDocument input, @Nonnull final Optional<CancellationToken> cancelToken) {
		final RazorParser parser = createParser();
		if (Debug.isAssertEnabled()) {
			assert parser != null;
		}
		return parser.parse(input);
	}

	public GeneratorResults generateCode(@Nonnull final ITextBuffer input) {
		return generateCode(input, null, null, null, null);
	}

	public GeneratorResults generateCode(@Nonnull final ITextBuffer input, @Nonnull final Optional<CancellationToken> cancelToken) {
		return generateCode(input, null, null, null, cancelToken);
	}

	public GeneratorResults generateCode(@Nonnull final ITextBuffer input, @Nullable final String className, @Nullable final String rootNamespace, @Nullable final String sourceFileName) {
		return generateCode(input, className, rootNamespace, sourceFileName, null);
	}

	public GeneratorResults generateCode(@Nonnull final ITextBuffer input, @Nullable final String className, @Nullable final String rootNamespace, @Nullable final String sourceFileName, @Nonnull final Optional<CancellationToken> cancelToken) {
		return generateCodeCore(TextExtensions.toDocument(input), className, rootNamespace, sourceFileName, cancelToken);
	}

	public GeneratorResults generateCode(@Nonnull final TextReader input) {
		return generateCode(input, null, null, null, null);
	}

	public GeneratorResults generateCode(@Nonnull final TextReader input, @Nullable final String className, @Nullable final String rootNamespace, @Nullable final String sourceFileName) {
		return generateCode(input, className, rootNamespace, sourceFileName, null);
	}

	public GeneratorResults generateCode(@Nonnull final TextReader input, @Nullable final String className, @Nullable final String rootNamespace, @Nullable final String sourceFileName, @Nonnull final Optional<CancellationToken> cancelToken) {
		return generateCodeCore(new SeekableTextReader(input), className, rootNamespace, sourceFileName, cancelToken);
	}

	@Internal
	protected GeneratorResults generateCodeCore(@Nonnull ITextDocument input, @Nullable String className, @Nullable String rootNamespace, @Nullable final String sourceFileName, @Nonnull final Optional<CancellationToken> cancelToken) {
		className = tryStringOrFinally(className, host.getDefaultClassName(), DefaultClassName);
		rootNamespace = tryStringOrFinally(rootNamespace, host.getDefaultPackage(), DefaultPackage);

		// Run the parser
		final RazorParser parser = createParser();
		if (Debug.isAssertEnabled()) {
			assert parser != null;
		}
		final ParserResults results = parser.parse(checkNotNull(input));

		// Generate Code
		final RazorCodeGenerator generator = createCodeGenerator(className, rootNamespace, sourceFileName);
		generator.setDesignTimeMode(host.isDesignTimeMode());
		generator.visit(results);

		// Post process code
		host.postProcessGeneratedCode(generator.getContext());

		// Extract design-time mappings
		Map<Integer, GeneratedCodeMapping> designTimeLineMappings = Maps.newHashMap();
		if (host.isDesignTimeMode()) {
			designTimeLineMappings.putAll(generator.getContext().getCodeMappings());
		}

		// Collect results and return
		return new GeneratorResults(results, generator.getContext().getCompileUnit(), designTimeLineMappings);
	}

	@Internal
	protected RazorCodeGenerator createCodeGenerator(@Nonnull final String className, @Nonnull final String rootNamespace, @Nullable final String sourceFileName) {
		return host.decorateCodeGenerator(
			host.getCodeLanguage().createCodeGenerator(className, rootNamespace, sourceFileName, host)
		);
	}

	@Internal
	protected RazorParser createParser() {
		final ParserBase codeParser = host.getCodeLanguage().createCodeParser();
		final ParserBase markupParser = host.createMarkupParser();

		final RazorParser razorParser = new RazorParser(
			host.decorateCodeParser(codeParser),
			host.decorateMarkupParser(markupParser)
		);
		razorParser.setDesignTimeMode(host.isDesignTimeMode());

		return razorParser;
	}

	private static String tryStringOrFinally(final String tryString, final String orString, final String finallyString) {
		final String ret;
		if (!Strings.isNullOrEmpty(tryString)) {
			ret = tryString;
		}
		else if (!Strings.isNullOrEmpty(orString)) {
			ret = orString;
		}
		else {
			ret = finallyString;
		}
		return ret;
	}
}
