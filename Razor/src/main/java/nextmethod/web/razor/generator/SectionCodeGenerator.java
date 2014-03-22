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

import nextmethod.web.razor.parser.syntaxtree.Block;

import static nextmethod.base.TypeHelpers.typeAs;

public class SectionCodeGenerator extends BlockCodeGenerator {

    private final String sectionName;

    public SectionCodeGenerator(@Nonnull final String sectionName) {
        this.sectionName = sectionName;
    }

    @Override
    public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
        final String startBlock = context.buildCodeString(
                                                             input -> {
                                                                 assert input != null;

                                                                 input.writeStartMethodInvoke(
                                                                                                 context.getHost()
                                                                                                        .getGeneratedClassContext()
                                                                                                        .getDefineSectionMethodName()
                                                                                             );
                                                                 input.writeStringLiteral(sectionName);
                                                                 input.writeParameterSeparator();
                                                                 input.writeStartLambdaDelegate();
                                                             }
                                                         );

        context.addStatement(startBlock);
    }

    @Override
    public void generateEndBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
        final String block = context.buildCodeString(
                                                        input -> {
                                                            assert input != null;

                                                            input.writeEndLambdaDelegate();
                                                            input.writeEndMethodInvoke();
                                                            input.writeEndStatement();
                                                        }
                                                    );

        context.addStatement(block);
    }

    public String getSectionName() {
        return sectionName;
    }

    @Override
    public String toString() {
        return "Section:" + sectionName;
    }

    @Override
    public boolean equals(final Object obj) {
        final SectionCodeGenerator other = typeAs(obj, SectionCodeGenerator.class);
        return other != null
               && super.equals(other)
               && sectionName.equals(other.sectionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                               super.hashCode(),
                               sectionName
                           );
    }
}
