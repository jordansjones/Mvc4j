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

package nextmethod.web.razor.parser;

import com.google.common.collect.Iterables;
import nextmethod.web.razor.StringTextBuffer;
import nextmethod.web.razor.framework.SpanFactory;
import nextmethod.web.razor.generator.ExpressionCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.MarkupBlock;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.StatementBlock;
import nextmethod.web.razor.text.TextChange;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 *
 */
public class BlockTest {

    @Test
    public void constructorWithBlockBuilderSetsParent() {
        final BlockBuilder blockBuilder = new BlockBuilder().setType(BlockType.Comment);
        final Span span = new SpanBuilder().setKind(SpanKind.Code).build();

        blockBuilder.getChildren().add(span);

        final Block block = blockBuilder.build();

        assertSame(block, span.getParent());
    }

    @Test
    public void constructorCopiesBasicValuesFromBlockBuilder() {
        final Block block = new BlockBuilder().setName("Foo").setType(BlockType.Helper).build();

        assertEquals("Foo", block.getName());
        assertEquals(BlockType.Helper, block.getType());
    }

    @Test
    public void constructorTransfersInstanceOfCodeGeneratorFromBlockBuilder() {
        final IBlockCodeGenerator expected = new ExpressionCodeGenerator();
        final Block block = new BlockBuilder().setType(BlockType.Helper).setCodeGenerator(expected).build();

        assertSame(expected, block.getCodeGenerator());
    }

    @Test
    public void constructorTransfersChildrenFromBlockBuilder() {
        final Span expected = new SpanBuilder().setKind(SpanKind.Code).build();
        final BlockBuilder blockBuilder = new BlockBuilder().setType(BlockType.Functions);
        blockBuilder.getChildren().add(expected);

        final Block block = blockBuilder.build();

        assertSame(expected, Iterables.getFirst(block.getChildren(), null));
    }

    @Test
    public void locateOwnerReturnsNullIfNoSpanReturnsTrueForOwnsSpan() {
        final SpanFactory factory = SpanFactory.createJavaHtml();
        final Block block = new MarkupBlock(
                                               factory.markup("Foo "),
                                               new StatementBlock(
                                                                     factory.codeTransition(),
                                                                     factory.code("bar").asStatement()
                                               ),
                                               factory.markup(" Baz")
        );

        final TextChange change = new TextChange(
                                                    128, 1, new StringTextBuffer("Foo @bar Baz"), 1,
                                                    new StringTextBuffer("Foo @bor Baz")
        );

        final Span actual = block.locateOwner(change);

        assertNull(actual);
    }

    @Test
    public void locateOwnerReturnsNullIfChangeCrossesMultipleSpans() {
        final SpanFactory factory = SpanFactory.createJavaHtml();
        final Block block = new MarkupBlock(
                                               factory.markup("Foo "),
                                               new StatementBlock(
                                                                     factory.codeTransition(),
                                                                     factory.code("bar").asStatement()
                                               ),
                                               factory.markup(" Baz")
        );

        final TextChange change = new TextChange(
                                                    4, 10, new StringTextBuffer("Foo @bar Baz"), 10,
                                                    new StringTextBuffer("Foo @bor Baz")
        );

        final Span actual = block.locateOwner(change);

        assertNull(actual);
    }

}
