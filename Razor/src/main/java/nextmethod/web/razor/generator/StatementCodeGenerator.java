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

import nextmethod.base.OutParam;
import nextmethod.web.razor.parser.syntaxtree.Span;

import static nextmethod.base.TypeHelpers.typeIs;

public class StatementCodeGenerator extends SpanCodeGenerator {

    @Override
    public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
        context.flushBufferedStatement();
        String generatedCode = context.buildCodeString(input -> input.writeSnippet(target.getContent()));
        final OutParam<Integer> startGeneratedCode = OutParam.of(target.getStart().getCharacterIndex());
        final OutParam<Integer> paddingCharCount = OutParam.of();
        generatedCode = CodeGeneratorPaddingHelper.padStatement(
                                                                   context.getHost(), generatedCode, target,
                                                                   startGeneratedCode, paddingCharCount
                                                               );

        context.addStatement(generatedCode, context.generateLinePragma(target, paddingCharCount.value()));
    }

    @Override
    public String toString() {
        return "Stmt";
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return (obj != null) && typeIs(obj, StatementCodeGenerator.class);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
