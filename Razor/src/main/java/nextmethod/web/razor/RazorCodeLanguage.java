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

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nextmethod.base.Strings;
import nextmethod.codedom.compiler.CodeDomProvider;
import nextmethod.collections.MapBuilder;
import nextmethod.web.razor.generator.RazorCodeGenerator;
import nextmethod.web.razor.parser.ParserBase;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents a code language in Razor. By default only Java is defined (.rzhtml)
 */
public abstract class RazorCodeLanguage<T extends CodeDomProvider> {

    private static Map<String, RazorCodeLanguage> services = MapBuilder.<String, RazorCodeLanguage>of(
                                                                                                         JavaRazorCodeLanguage.RazorFileExtension,
                                                                                                         new JavaRazorCodeLanguage()
                                                                                                     ).build();

    /**
     * Gets the list of registered languages mapped to file extensions (without a ".")
     *
     * @return registered code languages
     */
    public static Map<String, RazorCodeLanguage> getLanguages() {
        return services;
    }

    /**
     * The name of the language (for use in System.Web.Compilation.BuildProvider.GetDefaultCompilerTypeForLanguage)
     *
     * @return the name of the language
     */
    public abstract String getLanguageName();

    /**
     * The type of CodeDOM provider for this language.
     *
     * @return CodeDOM provider type
     */
    public abstract Class<T> getCodeDomProviderType();

    /**
     * Gets the RazorCodeLanguage registered for the specified file extension
     *
     * @param fileExtension the extension, with or without a "."
     *
     * @return the language registered for that extension or null
     */
    @Nullable
    public static RazorCodeLanguage getLanguageByExtension(@Nonnull String fileExtension) {
        checkArgument(!Strings.isNullOrEmpty(fileExtension));

        RazorCodeLanguage language = null;
        if (fileExtension.charAt(0) == '.') {
            fileExtension = fileExtension.substring(1);
        }
        if (getLanguages().containsKey(fileExtension)) {
            language = getLanguages().get(fileExtension);
        }
        return language;
    }

    /**
     * Constructs the code parser. Must return a new instance on EVERY call to ensure thread-safety
     *
     * @return new instance of code parser
     */
    public abstract ParserBase createCodeParser();

    /**
     * Constructs the code generator. Must return a new instance on EVERY call to ensure thread-safety
     *
     * @param className
     * @param rootNamespaceName
     * @param sourceFileName
     * @param host
     *
     * @return new instance of code generator
     */
    public abstract RazorCodeGenerator createCodeGenerator(final String className, final String rootNamespaceName,
                                                           final String sourceFileName, final RazorEngineHost host
                                                          );

}
