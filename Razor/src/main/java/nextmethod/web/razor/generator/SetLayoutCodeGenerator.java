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

import com.google.common.base.Objects;
import nextmethod.base.Strings;
import nextmethod.codedom.CodeAssignStatement;
import nextmethod.codedom.CodePrimitiveExpression;
import nextmethod.codedom.CodePropertyReferenceExpression;
import nextmethod.web.razor.parser.syntaxtree.Span;

import static nextmethod.base.TypeHelpers.typeAs;

public class SetLayoutCodeGenerator extends SpanCodeGenerator {

    private String layoutPath;

    public SetLayoutCodeGenerator(final String layoutPath) {
        this.layoutPath = layoutPath;
    }

    @Override
    public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
        if (!context.getHost().isDesignTimeMode() &&
            !Strings.isNullOrEmpty(context.getHost().getGeneratedClassContext().getLayoutPropertyName())) {
            context.getTargetMethod().getStatements().add(
                                                             new CodeAssignStatement(
                                                                                        new CodePropertyReferenceExpression(null,
                                                                                                                            context
                                                                                                                                .getHost()
                                                                                                                                .getGeneratedClassContext()
                                                                                                                                .getLayoutPropertyName()
                                                                                        ),
                                                                                        new CodePrimitiveExpression(layoutPath)
                                                             )
                                                         );
        }
    }

    public String getLayoutPath() {
        return layoutPath;
    }

    public void setLayoutPath(final String layoutPath) {
        this.layoutPath = layoutPath;
    }

    @Override
    public String toString() {
        return "Layout: " + layoutPath;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
        final SetLayoutCodeGenerator other = typeAs(obj, SetLayoutCodeGenerator.class);
        return other != null
               && Objects.equal(layoutPath, other.layoutPath);
    }

    @Override
    public int hashCode() {
        return Strings.nullToEmpty(layoutPath).hashCode();
    }
}
