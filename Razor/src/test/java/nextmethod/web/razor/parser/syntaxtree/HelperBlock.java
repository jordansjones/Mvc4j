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

package nextmethod.web.razor.parser.syntaxtree;

import java.util.Arrays;
import java.util.Collection;

import nextmethod.web.razor.generator.BlockCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;

public class HelperBlock extends Block {

    private static final BlockType blockType = BlockType.Helper;

    public HelperBlock(final IBlockCodeGenerator codeGenerator, final Collection<SyntaxTreeNode> children) {
        super(blockType, BlockExtensions.buildSpanConstructors(children), codeGenerator);
    }

    public HelperBlock(final IBlockCodeGenerator codeGenerator, final SyntaxTreeNode... nodes) {
        this(codeGenerator, Arrays.asList(nodes));
    }

    public HelperBlock(final SyntaxTreeNode... nodes) {
        this(BlockCodeGenerator.Null, nodes);
    }

    public HelperBlock(final Collection<SyntaxTreeNode> children) {
        this(BlockCodeGenerator.Null, children);
    }

}
