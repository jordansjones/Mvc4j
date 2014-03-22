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

package nextmethod.web.razor.utils;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class SimpleMarkupBuilder {

    public static SimpleMarkupBuilder create(final String name) {
        return new SimpleMarkupBuilder(name);
    }

    private final String name;
    private final Map<String, String> attributes;
    private final List<SimpleMarkupBuilder> children;

    private SimpleMarkupBuilder(final String name) {
        this.name = name;
        this.attributes = Maps.newHashMap();
        this.children = Lists.newArrayList();
    }

    public SimpleMarkupBuilder attribute(final String name, final String value) {
        this.attributes.put(name, value);
        return this;
    }

    public SimpleMarkupBuilder addChild(final SimpleMarkupBuilder child) {
        this.children.add(child);
        return this;
    }

    public SimpleMarkupBuilder newChild(final String name) {
        final SimpleMarkupBuilder child = new SimpleMarkupBuilder(name);
        this.children.add(child);
        return child;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("<")
                                     .append(this.name);

        for (String s : attributes.keySet()) {
            sb.append(" ").append(s).append("=\"").append(attributes.get(s)).append("\"");
        }
        sb.append(">");

        for (SimpleMarkupBuilder child : children) {
            sb.append(child.toString());
        }

        return sb.append("</").append(this.name).append(">").toString();
    }
}
