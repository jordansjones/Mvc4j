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
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;

import static nextmethod.base.TypeHelpers.typeIs;

public class ExpressionCodeGenerator extends HybridCodeGenerator {

    @Override
    public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
        if (context.getHost().isInstrumentationActive() &&
            context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
            final Span contentSpan = getFirstOrDefaultSpan(target);
            if (contentSpan != null) {
                context.addContextCall(
                                          contentSpan,
                                          context.getHost().getGeneratedClassContext().getBeginContextMethodName(),
                                          false
                                      );
            }
        }

        final String writeInvocation = context.buildCodeString(
                                                                  input -> {
                                                                      if (context.getHost().isDesignTimeMode()) {
                                                                          context.ensureExpressionHelperVariable();
                                                                          input.writeStartAssigment("__o");
                                                                      }
                                                                      else if (context.getExpressionRenderingMode() ==
                                                                               ExpressionRenderingMode.WriteToOutput) {
                                                                          if (!Strings.isNullOrEmpty(context.getTargetWriterName())) {
                                                                              input.writeStartMethodInvoke(
                                                                                                              context.getHost()
                                                                                                                     .getGeneratedClassContext()
                                                                                                                     .getWriteToMethodName()
                                                                                                          );
                                                                              input.writeSnippet(context.getTargetWriterName());
                                                                              input.writeParameterSeparator();
                                                                          }
                                                                          else {
                                                                              input.writeStartMethodInvoke(
                                                                                                              context.getHost()
                                                                                                                     .getGeneratedClassContext()
                                                                                                                     .getWriteMethodName()
                                                                                                          );
                                                                          }
                                                                      }
                                                                  }
                                                              );

        context.bufferStatementFragment(writeInvocation);
        context.markStartOfGeneratedCode();
    }

    @Override
    public void generateEndBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
        final String endBlock = context.buildCodeString(
                                                           input -> {
                                                               if (context.getExpressionRenderingMode() ==
                                                                   ExpressionRenderingMode.WriteToOutput) {
                                                                   if (!context.getHost().isDesignTimeMode()) {
                                                                       input.writeEndMethodInvoke();
                                                                   }
                                                                   input.writeEndStatement();
                                                               }
                                                               else {
                                                                   input.writeLineContinuation();
                                                               }
                                                           }
                                                       );

        context.markEndOfGeneratedCode();
        context.bufferStatementFragment(endBlock);
        context.flushBufferedStatement();

        if (context.getHost().isInstrumentationActive() &&
            context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
            final Span contentSpan = getFirstOrDefaultSpan(target);
            if (contentSpan != null) {
                context.addContextCall(
                                          contentSpan,
                                          context.getHost().getGeneratedClassContext().getEndContextMethodName(), false
                                      );
            }
        }
    }

    @Override
    public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
        Span sourceSpan = null;
        if (context.createCodeWriter().supportsMidStatementLinePragmas() ||
            context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
            sourceSpan = target;
        }
        context.bufferStatementFragment(target.getContent(), sourceSpan);
    }

    @Override
    public String toString() {
        return "Expr";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return obj != null && typeIs(obj, ExpressionCodeGenerator.class);
    }

    private static Span getFirstOrDefaultSpan(final Block target) {
        return (Span) target.getChildren()
            .stream()
            .filter(ExpressionCodeGenerator::isSpanCodeOrMarkup)
            .findFirst()
            .orElse(null);
    }

    private static boolean isSpanCodeOrMarkup(SyntaxTreeNode input) {
        if (input == null || !typeIs(input, Span.class)) return false;
        final Span span = Span.class.cast(input);
        final SpanKind kind = span.getKind();
        return kind == SpanKind.Code || kind == SpanKind.Markup;
    }

}
