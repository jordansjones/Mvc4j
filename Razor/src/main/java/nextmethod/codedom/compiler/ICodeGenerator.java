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

import nextmethod.codedom.CodeCompileUnit;
import nextmethod.codedom.CodeExpression;
import nextmethod.codedom.CodePackage;
import nextmethod.codedom.CodeStatement;
import nextmethod.codedom.CodeTypeDeclaration;
import nextmethod.codedom.CodeTypeReference;

/**
 *
 */
public interface ICodeGenerator {

    String createEscapedIdentifier(final String value);

    String createValidIdentifier(final String value);

    void generateCodeFromCompileUnit(@Nonnull final CodeCompileUnit compileUnit, @Nonnull final Writer writer,
                                     @Nonnull final CodeGeneratorOptions options
                                    );

    void generateCodeFromExpression(@Nonnull final CodeExpression codeExpression, @Nonnull final Writer writer,
                                    @Nonnull final CodeGeneratorOptions options
                                   );

    void generateCodeFromPackage(@Nonnull final CodePackage codePackage, @Nonnull final Writer writer,
                                 @Nonnull final CodeGeneratorOptions options
                                );

    void generateCodeFromStatement(@Nonnull final CodeStatement codeStatement, @Nonnull final Writer writer,
                                   @Nonnull final CodeGeneratorOptions options
                                  );

    void generateCodeFromType(@Nonnull final CodeTypeDeclaration codeTypeDeclaration, @Nonnull final Writer writer,
                              @Nonnull final CodeGeneratorOptions options
                             );

    String getTypeOutput(@Nonnull final CodeTypeReference type);

    boolean isValidIdentifier(@Nonnull final String value);

    boolean supports(@Nonnull final GeneratorSupport supports);

    void validateIdentifier(@Nonnull final String value);
}
