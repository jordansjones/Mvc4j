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

import java.util.List;

import com.google.common.collect.Lists;
import nextmethod.web.razor.ParserResults;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class ParserVisitorExtensionsTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void visitThrowsOnNullVisitor() {
        ParserVisitor target = null;
        final ParserResults results = new ParserResults(mockBlock(), Lists.<RazorError>newArrayList());

        target.visit(results);
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void visitThrowsOnNullResults() {
        final ParserVisitor target = mockParserVisitor();

        target.visit(null);
    }

    @Test
    public void visitSendsDocumentToVisitor() {
        final ParserVisitor mock = mockParserVisitor();

        final Block root = mockBlock();
        final ParserResults results = new ParserResults(root, Lists.<RazorError>newArrayList());

        mock.visit(results);

        verify(mock).visitBlock(eq(root));
    }

    @Test
    public void visitSendsErrorsToVisitor() {
        final ParserVisitor target = mockParserVisitor();
        final Block root = mockBlock();
        final List<RazorError> errors = Lists.newArrayList(
                                                              new RazorError("Foo", 1, 0, 1),
                                                              new RazorError("Bar", 2, 0, 2)
                                                          );
        final ParserResults results = new ParserResults(root, errors);

        target.visit(results);

        verify(target).visitError(eq(errors.get(0)));
        verify(target).visitError(eq(errors.get(1)));
    }

    @Test
    public void visitCallsOnCompleteWhenAllNodesHaveBeenVisited() {
        final ParserVisitor target = mockParserVisitor();
        final Block root = mockBlock();
        final List<RazorError> errors = Lists.newArrayList(
                                                              new RazorError("Foo", 1, 0, 1),
                                                              new RazorError("Bar", 2, 0, 2)
                                                          );
        final ParserResults results = new ParserResults(root, errors);

        target.visit(results);

        verify(target).onComplete();
    }

    private static ParserVisitor mockParserVisitor() {
        return mock(ParserVisitor.class, CALLS_REAL_METHODS);
    }

    private static Block mockBlock() {
        return new BlockBuilder().setType(BlockType.Comment).build();
    }

}
