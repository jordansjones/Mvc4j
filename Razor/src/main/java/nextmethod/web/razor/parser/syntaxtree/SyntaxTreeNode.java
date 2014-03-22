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

import javax.annotation.Nonnull;

import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.text.SourceLocation;

/**
 *
 */
public abstract class SyntaxTreeNode {

    protected Block parent;

    public Block getParent() {
        return this.parent;
    }

    protected void setParent(final Block parent) {
        this.parent = parent;
    }

    /**
     * Returns true if this element is a block (to avoid casting)
     */
    public abstract boolean isBlock();

    /**
     * The length of all the content contained in this node
     */
    public abstract int getLength();

    /**
     * The start point of this node
     */
    public abstract SourceLocation getStart();

    /**
     * Accepts a parser visitor, calling the appropriate visit method and passing in this instance
     *
     * @param visitor The visitor to accept
     */
    public abstract void accept(@Nonnull final ParserVisitor visitor);

    /**
     * Determines if the specified node is equivalent to this node
     *
     * @param node The node to compare this node with
     *
     * @return true if the provided node has all the same content and metadata, though the specific quantity and type of symbols may be different.
     */
    public abstract boolean equivalentTo(@Nonnull final SyntaxTreeNode node);

}
