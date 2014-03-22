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

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

import nextmethod.codedom.CodeSnippetStatement;
import nextmethod.codedom.CodeTypeReference;
import nextmethod.web.razor.parser.syntaxtree.Span;

import static nextmethod.base.TypeHelpers.typeAs;

public class SetBaseTypeCodeGenerator extends SpanCodeGenerator {

    private String baseType;

    public SetBaseTypeCodeGenerator(@Nonnull final String baseType) {
        this.baseType = baseType;
    }

    @Override
    public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
        context.getGeneratedClass().getBaseTypes().clear();
        context.getGeneratedClass().getBaseTypes().add(new CodeTypeReference(resolveType(context, baseType.trim())));

        if (context.getHost().isDesignTimeMode()) {
            final AtomicInteger generatedCodeStart = new AtomicInteger(0);
            final String code = context.buildCodeString(
                                                           input -> {
                                                               assert input != null;

                                                               generatedCodeStart.set(
                                                                                         input.writeVariableDeclaration(
                                                                                                                           target
                                                                                                                               .getContent(),
                                                                                                                           "__inheritsHelper",
                                                                                                                           null
                                                                                                                       )
                                                                                     );
                                                               input.writeEndStatement();
                                                           }
                                                       );

            int padding = calculatePadding(target, generatedCodeStart.get());
            final CodeSnippetStatement statement = new CodeSnippetStatement(
                                                                               pad(
                                                                                      code, target,
                                                                                      generatedCodeStart.get()
                                                                                  )
            );
            statement.setLinePragma(context.generateLinePragma(target, generatedCodeStart.get() + padding));
            context.addDesignTypeHelperStatement(statement);
        }
    }

    protected String resolveType(@Nonnull final CodeGeneratorContext context, @Nonnull final String baseType) {
        return baseType;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    @Override
    public String toString() {
        return "Base:" + baseType;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        final SetBaseTypeCodeGenerator o = typeAs(obj, SetBaseTypeCodeGenerator.class);
        return o != null && Objects.equals(baseType, o.baseType);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
