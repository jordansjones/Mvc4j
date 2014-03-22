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

package nextmethod.codedom.compiler;

import java.io.Writer;
import javax.annotation.Nonnull;

import nextmethod.base.NotImplementedException;
import nextmethod.base.Strings;
import nextmethod.codedom.CodeCompileUnit;
import nextmethod.codedom.CodeExpression;

// TODO
public abstract class CodeDomProvider {

    protected String fileExtension = Strings.Empty;
    protected LanguageOptions languageOptions = LanguageOptions.None;

    protected CodeDomProvider() {}

    public String getFileExtension() {
        return fileExtension;
    }

    public LanguageOptions getLanguageOptions() {
        return languageOptions;
    }


    public abstract ICodeCompiler createCompiler();

    public abstract ICodeGenerator createGenerator();

    public ICodeGenerator createGenerator(@Nonnull final String fileName) {
        return createGenerator();
    }

    public ICodeGenerator createGenerator(@Nonnull final Writer output) {
        return createGenerator();
    }

    public ICodeParser createParser() {
        return null;
    }

    public String createEscapedIdentifer(@Nonnull final String val) {
        final ICodeGenerator cg = createGenerator();
        if (cg == null) {
            throw getNotImplementedException();
        }
        return cg.createEscapedIdentifier(val);
    }

    public String createValidIdentifier(@Nonnull final String val) {
        final ICodeGenerator cg = createGenerator();
        if (cg == null) {
            throw getNotImplementedException();
        }
        return cg.createValidIdentifier(val);
    }

    public void generateCodeFromCompileUnit(@Nonnull final CodeCompileUnit compileUnit, @Nonnull final Writer writer,
                                            @Nonnull final CodeGeneratorOptions options
                                           ) {
        final ICodeGenerator cg = createGenerator();
        if (cg == null) {
            throw getNotImplementedException();
        }
        cg.generateCodeFromCompileUnit(compileUnit, writer, options);
    }

    public void generateCodeFromExpression(@Nonnull final CodeExpression expression, @Nonnull final Writer writer,
                                           @Nonnull final CodeGeneratorOptions options
                                          ) {
        final ICodeGenerator cg = createGenerator();
        if (cg == null) {
            throw getNotImplementedException();
        }
        cg.generateCodeFromExpression(expression, writer, options);
    }

    private RuntimeException getNotImplementedException() {
        return new NotImplementedException();
    }

}
