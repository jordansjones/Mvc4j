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

import nextmethod.base.Strings;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.text.LocationTagged;

import static nextmethod.base.TypeHelpers.typeAs;

public class AttributeBlockCodeGenerator extends BlockCodeGenerator {

    private final String name;
    private final LocationTagged<String> prefix;
    private final LocationTagged<String> suffix;

    public AttributeBlockCodeGenerator(
        @Nonnull final String name, @Nonnull final LocationTagged<String> prefix,
        @Nonnull final LocationTagged<String> suffix
    ) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
        final RazorEngineHost host = context.getHost();
        if (host.isDesignTimeMode()) {
            return;
        }

        context.flushBufferedStatement();
        context.addStatement(
            context.buildCodeString(
                input -> {
                    if (!Strings.isNullOrEmpty(context.getTargetWriterName())) {
                        input.writeStartMethodInvoke(
                            host.getGeneratedClassContext()
                                .getWriteAttributeToMethodName()
                        );
                        input.writeSnippet(context.getTargetWriterName());
                        input.writeParameterSeparator();
                    }
                    else {
                        input.writeStartMethodInvoke(
                            host.getGeneratedClassContext()
                                .getWriteAttributeMethodName()
                        );
                    }
                    input.writeStringLiteral(name);
                    input.writeParameterSeparator();
                    input.writeLocationTaggedString(prefix);
                    input.writeParameterSeparator();
                    input.writeLocationTaggedString(suffix);
                    input.writeLineContinuation();
                }
            )
        );
    }

    @Override
    public void generateEndBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
        if (context.getHost().isDesignTimeMode()) {
            return;
        }

        context.flushBufferedStatement();
        context.addStatement(
            context.buildCodeString(
                input -> {
                    input.writeEndMethodInvoke();
                    input.writeEndStatement();
                }
            )
        );
    }

    @Override
    public String toString() {
        return String.format("Attr:%s,%s,%s", this.name, this.prefix, this.suffix);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        final AttributeBlockCodeGenerator other = typeAs(obj, AttributeBlockCodeGenerator.class);
        return other != null
               && Objects.equals(other.name, name)
               && Objects.equals(other.prefix, prefix)
               && Objects.equals(other.suffix, suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.name,
            this.prefix,
            this.suffix
        );
    }
}
