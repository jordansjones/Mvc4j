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

package nextmethod.web.razor.tokenizer;

import java.util.Map;
import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import nextmethod.base.Strings;
import nextmethod.web.razor.tokenizer.symbols.JavaKeyword;

final class JavaKeywordDetector {

    private JavaKeywordDetector() {}

    private static final Map<String, JavaKeyword> keywords = createKeywordsMap();

    public static Optional<JavaKeyword> symbolTypeForIdentifier(@Nullable final String id) {
        if (Strings.isNullOrEmpty(id) || !keywords.containsKey(id)) return Optional.absent();
        return Optional.fromNullable(keywords.get(id));
    }

    private static Map<String, JavaKeyword> createKeywordsMap() {
        final ImmutableMap.Builder<String, JavaKeyword> builder = ImmutableMap.<String, JavaKeyword>builder();

        for (JavaKeyword keyword : JavaKeyword.values()) {
            builder.put(keyword.keyword(), keyword);
        }

        return builder.build();
    }
}
