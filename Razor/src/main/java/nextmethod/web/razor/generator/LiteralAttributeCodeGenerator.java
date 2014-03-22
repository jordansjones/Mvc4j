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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.LocationTagged;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.base.TypeHelpers.typeAs;

public class LiteralAttributeCodeGenerator extends SpanCodeGenerator {

    private final LocationTagged<String> prefix;
    private LocationTagged<String> value;
    private LocationTagged<SpanCodeGenerator> valueGenerator;

    private LiteralAttributeCodeGenerator(@Nonnull final LocationTagged<String> prefix,
                                          @Nullable final LocationTagged<String> value,
                                          @Nullable final LocationTagged<SpanCodeGenerator> valueGenerator
                                         ) {
        this.prefix = checkNotNull(prefix);
        this.value = value;
        this.valueGenerator = valueGenerator;
    }

    @Override
    public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
        final RazorEngineHost host = context.getHost();
        if (host.isDesignTimeMode()) {
            return;
        }

        final ExpressionRenderingMode oldMode = context.getExpressionRenderingMode();
        context.bufferStatementFragment(
                                           context.buildCodeString(
                                                                      input -> {
                                                                          assert input != null;

                                                                          input.writeParameterSeparator();
                                                                          input.writeStartMethodInvoke("nextmethod.base.KeyValue.of");
                                                                          input.writeLocationTaggedString(prefix);
                                                                          input.writeParameterSeparator();
                                                                          if (valueGenerator != null) {
                                                                              input.writeStartMethodInvoke(
                                                                                                              "nextmethod.base.KeyValue.of",
                                                                                                              "java.lang.Object",
                                                                                                              "java.lang.Integer"
                                                                                                          );
                                                                              context.setExpressionRenderingMode(ExpressionRenderingMode.InjectCode);
                                                                          }
                                                                          else {
                                                                              input.writeLocationTaggedString(value);
                                                                              input.writeParameterSeparator();
                                                                              // This attribute value is a literal value
                                                                              input.writeBooleanLiteral(true);
                                                                              input.writeEndMethodInvoke();

                                                                              input.writeLineContinuation();
                                                                          }
                                                                      }
                                                                  )
                                       );

        if (valueGenerator != null) {
            valueGenerator.getValue().generateCode(target, context);
            context.flushBufferedStatement();
            context.setExpressionRenderingMode(oldMode);
            context.addStatement(
                                    context.buildCodeString(
                                                               input -> {
                                                                   assert input != null;

                                                                   input.writeParameterSeparator();
                                                                   input.writeSnippet(
                                                                                         String.valueOf(
                                                                                                           valueGenerator
                                                                                                               .getLocation()
                                                                                                               .getAbsoluteIndex()
                                                                                                       )
                                                                                     );
                                                                   input.writeEndMethodInvoke();
                                                                   input.writeParameterSeparator();
                                                                   // This attribute value is not a literal value, it is dynamically generated
                                                                   input.writeBooleanLiteral(false);
                                                                   input.writeEndMethodInvoke();

                                                                   input.writeLineContinuation();
                                                               }
                                                           )
                                );
        }
        else {
            context.flushBufferedStatement();
        }
    }

    public LocationTagged<String> getPrefix() {
        return prefix;
    }

    public LocationTagged<String> getValue() {
        return value;
    }

    public LocationTagged<SpanCodeGenerator> getValueGenerator() {
        return valueGenerator;
    }

    @Override
    public String toString() {
        if (valueGenerator == null) {
            return String.format("LitAttr:%s,%s", prefix, value);
        }
        return String.format("LitAttr:%s,<Sub:%s>", prefix, valueGenerator);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
        final LiteralAttributeCodeGenerator other = typeAs(obj, LiteralAttributeCodeGenerator.class);
        return other != null
               && prefix.equals(other.prefix)
               && Objects.equals(value, other.value)
               && Objects.equals(valueGenerator, other.valueGenerator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                               prefix,
                               value,
                               valueGenerator
                           );
    }

    public static LiteralAttributeCodeGenerator fromValue(@Nonnull final LocationTagged<String> prefix,
                                                          @Nonnull final LocationTagged<String> value
                                                         ) {
        return new LiteralAttributeCodeGenerator(prefix, value, null);
    }

    public static LiteralAttributeCodeGenerator fromValueGenerator(@Nonnull final LocationTagged<String> prefix,
                                                                   @Nonnull
                                                                   final LocationTagged<SpanCodeGenerator> valueGenerator
                                                                  ) {
        return new LiteralAttributeCodeGenerator(prefix, null, valueGenerator);
    }

}
