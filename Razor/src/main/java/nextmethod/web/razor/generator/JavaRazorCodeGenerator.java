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

import nextmethod.base.Delegates;
import nextmethod.codedom.CodeSnippetTypeMember;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.generator.internal.JavaCodeWriter;

public class JavaRazorCodeGenerator extends RazorCodeGenerator {

    private static final String HiddenLinePragma = "//#line hidden";


    public JavaRazorCodeGenerator(@Nonnull final String className, @Nonnull final String rootPackageName,
                                  @Nonnull final String sourceFileName, @Nonnull final RazorEngineHost host
                                 ) {
        super(className, rootPackageName, sourceFileName, host);
    }

    @Override
    protected Delegates.IFunc<CodeWriter> getCodeWriterFactory() {
        return JavaCodeWriter::new;
    }

    @Override
    protected void initialize(@Nonnull final CodeGeneratorContext context) {
        super.initialize(context);
        context.getGeneratedClass().getMembers().add(0, new CodeSnippetTypeMember(HiddenLinePragma));
    }
}
