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

import javax.annotation.Nonnull;

import nextmethod.base.Strings;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.syntaxtree.Block;

public class RazorCommentCodeGenerator extends BlockCodeGenerator {

    @Override
    public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
        // Flush the buffered statement since we're interrupting it with a comment.
        if (!Strings.isNullOrEmpty(context.getCurrentBufferedStatement())) {
            context.markEndOfGeneratedCode();
            context.bufferStatementFragment(context.buildCodeString(CodeWriter::writeLineContinuation));
        }
        context.flushBufferedStatement();
    }


}
