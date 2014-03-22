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

package nextmethod.web.razor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

import nextmethod.codedom.CodeCompileUnit;
import nextmethod.web.razor.generator.GeneratedCodeMapping;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.RazorError;

import static com.google.common.base.Preconditions.checkNotNull;

public class GeneratorResults extends ParserResults {

    private final CodeCompileUnit generatedCode;
    private final Map<Integer, GeneratedCodeMapping> designTimeLineMappings;

    public GeneratorResults(@Nonnull final ParserResults parserResults, @Nonnull final CodeCompileUnit generatedCode,
                            @Nonnull final Map<Integer, GeneratedCodeMapping> designTimeLineMappings
                           ) {
        this(
                parserResults.getDocument(),
                parserResults.getParserErrors(),
                generatedCode,
                designTimeLineMappings
            );
    }

    public GeneratorResults(
                               @Nonnull final Block document,
                               @Nonnull final List<RazorError> parserErrors,
                               @Nonnull final CodeCompileUnit generatedCode,
                               @Nonnull final Map<Integer, GeneratedCodeMapping> designTimeLineMappings
                           ) {
        this(parserErrors.size() == 0, document, parserErrors, generatedCode, designTimeLineMappings);
    }

    public GeneratorResults(
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
     *
     * @return generated code
     */
    public CodeCompileUnit getGeneratedCode() {
        return generatedCode;
    }

    /**
     * If design-time mode was used in the Code Generator, this will contain the map
     * of design-time generated code mappings
     *
     * @return generated code mappings if design-time mode was enabled
     */
    public Map<Integer, GeneratedCodeMapping> getDesignTimeLineMappings() {
        return designTimeLineMappings;
    }

    /**
     * If design-time mode was used in the Code Generator, this will contain the entries
     * of design-time generated code mappings
     *
     * @return generated code mappings if design-time mode was enabled
     */
    public Set<Map.Entry<Integer, GeneratedCodeMapping>> getDesignTimeLineMappingEntries() {
        return designTimeLineMappings.entrySet();
    }
}
