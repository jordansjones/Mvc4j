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

package nextmethod.web.razor.editor;

import java.util.EnumSet;
import javax.annotation.Nonnull;

import nextmethod.base.Delegates;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

public class SingleLineMarkupEditHandler extends SpanEditHandler {

    public SingleLineMarkupEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer) {
        super(tokenizer);
    }

    public SingleLineMarkupEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer,
                                       @Nonnull final AcceptedCharacters accepted
                                      ) {
        super(tokenizer, accepted);
    }

    public SingleLineMarkupEditHandler(@Nonnull final Delegates.IFunc1<String, Iterable<ISymbol>> tokenizer,
                                       @Nonnull final EnumSet<AcceptedCharacters> accepted
                                      ) {
        super(tokenizer, accepted);
    }
}
