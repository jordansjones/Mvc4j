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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;

import static nextmethod.base.TypeHelpers.typeAs;

public class DynamicAttributeBlockCodeGenerator extends BlockCodeGenerator {

    private static final String ValueWriterName = "__razor_attribute_value_writer";

    private String oldTargetWriter;
    private boolean isExpression;
    private ExpressionRenderingMode oldRenderingMode;

    private final LocationTagged<String> prefix;
    private final SourceLocation valueStart;

    public DynamicAttributeBlockCodeGenerator(@Nonnull final LocationTagged<String> prefix, final int offset,
                                              final int line, final int col
                                             ) {
        this(prefix, new SourceLocation(offset, line, col));
    }

    public DynamicAttributeBlockCodeGenerator(@Nonnull final LocationTagged<String> prefix,
                                              @Nonnull final SourceLocation valueStart
                                             ) {
        this.prefix = prefix;
        this.valueStart = valueStart;
    }

    @Override
    public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
        if (context.getHost().isDesignTimeMode()) {
            return;
        }

        // Whate kind of block is nested within
        final String generatedCode;
        final Block child = (Block) Iterables.find(target.getChildren(), isBlockPredicate, null);
        if (child != null && child.getType() == BlockType.Expression) {
            isExpression = true;
            generatedCode = context.buildCodeString(
                                                       input -> {
                                                           assert input != null;

                                                           input.writeParameterSeparator();
                                                           input.writeStartMethodInvoke("Tuple.Create");
                                                           input.writeLocationTaggedString(prefix);
                                                           input.writeParameterSeparator();
                                                           input.writeStartMethodInvoke(
                                                                                           "Tuple.Create",
                                                                                           "java.lang.Object",
                                                                                           "java.lang.Integer"
                                                                                       );
                                                       }
                                                   );

            oldRenderingMode = context.getExpressionRenderingMode();
            context.setExpressionRenderingMode(ExpressionRenderingMode.InjectCode);
        }
        else {
            generatedCode = context.buildCodeString(
                                                       input -> {
                                                           assert input != null;

                                                           input.writeParameterSeparator();
                                                           input.writeStartMethodInvoke("Tuple.Create");
                                                           input.writeLocationTaggedString(prefix);
                                                           input.writeParameterSeparator();
                                                           input.writeStartMethodInvoke(
                                                                                           "Tuple.Create",
                                                                                           "java.lang.Object",
                                                                                           "java.lang.Integer"
                                                                                       );
                                                           input.writeStartConstructor(
                                                                                          context.getHost()
                                                                                                 .getGeneratedClassContext()
                                                                                                 .getTemplateTypeName()
                                                                                      );
                                                           input.writeStartLambdaDelegate(ValueWriterName);
                                                       }
                                                   );
        }

        context.markEndOfGeneratedCode();
        context.bufferStatementFragment(generatedCode);

        oldTargetWriter = context.getTargetWriterName();
        context.setTargetWriterName(ValueWriterName);
    }

    @Override
    public void generateEndBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
        if (context.getHost().isDesignTimeMode()) {
            return;
        }

        final String generatedCode;
        if (isExpression) {
            generatedCode = context.buildCodeString(
                                                       input -> {
                                                           assert input != null;

                                                           input.writeParameterSeparator();
                                                           input.writeSnippet(String.valueOf(valueStart.getAbsoluteIndex()));
                                                           input.writeEndMethodInvoke();
                                                           input.writeParameterSeparator();
                                                           // This attribute value is not a literal value, it is dynamically generated
                                                           input.writeBooleanLiteral(false);
                                                           input.writeEndMethodInvoke();
                                                           input.writeLineContinuation();
                                                       }
                                                   );
            context.setExpressionRenderingMode(oldRenderingMode);
        }
        else {
            generatedCode = context.buildCodeString(
                                                       input -> {
                                                           assert input != null;

                                                           input.writeEndLambdaDelegate();
                                                           input.writeEndConstructor();
                                                           input.writeParameterSeparator();
                                                           input.writeSnippet(String.valueOf(valueStart.getAbsoluteIndex()));
                                                           input.writeEndMethodInvoke();
                                                           input.writeParameterSeparator();
                                                           // This attribute value is not a literal value, it is dynamically generated
                                                           input.writeBooleanLiteral(false);
                                                           input.writeEndMethodInvoke();
                                                           input.writeLineContinuation();
                                                       }
                                                   );
        }

        context.addStatement(generatedCode);
        context.setTargetWriterName(oldTargetWriter);
    }

    public LocationTagged<String> getPrefix() {
        return prefix;
    }

    public SourceLocation getValueStart() {
        return valueStart;
    }

    @Override
    public String toString() {
        return String.format("DynAttr:%s", prefix);
    }

    @Override
    public boolean equals(final Object obj) {
        final DynamicAttributeBlockCodeGenerator other = typeAs(obj, DynamicAttributeBlockCodeGenerator.class);
        return other != null
               && Objects.equals(other.prefix, prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix);
    }

    private static final Predicate<SyntaxTreeNode> isBlockPredicate = input -> input != null && input.isBlock();
}
