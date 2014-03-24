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

public class MarkupCodeGenerator extends SpanCodeGenerator {

    @Override
    public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
        if (!context.getHost().isDesignTimeMode() && Strings.isNullOrEmpty(target.getContent())) {
            return;
        }

        if (context.getHost().isInstrumentationActive()) {
            context.addContextCall(
                target, context.getHost().getGeneratedClassContext().getBeginContextMethodName(),
                true
            );
        }

        if (!Strings.isNullOrEmpty(target.getContent()) && !context.getHost().isDesignTimeMode()) {
            final String code = context.buildCodeString(
                input -> {

                    if (!Strings.isNullOrEmpty(context.getTargetWriterName())) {
                        input.writeStartMethodInvoke(
                            context.getHost()
                                .getGeneratedClassContext()
                                .getWriteLiteralToMethodName()
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
                    input.writeStringLiteral(target.getContent());
                    input.writeEndMethodInvoke();
                    input.writeEndStatement();

                }
            );
            context.addStatement(code);
        }

        if (context.getHost().isInstrumentationActive()) {
            context.addContextCall(
                target, context.getHost().getGeneratedClassContext().getEndContextMethodName(),
                true
            );
        }
    }

    @Override
    public String toString() {
        return "Markup";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MarkupCodeGenerator;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
