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

import javax.annotation.Nonnull;

import nextmethod.base.KeyValue;
import nextmethod.base.Strings;
import nextmethod.codedom.CodeAnnotationArgument;
import nextmethod.codedom.CodeAnnotationDeclaration;
import nextmethod.codedom.CodePrimitiveExpression;
import nextmethod.codedom.CodeTypeReference;
import nextmethod.web.razor.RazorDirectiveAnnotation;
import nextmethod.web.razor.parser.syntaxtree.Span;

import static com.google.common.base.Preconditions.checkArgument;
import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.common.Mvc4jCommonResources.CommonResources;

public class RazorDirectiveAnnotationCodeGenerator extends SpanCodeGenerator {

    private final String name;
    private final String value;

    public RazorDirectiveAnnotationCodeGenerator(@Nonnull final String name, final String value) {
        checkArgument(!Strings.isNullOrEmpty(name), CommonResources().argumentCannotBeNullOrEmpty(), "name");

        this.name = name;
        this.value = Strings.nullToEmpty(value); // Coerce to empty string if it was null
    }

    @Override
    public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
        final CodeTypeReference attributeType = new CodeTypeReference(RazorDirectiveAnnotation.class);
        final CodeAnnotationDeclaration attributeDeclaration = new CodeAnnotationDeclaration(
                                                                                                attributeType,
                                                                                                new CodeAnnotationArgument(new CodePrimitiveExpression(name)),
                                                                                                new CodeAnnotationArgument(new CodePrimitiveExpression(value))
        );
        context.getGeneratedClass().getCustomAnnotations().add(attributeDeclaration);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Directive: " + name + ", Value: " + value;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
        final RazorDirectiveAnnotationCodeGenerator other = typeAs(obj, RazorDirectiveAnnotationCodeGenerator.class);

        return other != null
               && name.equalsIgnoreCase(other.name)
               && value.equalsIgnoreCase(other.value);
    }

    @Override
    public int hashCode() {
        return KeyValue.of(name.toUpperCase(), value.toUpperCase()).hashCode();
    }
}
