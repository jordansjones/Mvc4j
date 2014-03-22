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

import nextmethod.codedom.java.JavaCodeProvider;
import nextmethod.web.razor.generator.JavaRazorCodeGenerator;
import nextmethod.web.razor.generator.RazorCodeGenerator;
import nextmethod.web.razor.parser.JavaCodeParser;
import nextmethod.web.razor.parser.ParserBase;

public class JavaRazorCodeLanguage extends RazorCodeLanguage<JavaCodeProvider> {

    public static final String RazorFileExtension = "rzhtml";
    public static final String LanguageFileExtension = "java";
    public static final String LanguageName = "java";


    /**
     * The name of the language (for use in System.Web.Compilation.BuildProvider.GetDefaultCompilerTypeForLanguage)
     *
     * @return the name of the language
     */
    @Override
    public String getLanguageName() {
        return LanguageName;
    }

    /**
     * The type of CodeDOM provider for this language.
     *
     * @return CodeDOM provider type
     */
    @Override
    public Class<JavaCodeProvider> getCodeDomProviderType() {
        return JavaCodeProvider.class;
    }

    /**
     * Constructs the code parser. Must return a new instance on EVERY call to ensure thread-safety
     *
     * @return new instance of code parser
     */
    @Override
    public ParserBase createCodeParser() {
        return new JavaCodeParser();
    }

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
    @Override
    public RazorCodeGenerator createCodeGenerator(final String className, final String rootNamespaceName,
                                                  final String sourceFileName, final RazorEngineHost host
                                                 ) {
        return new JavaRazorCodeGenerator(className, rootNamespaceName, sourceFileName, host);
    }
}
