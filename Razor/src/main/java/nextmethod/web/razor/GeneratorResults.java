package nextmethod.web.razor;

import nextmethod.codedom.CodeCompileUnit;
import nextmethod.web.razor.generator.GeneratedCodeMapping;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.RazorError;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class GeneratorResults extends ParserResults {

	private final CodeCompileUnit generatedCode;
	private final Map<Integer, GeneratedCodeMapping> designTimeLineMappings;

	public GeneratorResults(@Nonnull final ParserResults parserResults, @Nonnull final CodeCompileUnit generatedCode, @Nonnull final Map<Integer, GeneratedCodeMapping> designTimeLineMappings) {
		this(
			parserResults.getDocument(),
			parserResults.getParserErrors(),
			generatedCode,
			designTimeLineMappings
		);
	}

	public GeneratorResults (
		@Nonnull final Block document,
	    @Nonnull final List<RazorError> parserErrors,
	    @Nonnull final CodeCompileUnit generatedCode,
	    @Nonnull final Map<Integer, GeneratedCodeMapping> designTimeLineMappings
	) {
		this(parserErrors.size() == 0, document, parserErrors, generatedCode, designTimeLineMappings);
	}

	public GeneratorResults (
		final boolean success,
		@Nonnull final Block document,
		@Nonnull final List<RazorError> parserErrors,
		@Nonnull final CodeCompileUnit generatedCode,
		@Nonnull final Map<Integer, GeneratedCodeMapping> designTimeLineMappings
	) {
		super(success, document, parserErrors);
		this.generatedCode = checkNotNull(generatedCode);
		this.designTimeLineMappings = checkNotNull(designTimeLineMappings);
	}

	/**
	 * The Generated code
	 * @return generated code
	 */
	public CodeCompileUnit getGeneratedCode() {
		return generatedCode;
	}

	/**
	 * If design-time mode was used in the Code Generator, this will contain the map
	 * of design-time generated code mappings
	 * @return generated code mappings if design-time mode was enabled
	 */
	public Map<Integer, GeneratedCodeMapping> getDesignTimeLineMappings() {
		return designTimeLineMappings;
	}

	/**
	 * If design-time mode was used in the Code Generator, this will contain the entries
	 * of design-time generated code mappings
	 * @return generated code mappings if design-time mode was enabled
	 */
	public Set<Map.Entry<Integer, GeneratedCodeMapping>> getDesignTimeLineMappingEntries() {
		return designTimeLineMappings.entrySet();
	}
}
