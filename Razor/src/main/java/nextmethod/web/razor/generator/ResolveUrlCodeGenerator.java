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

import nextmethod.base.Strings;
import nextmethod.web.razor.parser.syntaxtree.Span;

import static nextmethod.base.TypeHelpers.typeIs;

public class ResolveUrlCodeGenerator extends SpanCodeGenerator {

    @Override
    public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
        // Check if the host supports it
        if (Strings.isNullOrEmpty(context.getHost().getGeneratedClassContext().getResolveUrlMethodName())) {
            // Nope, just use the default MarkupCodeGenerator behavior
            new MarkupCodeGenerator().generateCode(target, context);
            return;
        }

        if (!context.getHost().isDesignTimeMode() && Strings.isNullOrEmpty(target.getContent())) {
            return;
        }

        if (context.getHost().isInstrumentationActive() &&
            context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
            // Add a non-literal context call (non-literal because the expanded URL will not match the source character-by-character)
            context.addContextCall(
                target, context.getHost().getGeneratedClassContext().getBeginContextMethodName(),
                false
            );
        }

        if (!Strings.isNullOrEmpty(target.getContent()) && !context.getHost().isDesignTimeMode()) {
            final String code = context.buildCodeString(
                input -> {
                    assert input != null;

                    if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
                        if (!Strings.isNullOrEmpty(context.getTargetWriterName())) {
                            input.writeStartMethodInvoke(
                                context.getHost()
                                    .getGeneratedClassContext()
                                    .getWriteLiteralMethodName()
                            );
                            input.writeSnippet(context.getTargetWriterName());
                            input.writeParameterSeparator();
                        }
                        else {
                            input.writeStartMethodInvoke(
                                context.getHost()
                                    .getGeneratedClassContext()
                                    .getWriteLiteralMethodName()
                            );
                        }
                    }
                    input.writeStartMethodInvoke(
                        context.getHost()
                            .getGeneratedClassContext()
                            .getResolveUrlMethodName()
                    );
                    input.writeStringLiteral(target.getContent());
                    input.writeEndMethodInvoke();

                    if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
                        input.writeEndMethodInvoke();
                        input.writeEndStatement();
                    }
                    else {
                        input.writeLineContinuation();
                    }
                }
            );

            if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
                context.addStatement(code);
            }
            else {
                context.bufferStatementFragment(code);
            }
        }

        if (context.getHost().isInstrumentationActive() &&
            context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
            context.addContextCall(
                target, context.getHost().getGeneratedClassContext().getEndContextMethodName(),
                false
            );
        }
    }

    @Override
    public String toString() {
        return "VirtualPath";
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
        return typeIs(obj, ResolveUrlCodeGenerator.class);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
