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

import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import nextmethod.web.razor.generator.BlockCodeGenerator;
import nextmethod.web.razor.generator.IBlockCodeGenerator;

/**
 *
 */
public class BlockBuilder {

    private BlockType type;
    private String name;
    private List<SyntaxTreeNode> children;
    private IBlockCodeGenerator codeGenerator;

    public BlockBuilder() {
        reset();
    }

    public BlockBuilder(@Nonnull final Block original) {
        this.type = original.getType();
        this.children = Lists.newArrayList(original.getChildren());
        this.name = original.getName();
        this.codeGenerator = original.getCodeGenerator();
    }

    public Block build() {
        return new Block(this);
    }

    public void reset() {
        this.type = null;
        this.name = null;
        this.children = Lists.newArrayList();
        this.codeGenerator = BlockCodeGenerator.Null;
    }

    public Optional<BlockType> getType() {
        return type != null
               ? Optional.of(type)
               : Optional.<BlockType>absent();
    }

    public BlockBuilder setType(@Nullable final BlockType type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public BlockBuilder setName(@Nullable final String name) {
        this.name = name;
        return this;
    }

    public Collection<SyntaxTreeNode> getChildren() {
        return children;
    }

    public IBlockCodeGenerator getCodeGenerator() {
        return codeGenerator;
    }

    public BlockBuilder setCodeGenerator(@Nullable final IBlockCodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
        return this;
    }

}
