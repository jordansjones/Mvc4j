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
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import nextmethod.web.razor.framework.ISpanConstructor;

/**
 *
 */
public final class BlockExtensions {

    private BlockExtensions() {}

    public static void linkNodes(@Nullable final Block self) {
        if (self == null) return;

        Span first = null;
        Span previous = null;
        for (Span span : self.flatten()) {
            if (first == null) { first = span; }

            span.setPrevious(previous);

            if (previous != null) { previous.setNext(span); }

            previous = span;
        }
    }

    private static final Class<ISpanConstructor> ISpanConstructorClass = ISpanConstructor.class;

    public static Collection<SyntaxTreeNode> buildSpanConstructors(final Collection<SyntaxTreeNode> children) {
        final List<SyntaxTreeNode> built = Lists.newArrayListWithExpectedSize(children.size());
        for (SyntaxTreeNode child : children) {
            if (ISpanConstructorClass.isAssignableFrom(child.getClass())) {
                built.add(ISpanConstructorClass.cast(child).build());
            }
            else {
                built.add(child);
            }
        }
        return built;
    }
}
